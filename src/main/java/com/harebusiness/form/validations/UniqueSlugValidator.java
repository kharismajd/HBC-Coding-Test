package com.harebusiness.form.validations;

import com.harebusiness.form.repositories.FormRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueSlugValidator implements ConstraintValidator<UniqueSlug, String> {

    @Autowired
    private FormRepository formRepository;

    @Override
    public boolean isValid(String slug, ConstraintValidatorContext context) {
        if (slug == null || slug.isEmpty()) {
            return true;
        }

        return !formRepository.existsBySlug(slug);
    }
}
