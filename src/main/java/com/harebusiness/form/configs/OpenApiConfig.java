package com.harebusiness.form.configs;

import com.harebusiness.form.constants.OpenApiConstant;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
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

}
