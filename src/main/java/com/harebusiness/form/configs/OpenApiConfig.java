package com.harebusiness.form.configs;

import com.harebusiness.form.constants.OpenApiConstant;
import com.harebusiness.form.dtos.response.BasicExceptionResponseDto;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

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
        BasicExceptionResponseDto unauthExample = new BasicExceptionResponseDto("Unauthenticated");

        return new OpenAPI()
                .components(new Components()
                        .addResponses("UnauthorizedError", new ApiResponse()
                                .description("Unauthenticated error")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().example(unauthExample)))));
    }
}
