package com.harebusiness.form.services;

import com.harebusiness.form.constants.ExceptionMessageConstant;
import com.harebusiness.form.constants.ResponseMessageConstant;
import com.harebusiness.form.dtos.request.SubmitResponseRequestDto;
import com.harebusiness.form.dtos.response.GetAllResponsesDto;
import com.harebusiness.form.dtos.response.SubmitResponseResponseDto;
import com.harebusiness.form.enums.ChoiceType;
import com.harebusiness.form.exceptions.ForbiddenAccessException;
import com.harebusiness.form.exceptions.OneResponseLimitException;
import com.harebusiness.form.exceptions.ResourceNotFoundException;
import com.harebusiness.form.exceptions.ValidationFieldException;
import com.harebusiness.form.models.AllowedDomain;
import com.harebusiness.form.models.Answer;
import com.harebusiness.form.models.Form;
import com.harebusiness.form.models.Question;
import com.harebusiness.form.models.Response;
import com.harebusiness.form.models.User;
import com.harebusiness.form.repositories.FormRepository;
import com.harebusiness.form.repositories.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ResponseServiceImpl implements ResponseService {

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Transactional
    public SubmitResponseResponseDto submitResponse(String slug, SubmitResponseRequestDto request, User user) {
        Form form = formRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessageConstant.FORM_NOT_FOUND_MESSAGE));

        List<String> allowedDomains = form.getAllowedDomains().stream()
                .map(AllowedDomain::getDomain)
                .toList();

        if (!allowedDomains.isEmpty()) {
            String userDomain = user.getEmail().substring(user.getEmail().indexOf("@") + 1);
            if (!allowedDomains.contains(userDomain)) {
                throw new ForbiddenAccessException(ExceptionMessageConstant.FORBIDDEN_ACCESS_MESSAGE);
            }
        }

        if (form.isLimitOneResponse() && responseRepository.existsByFormAndUser(form, user)) {
            throw new OneResponseLimitException(ExceptionMessageConstant.ONE_RESPONSE_LIMIT_MESSAGE);
        }

        Map<Long, String> requestAnswerMap = request.getAnswers().stream()
                .collect(Collectors.toMap(SubmitResponseRequestDto.AnswerItem::getQuestionId,
                        SubmitResponseRequestDto.AnswerItem::getValue));

        Response response = new Response();
        response.setForm(form);
        response.setUser(user);

        List<Answer> answersToSave = new ArrayList<>();
        List<String> errors  = new ArrayList<>();
        for (Question question : form.getQuestions()) {
            String value = requestAnswerMap.get(question.getId());

            if (question.isRequired() && (value == null || value.trim().isEmpty())) {
                errors.add("The answer value for question with id: " + question.getId() + " is required");
                continue;
            }

            validateValueFormat(question.getChoiceType(), value, errors);
            validateChoiceOptions(question, value, errors);

            if (value != null && !value.trim().isEmpty()) {
                Answer answer = new Answer();
                answer.setQuestion(question);
                answer.setValue(value);
                answer.setResponse(response);
                answersToSave.add(answer);
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationFieldException(Map.of("answers", errors));
        }

        response.setAnswers(answersToSave);
        responseRepository.save(response);

        return new SubmitResponseResponseDto(ResponseMessageConstant.SUBMIT_RESPONSE_SUCCESS_MESSAGE);
    }

    @Transactional(readOnly = true)
    public GetAllResponsesDto getAllResponses(String slug, User user) {
        Form form = formRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessageConstant.FORM_NOT_FOUND_MESSAGE));

        if (!form.getCreator().getId().equals(user.getId())) {
            throw new ForbiddenAccessException(ExceptionMessageConstant.FORBIDDEN_ACCESS_MESSAGE);
        }

        List<Response> responses = responseRepository.findAllByFormId(form.getId());

        List<GetAllResponsesDto.ResponseData> responseDataList = responses.stream().map(r -> {
            GetAllResponsesDto.ResponseData dto = new GetAllResponsesDto.ResponseData();
            LocalDateTime localResponseDate = null;
            if (!Objects.isNull(r.getDate())) {
                localResponseDate = r.getDate().atZoneSameInstant(ZoneId.of("Asia/Makassar")).toLocalDateTime();
            }
            dto.setDate(localResponseDate);

            GetAllResponsesDto.UserDto userDto = new GetAllResponsesDto.UserDto();
            userDto.setId(r.getUser().getId());
            userDto.setName(r.getUser().getName());
            userDto.setEmail(r.getUser().getEmail());

            LocalDateTime localEmailVerifiedAt = null;
            if (!Objects.isNull(r.getUser().getEmailVerifiedAt())) {
                localEmailVerifiedAt = r.getUser().getEmailVerifiedAt().atZoneSameInstant(ZoneId.of("Asia/Makassar")).toLocalDateTime();
            }
            userDto.setEmailVerifiedAt(localEmailVerifiedAt);
            dto.setUser(userDto);

            Map<String, String> answerMap = new LinkedHashMap<>();
            for (Answer a : r.getAnswers()) {
                answerMap.put(a.getQuestion().getName(), a.getValue());
            }
            dto.setAnswers(answerMap);

            return dto;
        }).toList();

        GetAllResponsesDto getAllResponsesDto = new GetAllResponsesDto();
        getAllResponsesDto.setMessage(ResponseMessageConstant.GET_RESPONSE_SUCCESS_MESSAGE);
        getAllResponsesDto.setResponses(responseDataList);

        return getAllResponsesDto;
    }

    private void validateValueFormat(ChoiceType type, String value, List<String> errors) {
        if (value == null || value.trim().isEmpty()) return;

        try {
            switch (type) {
                case DATE -> java.time.LocalDate.parse(value);
                case TIME -> java.time.LocalTime.parse(value);
            }
        } catch (java.time.format.DateTimeParseException e) {
            errors.add("The answers field must be a valid " + type.getValue() + " format.");
        }
    }

    private void validateChoiceOptions(Question question, String userValue, List<String> errors) {
        if (userValue == null || userValue.trim().isEmpty()) return;

        String rawChoices = question.getChoices();
        if (rawChoices == null || rawChoices.isEmpty()) return;

        List<String> validOptions = List.of(rawChoices.split(","));
        switch (question.getChoiceType()) {
            case MULTIPLE_CHOICE, DROPDOWN -> {
                if (userValue.contains(",")) {
                    errors.add("Only one choice is permitted for multiple choices or dropdown.");
                }
                else if (!validOptions.contains(userValue)) {
                    errors.add("The selected option is not a valid choice.");
                }
            }
            case CHECKBOXES -> {
                List<String> userSelections = List.of(userValue.split(","));
                for (String selection : userSelections) {
                    if (!validOptions.contains(selection.trim())) {
                        errors.add("One or more selected checkboxes are invalid.");
                    }
                }
            }
            default -> {}
        }
    }
}
