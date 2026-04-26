package com.harebusiness.form.service;

import com.harebusiness.form.constants.ExceptionMessageConstant;
import com.harebusiness.form.constants.ResponseMessageConstant;
import com.harebusiness.form.dtos.request.CreateFormRequestDto;
import com.harebusiness.form.dtos.response.CreateFormResponseDto;
import com.harebusiness.form.dtos.response.GetAllFormsResponseDto;
import com.harebusiness.form.dtos.response.GetFormDetailResponseDto;
import com.harebusiness.form.enums.ChoiceType;
import com.harebusiness.form.exceptions.ForbiddenAccessException;
import com.harebusiness.form.exceptions.ResourceNotFoundException;
import com.harebusiness.form.exceptions.UserNotFoundException;
import com.harebusiness.form.models.AllowedDomain;
import com.harebusiness.form.models.Form;
import com.harebusiness.form.models.Question;
import com.harebusiness.form.models.User;
import com.harebusiness.form.repositories.AllowedDomainRepository;
import com.harebusiness.form.repositories.FormRepository;
import com.harebusiness.form.repositories.UserRepository;
import com.harebusiness.form.services.FormServiceImpl;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormServiceImplTest {

    @Mock
    private FormRepository formRepository;

    @Mock
    private AllowedDomainRepository allowedDomainRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FormServiceImpl formService;

    private CreateFormRequestDto requestDto;
    private User mockUser;
    private User mockUser2;
    private Form mockForm;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        requestDto = new CreateFormRequestDto();
        requestDto.setName("Test Form");
        requestDto.setSlug("test-form");
        requestDto.setDescription("Description");
        requestDto.setLimitOneResponse(true);
        requestDto.setAllowedDomains(List.of("example.com", "test.id"));

        mockUser = new User();
        mockUser.setId(userId);
        mockUser.setEmail("user@test.com");

        mockUser2 = new User();
        mockUser2.setId(1L);
        mockUser2.setEmail("test@webtech.id");

        User creator = new User();
        creator.setId(2L);

        mockForm = new Form();
        mockForm.setId(10L);
        mockForm.setName("Test Form");
        mockForm.setSlug("test-slug");
        mockForm.setDescription("Desc");
        mockForm.setLimitOneResponse(true);
        mockForm.setCreator(creator);
    }

    @Test
    void createForm_Success() {
        Form savedForm = new Form();
        savedForm.setId(100L);
        savedForm.setName(requestDto.getName());
        savedForm.setSlug(requestDto.getSlug());
        savedForm.setDescription(requestDto.getDescription());
        savedForm.setLimitOneResponse(requestDto.isLimitOneResponse());
        savedForm.setCreator(mockUser);

        when(formRepository.save(any(Form.class))).thenReturn(savedForm);
        when(allowedDomainRepository.saveAll(anyList())).thenReturn(List.of());

        CreateFormResponseDto result = formService.createForm(requestDto, mockUser);

        assertNotNull(result);
        assertEquals(ResponseMessageConstant.CREATE_FORM_SUCCESS_MESSAGE, result.getMessage());
        assertEquals(100L, result.getForm().getId());
        assertEquals("test-form", result.getForm().getSlug());
        assertEquals(userId, result.getForm().getCreatorId());
        verify(formRepository, times(1)).save(any(Form.class));
        verify(allowedDomainRepository, times(1)).saveAll(anyList());
    }

    @Test
    void getAllForms_Success() {
        Form form1 = new Form();
        form1.setId(10L);
        form1.setName("Form 1");
        form1.setSlug("form-1");
        form1.setLimitOneResponse(true);
        form1.setCreator(mockUser);

        Form form2 = new Form();
        form2.setId(9L);
        form2.setName("Form 2");
        form2.setSlug("form-2");
        form2.setLimitOneResponse(false);
        form2.setCreator(mockUser);

        when(formRepository.findAllByCreatorIdOrderByIdDesc(userId))
                .thenReturn(List.of(form1, form2));

        GetAllFormsResponseDto result = formService.getAllForms(mockUser);

        assertNotNull(result);
        assertEquals(ResponseMessageConstant.GET_ALL_FORMS_SUCCESS_MESSAGE, result.getMessage());
        assertEquals(2, result.getForms().size());

        GetAllFormsResponseDto.FormDataResponse firstForm = result.getForms().get(0);
        assertEquals(10L, firstForm.getId());
        assertEquals("Form 1", firstForm.getName());
        assertEquals(1, firstForm.getLimitOneResponse());
        assertEquals(userId, firstForm.getCreatorId());

        GetAllFormsResponseDto.FormDataResponse secondForm = result.getForms().get(1);
        assertEquals(9L, secondForm.getId());
        assertEquals(0, secondForm.getLimitOneResponse());

        verify(formRepository, times(1)).findAllByCreatorIdOrderByIdDesc(userId);
    }

    @Test
    void getAllForms_EmptyList() {
        when(formRepository.findAllByCreatorIdOrderByIdDesc(userId)).thenReturn(List.of());

        GetAllFormsResponseDto result = formService.getAllForms(mockUser);
        
        assertTrue(result.getForms().isEmpty());
        assertEquals(ResponseMessageConstant.GET_ALL_FORMS_SUCCESS_MESSAGE, result.getMessage());
    }

    @Test
    void getFormDetail_Success() {
        AllowedDomain allowedDomain = new AllowedDomain();
        allowedDomain.setDomain("webtech.id");
        mockForm.setAllowedDomains(List.of(allowedDomain));

        Question question = new Question();
        question.setId(100L);
        question.setName("Sex");
        question.setChoiceType(ChoiceType.MULTIPLE_CHOICE);
        question.setChoices("Male,Female");
        question.setRequired(true);
        question.setForm(mockForm);
        mockForm.setQuestions(List.of(question));

        when(formRepository.findBySlug("test-slug")).thenReturn(Optional.of(mockForm));

        GetFormDetailResponseDto result = formService.getFormDetail("test-slug", mockUser2);

        assertNotNull(result);
        assertEquals("multiple choice", result.getForm().getQuestions().get(0).getChoiceType());
        assertEquals("Male,Female", result.getForm().getQuestions().get(0).getChoices());
    }

    @Test
    void getFormDetail_whenFormNotFound_shouldThrowsException() {
        when(formRepository.findBySlug("invalid-slug")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            formService.getFormDetail("invalid-slug", mockUser);
        });

        assertEquals(ExceptionMessageConstant.FORM_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    void getFormDetail_whenForbiddenDomain_shouldThrowsException() {
        AllowedDomain allowedDomain = new AllowedDomain();
        allowedDomain.setDomain("domainqu.com");
        mockForm.setAllowedDomains(List.of(allowedDomain));

        when(formRepository.findBySlug("test-slug")).thenReturn(Optional.of(mockForm));

        ForbiddenAccessException exception = assertThrows(ForbiddenAccessException.class, () -> {
            formService.getFormDetail("test-slug", mockUser);
        });

        assertEquals(ExceptionMessageConstant.FORBIDDEN_ACCESS_MESSAGE, exception.getMessage());
    }
}
