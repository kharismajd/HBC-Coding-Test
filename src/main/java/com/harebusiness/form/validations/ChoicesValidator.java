package com.harebusiness.form.validations;

import com.harebusiness.form.dtos.request.AddQuestionRequestDto;
import com.harebusiness.form.enums.ChoiceType;
import com.harebusiness.form.exceptions.InvalidChoiceTypeException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static com.harebusiness.form.enums.ChoiceType.CHECKBOXES;
import static com.harebusiness.form.enums.ChoiceType.DROPDOWN;
import static com.harebusiness.form.enums.ChoiceType.MULTIPLE_CHOICE;

public class ChoicesValidator implements ConstraintValidator<ValidChoices, AddQuestionRequestDto> {

    @Override
    public boolean isValid(AddQuestionRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getChoiceType() == null) return true;
        try {
            ChoiceType type = ChoiceType.fromString(dto.getChoiceType());

            boolean requiresChoices = switch (type) {
                case MULTIPLE_CHOICE, DROPDOWN, CHECKBOXES -> true;
                default -> false;
            };

            if (requiresChoices && (dto.getChoices() == null || dto.getChoices().isEmpty())) {
                addError(context, "choices", "The choices must be an array.");
                return false;
            }

        } catch (InvalidChoiceTypeException e) {
            addError(context, "choice_type", e.getMessage());
            return false;
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
