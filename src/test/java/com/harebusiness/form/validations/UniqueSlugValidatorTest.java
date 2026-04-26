package com.harebusiness.form.validations;

import com.harebusiness.form.repositories.FormRepository;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniqueSlugValidatorTest {

    @Mock
    private FormRepository formRepository;

    @Mock
    private ConstraintValidatorContext context;

    @InjectMocks
    private UniqueSlugValidator validator;

    @Test
    void isValid_whenSlugDoesNotExist_shouldReturnTrue() {
        String slug = "new-unique-slug";
        when(formRepository.existsBySlug(slug)).thenReturn(false);

        boolean isValid = validator.isValid(slug, context);

        assertTrue(isValid);
    }

    @Test
    void isValid_whenSlugAlreadyExists_shouldReturnFalse() {
        String slug = "existing-slug";
        when(formRepository.existsBySlug(slug)).thenReturn(true);

        boolean isValid = validator.isValid(slug, context);

        assertFalse(isValid);
    }

    @Test
    void isValid_whenSlugIsEmpty_shouldReturnTrue() {
        assertTrue(validator.isValid("", context));
        assertTrue(validator.isValid(null, context));
    }
}
