package com.harebusiness.form.configs;

import com.harebusiness.form.constants.ExceptionMessageConstant;
import com.harebusiness.form.constants.OpenApiConstant;
import com.harebusiness.form.dtos.response.BasicExceptionResponseDto;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = OpenApiConstant.AUTHORIZATION,
        type = SecuritySchemeType.HTTP,
        bearerFormat = OpenApiConstant.JWT,
        scheme = OpenApiConstant.BEARER_AUTH
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        BasicExceptionResponseDto unauthExample = new BasicExceptionResponseDto(ExceptionMessageConstant.UNAUTHENTICATED_MESSAGE);
        BasicExceptionResponseDto forbiddenExample = new BasicExceptionResponseDto(ExceptionMessageConstant.FORBIDDEN_ACCESS_MESSAGE);
        BasicExceptionResponseDto formNotFoundExample = new BasicExceptionResponseDto(ExceptionMessageConstant.FORM_NOT_FOUND_MESSAGE);
        BasicExceptionResponseDto questionNotFoundExample = new BasicExceptionResponseDto(ExceptionMessageConstant.QUESTION_NOT_FOUND_MESSAGE);

        return new OpenAPI()
                .components(new Components()
                        .addResponses(OpenApiConstant.UNAUTHENTICATED_ERROR, new ApiResponse()
                                .description("Unauthenticated error")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().example(unauthExample))))
                        .addResponses(OpenApiConstant.FORBIDDEN_ACCESS_ERROR, new ApiResponse()
                                .description("Forbidden error")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().example(forbiddenExample))))
                        .addResponses(OpenApiConstant.FORM_NOT_FOUND_ERROR, new ApiResponse()
                                .description("Form not found error")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().example(formNotFoundExample))))
                        .addResponses(OpenApiConstant.QUESTION_NOT_FOUND_ERROR, new ApiResponse()
                                .description("Question not found error")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().example(questionNotFoundExample))))
                );
    }
}
