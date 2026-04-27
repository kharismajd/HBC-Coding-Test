package com.harebusiness.form.service;

import com.harebusiness.form.constants.ExceptionMessageConstant;
import com.harebusiness.form.constants.ResponseMessageConstant;
import com.harebusiness.form.dtos.request.SubmitResponseRequestDto;
import com.harebusiness.form.dtos.response.SubmitResponseResponseDto;
import com.harebusiness.form.enums.ChoiceType;
import com.harebusiness.form.exceptions.ForbiddenAccessException;
import com.harebusiness.form.exceptions.OneResponseLimitException;
import com.harebusiness.form.exceptions.ResourceNotFoundException;
import com.harebusiness.form.exceptions.ValidationFieldException;
import com.harebusiness.form.models.AllowedDomain;
import com.harebusiness.form.models.Form;
import com.harebusiness.form.models.Question;
import com.harebusiness.form.models.Response;
import com.harebusiness.form.models.User;
import com.harebusiness.form.repositories.FormRepository;
import com.harebusiness.form.repositories.ResponseRepository;
import com.harebusiness.form.services.ResponseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResponseServiceImplTest {

    @Mock
    private FormRepository formRepository;

    @Mock
    private ResponseRepository responseRepository;

    @InjectMocks
    private ResponseServiceImpl responseService;

    private User user;
    private Form form;
    private final String slug = "test-slug";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user1@webtech.id");

        form = new Form();
        form.setId(10L);
        form.setSlug(slug);
        form.setLimitOneResponse(false);
        form.setAllowedDomains(new ArrayList<>());
        form.setQuestions(new ArrayList<>());
    }

    private SubmitResponseRequestDto.AnswerItem createAnswerItem(Long questionId, String value) {
        SubmitResponseRequestDto.AnswerItem item = new SubmitResponseRequestDto.AnswerItem();
        item.setQuestionId(questionId);
        item.setValue(value);
        return item;
    }

    private Question createQuestion(Long id, ChoiceType type, boolean required, String choices) {
        Question q = new Question();
        q.setId(id);
        q.setChoiceType(type);
        q.setRequired(required);
        q.setChoices(choices);
        return q;
    }

    @Test
    void submitResponse_success() {
        form.getQuestions().add(createQuestion(1L, ChoiceType.SHORT_ANSWER, true, null));
        form.getQuestions().add(createQuestion(2L, ChoiceType.DATE, true, null));
        form.getQuestions().add(createQuestion(3L, ChoiceType.MULTIPLE_CHOICE, true, "A,B,C"));

        SubmitResponseRequestDto request = new SubmitResponseRequestDto();
        request.setAnswers(List.of(
                createAnswerItem(1L, "My Answer"),
                createAnswerItem(2L, "2023-10-24"),
                createAnswerItem(3L, "B")
        ));

        when(formRepository.findBySlug(slug)).thenReturn(Optional.of(form));
        when(responseRepository.save(any(Response.class))).thenAnswer(i -> i.getArguments()[0]);

        SubmitResponseResponseDto response = responseService.submitResponse(slug, request, user);

        assertNotNull(response);
        assertEquals(ResponseMessageConstant.SUBMIT_RESPONSE_SUCCESS_MESSAGE, response.getMessage());
        verify(responseRepository, times(1)).save(any(Response.class));
    }

    @Test
    void submitResponse_whenFormDoesNotExist_shouldThrowsResourceNotFound() {
        when(formRepository.findBySlug(slug)).thenReturn(Optional.empty());
        SubmitResponseRequestDto request = new SubmitResponseRequestDto();

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> responseService.submitResponse(slug, request, user));
        assertEquals(ExceptionMessageConstant.FORM_NOT_FOUND_MESSAGE, ex.getMessage());
    }

    @Test
    void submitResponse_whenDomainNotAllowed_shouldThrowsForbiddenAccess() {
        AllowedDomain domain = new AllowedDomain();
        domain.setDomain("OwO.id");
        form.setAllowedDomains(List.of(domain));

        when(formRepository.findBySlug(slug)).thenReturn(Optional.of(form));
        SubmitResponseRequestDto request = new SubmitResponseRequestDto();

        ForbiddenAccessException ex = assertThrows(ForbiddenAccessException.class,
                () -> responseService.submitResponse(slug, request, user));
        assertEquals(ExceptionMessageConstant.FORBIDDEN_ACCESS_MESSAGE, ex.getMessage());
    }

    @Test
    void submitResponse_whenUserAlreadySubmitted_shouldThrowsOneResponseLimit() {
        form.setLimitOneResponse(true);
        when(formRepository.findBySlug(slug)).thenReturn(Optional.of(form));
        when(responseRepository.existsByFormAndUser(form, user)).thenReturn(true);

        SubmitResponseRequestDto request = new SubmitResponseRequestDto();
        OneResponseLimitException ex = assertThrows(OneResponseLimitException.class,
                () -> responseService.submitResponse(slug, request, user));

        assertEquals(ExceptionMessageConstant.ONE_RESPONSE_LIMIT_MESSAGE, ex.getMessage());
    }

    @Test
    void submitResponse_whenValidationFails_accumulatesErrors() {
        form.getQuestions().add(createQuestion(1L, ChoiceType.SHORT_ANSWER, true, null));
        form.getQuestions().add(createQuestion(2L, ChoiceType.DATE, false, null));
        form.getQuestions().add(createQuestion(3L, ChoiceType.TIME, false, null));
        form.getQuestions().add(createQuestion(4L, ChoiceType.MULTIPLE_CHOICE, false, "Red,Blue"));
        form.getQuestions().add(createQuestion(5L, ChoiceType.DROPDOWN, false, "Small,Large"));
        form.getQuestions().add(createQuestion(6L, ChoiceType.CHECKBOXES, false, "Java,PHP"));

        SubmitResponseRequestDto request = new SubmitResponseRequestDto();
        request.setAnswers(List.of(
                createAnswerItem(2L, "24-10-2023"),
                createAnswerItem(3L, "10 PM"),
                createAnswerItem(4L, "Red,Blue"),
                createAnswerItem(5L, "Medium"),
                createAnswerItem(6L, "Java,Python")
        ));

        when(formRepository.findBySlug(slug)).thenReturn(Optional.of(form));

        ValidationFieldException ex = assertThrows(ValidationFieldException.class,
                () -> responseService.submitResponse(slug, request, user));

        Map<String, List<String>> errorsMap = ex.getErrors();
        assertTrue(errorsMap.containsKey("answers"));

        List<String> errors = errorsMap.get("answers");
        assertEquals(6, errors.size());

        assertTrue(errors.contains("The answers field is required."));
        assertTrue(errors.contains("The answers field must be a valid date format."));
        assertTrue(errors.contains("The answers field must be a valid time format."));
        assertTrue(errors.contains("Only one choice is permitted for multiple choices or dropdown."));
        assertTrue(errors.contains("The selected option is not a valid choice."));
        assertTrue(errors.contains("One or more selected checkboxes are invalid."));

        verify(responseRepository, never()).save(any());
    }
}
