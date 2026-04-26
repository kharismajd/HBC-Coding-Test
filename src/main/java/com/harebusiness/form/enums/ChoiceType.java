package com.harebusiness.form.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.harebusiness.form.exceptions.InvalidChoiceTypeException;
import lombok.Getter;

@Getter
public enum ChoiceType {
    SHORT_ANSWER("short answer"),
    PARAGRAPH("paragraph"),
    DATE("date"),
    TIME("time"),
    MULTIPLE_CHOICE("multiple choice"),
    DROPDOWN("dropdown"),
    CHECKBOXES("checkboxes");

    private final String value;

    ChoiceType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static ChoiceType fromString(String text) {
        for (ChoiceType b : ChoiceType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new InvalidChoiceTypeException("Invalid choice type: " + text);
    }
}
