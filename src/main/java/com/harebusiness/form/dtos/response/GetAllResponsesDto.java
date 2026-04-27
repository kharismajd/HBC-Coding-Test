package com.harebusiness.form.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAllResponsesDto {
    private String message;
    private List<ResponseData> responses;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseData {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime date;

        private UserDto user;
        private Map<String, String> answers;
    }

    @Getter
    @Setter
    public static class UserDto {
        private Long id;
        private String name;
        private String email;

        @JsonProperty("email_verified_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime emailVerifiedAt;
    }
}
