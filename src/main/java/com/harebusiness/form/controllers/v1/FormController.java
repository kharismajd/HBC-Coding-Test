package com.harebusiness.form.controllers.v1;

import com.harebusiness.form.constants.ControllerConstant;
import com.harebusiness.form.constants.OpenApiConstant;
import com.harebusiness.form.dtos.request.CreateFormRequestDto;
import com.harebusiness.form.dtos.response.CreateFormResponseDto;
import com.harebusiness.form.services.FormService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ControllerConstant.FORMS_PATH_V1)
@SecurityRequirement(name = OpenApiConstant.AUTHORIZATION)
public class FormController {

    @Autowired
    private FormService formService;

    @PostMapping
    public ResponseEntity<CreateFormResponseDto> create(
            @Valid @RequestBody CreateFormRequestDto request,
            @AuthenticationPrincipal User defaultUser) {
        CreateFormResponseDto response = formService.createForm(request, Long.parseLong(defaultUser.getUsername()));
        return ResponseEntity.ok(response);
    }
}
