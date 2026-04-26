package com.harebusiness.form.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetFormDetailResponseDto {
    private String message;
    private FormDetailData form;

    @Getter
    @Setter
    public static class FormDetailData {
        private Long id;
        private String name;
        private String slug;
        private String description;

        @JsonProperty("limit_one_response")
        private int limitOneResponse;

        @JsonProperty("creator_id")
        private Long creatorId;

        @JsonProperty("allowed_domains")
        private List<String> allowedDomains;

        private List<QuestionResponseDto> questions;
    }

    @Getter
    @Setter
    public static class QuestionResponseDto {
        private Long id;

        @JsonProperty("form_id")
        private Long formId;

        private String name;

        @JsonProperty("choice_type")
        private String choiceType;

        private String choices;

        @JsonProperty("is_required")
        private int isRequired;
    }
}
