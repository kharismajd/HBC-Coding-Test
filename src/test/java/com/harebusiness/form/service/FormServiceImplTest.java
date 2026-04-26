package com.harebusiness.form.service;

import com.harebusiness.form.constants.ResponseMessageConstant;
import com.harebusiness.form.dtos.request.CreateFormRequestDto;
import com.harebusiness.form.dtos.response.CreateFormResponseDto;
import com.harebusiness.form.exceptions.UserNotFoundException;
import com.harebusiness.form.models.Form;
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
}
