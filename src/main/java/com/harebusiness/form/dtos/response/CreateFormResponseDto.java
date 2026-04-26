package com.harebusiness.form.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFormResponseDto {

    private String message;
    private FormDataResponse form;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormDataResponse {
        private Long id;
        private String name;
        private String slug;
        private String description;

        @JsonProperty("limit_one_response")
        private boolean limitOneResponse;

        @JsonProperty("creator_id")
        private Long creatorId;
    }
}
