package com.harebusiness.form.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harebusiness.form.enums.ChoiceType;
import com.harebusiness.form.validations.ValidChoices;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ValidChoices
public class AddQuestionRequestDto {

    @NotBlank(message = "The name field is required.")
    private String name;

    @NotNull(message = "The choice type field is required.")
    @JsonProperty("choice_type")
    private ChoiceType choiceType;

    private List<@NotBlank(message = "The choices element must be a string.") String> choices;

    @JsonProperty("is_required")
    private boolean required;
}
