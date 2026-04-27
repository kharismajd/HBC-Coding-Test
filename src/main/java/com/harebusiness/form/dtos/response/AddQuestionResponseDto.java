package com.harebusiness.form.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddQuestionResponseDto {
    private String message;
    private QuestionData question;

    @Getter
    @Setter
    public static class QuestionData {
        private Long id;

        @JsonProperty("form_id")
        private Long formId;

        private String name;

        @JsonProperty("choice_type")
        private String choiceType;

        private String choices;

        @JsonProperty("is_required")
        private boolean isRequired;
    }
}
