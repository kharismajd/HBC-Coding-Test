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
public class GetAllFormsResponseDto {

    private String message;
    private List<FormDataResponse> forms;

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
        private int limitOneResponse;

        @JsonProperty("creator_id")
        private Long creatorId;
    }
}
