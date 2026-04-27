package com.harebusiness.form.service;

import com.harebusiness.form.constants.ExceptionMessageConstant;
import com.harebusiness.form.constants.ResponseMessageConstant;
import com.harebusiness.form.dtos.request.AddQuestionRequestDto;
import com.harebusiness.form.dtos.response.AddQuestionResponseDto;
import com.harebusiness.form.dtos.response.RemoveQuestionResponseDto;
import com.harebusiness.form.enums.ChoiceType;
import com.harebusiness.form.exceptions.ForbiddenAccessException;
import com.harebusiness.form.exceptions.ResourceNotFoundException;
import com.harebusiness.form.models.Form;
import com.harebusiness.form.models.Question;
import com.harebusiness.form.models.User;
import com.harebusiness.form.repositories.FormRepository;
import com.harebusiness.form.repositories.QuestionRepository;
import com.harebusiness.form.services.QuestionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceImplTest {

    @Mock
    private FormRepository formRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionServiceImpl questionService;

    private User owner;
    private Form form;
    private Question question;
    private String slug = "test-slug";
    private final Long questionId = 100L;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);

        form = new Form();
        form.setId(10L);
        form.setSlug(slug);
        form.setCreator(owner);

        question = new Question();
        question.setId(questionId);
        question.setForm(form);
    }

    @Test
    void addQuestion_withChoices_success() {
        AddQuestionRequestDto request = new AddQuestionRequestDto();
        request.setName("Favorite Framework");
        request.setChoiceType(ChoiceType.MULTIPLE_CHOICE.getValue());
        request.setChoices(List.of("React", "Vue", "Angular"));
        request.setRequired(true);

        when(formRepository.findBySlug(slug)).thenReturn(Optional.of(form));
        when(questionRepository.save(any(Question.class))).thenAnswer(invocation -> {
            Question q = invocation.getArgument(0);
            q.setId(100L);
            return q;
        });

        AddQuestionResponseDto response = questionService.addQuestion(slug, request, owner);

        assertNotNull(response);
        assertEquals(ResponseMessageConstant.ADD_QUESTION_SUCCESS_MESSAGE, response.getMessage());
        assertEquals("Favorite Framework", response.getQuestion().getName());
        assertEquals("multiple choice", response.getQuestion().getChoiceType());
        assertEquals("React,Vue,Angular", response.getQuestion().getChoices());
        assertEquals(10L, response.getQuestion().getFormId());
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    void addQuestion_noChoicesRequired_success() {
        AddQuestionRequestDto request = new AddQuestionRequestDto();
        request.setName("Wawa?");
        request.setChoiceType(ChoiceType.PARAGRAPH.getValue());
        request.setChoices(null);
        request.setRequired(false);

        when(formRepository.findBySlug(slug)).thenReturn(Optional.of(form));
        when(questionRepository.save(any(Question.class))).thenAnswer(i -> {
            Question q = i.getArgument(0);
            q.setId(101L);
            return q;
        });

        AddQuestionResponseDto response = questionService.addQuestion(slug, request, owner);

        assertNull(response.getQuestion().getChoices());
        assertEquals("paragraph", response.getQuestion().getChoiceType());
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    void addQuestion_whenFormNotFound_shouldThrowsException() {
        when(formRepository.findBySlug("invalid")).thenReturn(Optional.empty());
        AddQuestionRequestDto request = new AddQuestionRequestDto();

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                questionService.addQuestion("invalid", request, owner)
        );
        assertEquals(ExceptionMessageConstant.FORM_NOT_FOUND_MESSAGE, ex.getMessage());
    }

    @Test
    void addQuestion_whenForbiddenAccess_shouldThrowsException() {
        User sus = new User();
        sus.setId(99L);

        when(formRepository.findBySlug(slug)).thenReturn(Optional.of(form));
        AddQuestionRequestDto request = new AddQuestionRequestDto();

        ForbiddenAccessException ex = assertThrows(ForbiddenAccessException.class, () ->
                questionService.addQuestion(slug, request, sus)
        );

        assertEquals(ExceptionMessageConstant.FORBIDDEN_ACCESS_MESSAGE, ex.getMessage());
        verify(questionRepository, never()).save(any());
    }

    @Test
    void removeQuestion_success() {
        when(formRepository.findBySlug(slug)).thenReturn(Optional.of(form));
        when(questionRepository.findByIdAndFormId(questionId, form.getId())).thenReturn(Optional.of(question));

        RemoveQuestionResponseDto response = questionService.removeQuestion(slug, questionId, owner);

        assertNotNull(response);
        assertEquals(ResponseMessageConstant.REMOVE_QUESTION_SUCCESS_MESSAGE, response.getMessage());
        verify(questionRepository, times(1)).delete(question);
    }

    @Test
    void removeQuestion_whenFormNotFound_shouldThrowsException() {
        when(formRepository.findBySlug("invalid-slug")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                questionService.removeQuestion("invalid-slug", questionId, owner)
        );

        assertEquals(ExceptionMessageConstant.FORM_NOT_FOUND_MESSAGE, ex.getMessage());
        verify(questionRepository, never()).delete(any());
    }

    @Test
    void removeQuestion_whenForbiddenAccess_shouldThrowsException() {
        User sus = new User();
        sus.setId(2L);

        when(formRepository.findBySlug(slug)).thenReturn(Optional.of(form));

        ForbiddenAccessException ex = assertThrows(ForbiddenAccessException.class, () ->
                questionService.removeQuestion(slug, questionId, sus)
        );

        assertEquals(ExceptionMessageConstant.FORBIDDEN_ACCESS_MESSAGE, ex.getMessage());
        verify(questionRepository, never()).delete(any());
    }

    @Test
    void removeQuestion_QuestionNotFound_ThrowsException() {
        when(formRepository.findBySlug(slug)).thenReturn(Optional.of(form));
        when(questionRepository.findByIdAndFormId(questionId, form.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                questionService.removeQuestion(slug, questionId, owner)
        );

        assertEquals(ExceptionMessageConstant.QUESTION_NOT_FOUND_MESSAGE, ex.getMessage());
        verify(questionRepository, never()).delete(any());
    }
}
