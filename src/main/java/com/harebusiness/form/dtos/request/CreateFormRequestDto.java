package com.harebusiness.form.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harebusiness.form.validations.UniqueSlug;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFormRequestDto {
    @NotBlank(message = "The name field is required.")
    private String name;

    @NotBlank(message = "The slug field is required.")
    @Pattern(regexp = "^[a-zA-Z0-9.-]+$",
            message = "The slug must be alphanumeric and can only contain dots and dashes.")
    @UniqueSlug
    private String slug;

    @NotNull(message = "The allowed domains must be an array.")
    @JsonProperty("allowed_domains")
    private List<String> allowedDomains;

    private String description;

    @JsonProperty("limit_one_response")
    private boolean limitOneResponse;
}
