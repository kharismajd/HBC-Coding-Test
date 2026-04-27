package com.harebusiness.form.services;

import com.harebusiness.form.constants.ExceptionMessageConstant;
import com.harebusiness.form.constants.ResponseMessageConstant;
import com.harebusiness.form.dtos.request.AddQuestionRequestDto;
import com.harebusiness.form.dtos.response.AddQuestionResponseDto;
import com.harebusiness.form.exceptions.ForbiddenAccessException;
import com.harebusiness.form.exceptions.ResourceNotFoundException;
import com.harebusiness.form.models.Form;
import com.harebusiness.form.models.Question;
import com.harebusiness.form.models.User;
import com.harebusiness.form.repositories.FormRepository;
import com.harebusiness.form.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Transactional
    public AddQuestionResponseDto addQuestion(String slug, AddQuestionRequestDto request, User user) {
        Form form = formRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessageConstant.FORM_NOT_FOUND_MESSAGE));

        if (!form.getCreator().getId().equals(user.getId())) {
            throw new ForbiddenAccessException(ExceptionMessageConstant.FORBIDDEN_ACCESS_MESSAGE);
        }

        Question question = new Question();
        question.setName(request.getName());
        question.setChoiceType(request.getChoiceType());
        question.setRequired(request.isRequired());
        question.setForm(form);

        boolean requiresChoices = switch (question.getChoiceType()) {
            case MULTIPLE_CHOICE, DROPDOWN, CHECKBOXES -> true;
            default -> false;
        };
        if (requiresChoices && request.getChoices() != null && !request.getChoices().isEmpty()) {
            question.setChoices(String.join(",", request.getChoices()));
        }

        Question savedQuestion = questionRepository.save(question);

        AddQuestionResponseDto.QuestionData questionData = new AddQuestionResponseDto.QuestionData();
        questionData.setId(savedQuestion.getId());
        questionData.setFormId(savedQuestion.getForm().getId());
        questionData.setName(savedQuestion.getName());
        questionData.setChoiceType(savedQuestion.getChoiceType().getValue());
        questionData.setRequired(savedQuestion.isRequired());
        questionData.setChoices(savedQuestion.getChoices());

        AddQuestionResponseDto response = new AddQuestionResponseDto();
        response.setMessage(ResponseMessageConstant.ADD_QUESTION_SUCCESS_MESSAGE);
        response.setQuestion(questionData);

        return response;
    }
}
