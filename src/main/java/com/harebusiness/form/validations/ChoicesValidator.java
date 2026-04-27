package com.harebusiness.form.validations;

import com.harebusiness.form.dtos.request.AddQuestionRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static com.harebusiness.form.enums.ChoiceType.CHECKBOXES;
import static com.harebusiness.form.enums.ChoiceType.DROPDOWN;
import static com.harebusiness.form.enums.ChoiceType.MULTIPLE_CHOICE;

public class ChoicesValidator implements ConstraintValidator<ValidChoices, AddQuestionRequestDto> {

    @Override
    public boolean isValid(AddQuestionRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getChoiceType() == null) return true;
        boolean requiresChoices = switch (dto.getChoiceType()) {
            case MULTIPLE_CHOICE, DROPDOWN, CHECKBOXES -> true;
            default -> false;
        };

        if (requiresChoices) {
            if (dto.getChoices() == null || dto.getChoices().isEmpty()) {
                addError(context, "choices", "The choices must be an array.");
                return false;
            }
        }

        return true;
    }

    private void addError(ConstraintValidatorContext context, String field, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }
}
