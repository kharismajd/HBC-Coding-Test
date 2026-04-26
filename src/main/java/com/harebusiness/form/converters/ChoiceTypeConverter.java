package com.harebusiness.form.converters;

import com.harebusiness.form.enums.ChoiceType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ChoiceTypeConverter implements AttributeConverter<ChoiceType, String> {

    @Override
    public String convertToDatabaseColumn(ChoiceType choiceType) {
        if (choiceType == null) return null;
        return choiceType.getValue();
    }

    @Override
    public ChoiceType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return ChoiceType.fromString(dbData);
    }
}