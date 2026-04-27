package com.harebusiness.form.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitResponseRequestDto {

    @NotEmpty(message = "The answers field is required.")
    private List<@Valid AnswerItem> answers;

    @Getter
    @Setter
    public static class AnswerItem {
        @JsonProperty("question_id")
        @NotNull(message = "Question ID is required")
        private Long questionId;

        private String value;
    }
}
