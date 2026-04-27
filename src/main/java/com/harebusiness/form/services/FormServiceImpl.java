package com.harebusiness.form.services;

import com.harebusiness.form.constants.ExceptionMessageConstant;
import com.harebusiness.form.constants.ResponseMessageConstant;
import com.harebusiness.form.dtos.request.CreateFormRequestDto;
import com.harebusiness.form.dtos.response.CreateFormResponseDto;
import com.harebusiness.form.dtos.response.GetAllFormsResponseDto;
import com.harebusiness.form.dtos.response.GetFormDetailResponseDto;
import com.harebusiness.form.exceptions.ForbiddenAccessException;
import com.harebusiness.form.exceptions.ResourceNotFoundException;
import com.harebusiness.form.models.AllowedDomain;
import com.harebusiness.form.models.Form;
import com.harebusiness.form.models.User;
import com.harebusiness.form.repositories.AllowedDomainRepository;
import com.harebusiness.form.repositories.FormRepository;
import com.harebusiness.form.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FormServiceImpl implements FormService {

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private AllowedDomainRepository allowedDomainRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public CreateFormResponseDto createForm(CreateFormRequestDto request, User user) {
        Form form = new Form();
        form.setName(request.getName());
        form.setSlug(request.getSlug());
        form.setDescription(request.getDescription());
        form.setLimitOneResponse(request.isLimitOneResponse());
        form.setCreator(user);
        Form savedForm = formRepository.save(form);

        List<AllowedDomain> allowedDomains = new ArrayList<>();
        for (String domain : request.getAllowedDomains()) {
            AllowedDomain allowedDomain = new AllowedDomain();
            String trimmedDomain = domain.replaceAll("\\s+", "");
            if (!trimmedDomain.isEmpty()) {
                allowedDomain.setDomain(trimmedDomain);
                allowedDomain.setForm(form);
                allowedDomains.add(allowedDomain);
            }
        }
        allowedDomainRepository.saveAll(allowedDomains);

        CreateFormResponseDto createFormResponseDto = new CreateFormResponseDto();
        createFormResponseDto.setMessage(ResponseMessageConstant.CREATE_FORM_SUCCESS_MESSAGE);

        CreateFormResponseDto.FormDataResponse responseForm = new CreateFormResponseDto.FormDataResponse();
        responseForm.setId(savedForm.getId());
        responseForm.setName(savedForm.getName());
        responseForm.setSlug(savedForm.getSlug());
        responseForm.setDescription(savedForm.getDescription());
        responseForm.setLimitOneResponse(savedForm.isLimitOneResponse());
        responseForm.setCreatorId(user.getId());
        createFormResponseDto.setForm(responseForm);

        return createFormResponseDto;
    }

    @Transactional(readOnly = true)
    public GetAllFormsResponseDto getAllForms(User user) {
        List<Form> forms = formRepository.findAllByCreatorIdOrderByIdDesc(user.getId());

        List<GetAllFormsResponseDto.FormDataResponse> formDtos = forms.stream()
                .map(form -> {
                    GetAllFormsResponseDto.FormDataResponse dto = new GetAllFormsResponseDto.FormDataResponse();
                    dto.setId(form.getId());
                    dto.setName(form.getName());
                    dto.setSlug(form.getSlug());
                    dto.setDescription(form.getDescription());

                    dto.setLimitOneResponse(form.isLimitOneResponse() ? 1 : 0);

                    dto.setCreatorId(form.getCreator().getId());
                    return dto;
                }).toList();

        GetAllFormsResponseDto response = new GetAllFormsResponseDto();
        response.setMessage(ResponseMessageConstant.GET_ALL_FORMS_SUCCESS_MESSAGE);
        response.setForms(formDtos);

        return response;
    }

    @Transactional(readOnly = true)
    public GetFormDetailResponseDto getFormDetail(String slug, User user) {
        Form form = formRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessageConstant.FORM_NOT_FOUND_MESSAGE));

        List<String> allowedDomainList = form.getAllowedDomains().stream()
                .map(AllowedDomain::getDomain)
                .toList();
        String userEmail = user.getEmail();
        String userDomain = userEmail.substring(userEmail.indexOf("@") + 1);
        if (!allowedDomainList.isEmpty() && !allowedDomainList.contains(userDomain)) {
            throw new ForbiddenAccessException(ExceptionMessageConstant.FORBIDDEN_ACCESS_MESSAGE);
        }

        List<GetFormDetailResponseDto.QuestionResponseDto> questionDtos = form.getQuestions().stream()
                .map(question -> {
                    GetFormDetailResponseDto.QuestionResponseDto qDto = new GetFormDetailResponseDto.QuestionResponseDto();
                    qDto.setId(question.getId());
                    qDto.setFormId(form.getId());
                    qDto.setName(question.getName());
                    qDto.setChoiceType(question.getChoiceType().getValue());
                    qDto.setChoices(question.getChoices());
                    qDto.setIsRequired(question.isRequired() ? 1 : 0);
                    return qDto;
                }).toList();

        GetFormDetailResponseDto.FormDetailData formDto = new GetFormDetailResponseDto.FormDetailData();
        formDto.setId(form.getId());
        formDto.setName(form.getName());
        formDto.setSlug(form.getSlug());
        formDto.setDescription(form.getDescription());
        formDto.setLimitOneResponse(form.isLimitOneResponse() ? 1 : 0);
        formDto.setCreatorId(form.getCreator().getId());
        formDto.setAllowedDomains(allowedDomainList);
        formDto.setQuestions(questionDtos);

        GetFormDetailResponseDto response = new GetFormDetailResponseDto();
        response.setMessage(ResponseMessageConstant.GET_FORM_SUCCESS_MESSAGE);
        response.setForm(formDto);

        return response;
    }
}
