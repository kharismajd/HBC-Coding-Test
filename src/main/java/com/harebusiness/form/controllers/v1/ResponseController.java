package com.harebusiness.form.controllers.v1;

import com.harebusiness.form.constants.ControllerConstant;
import com.harebusiness.form.constants.OpenApiConstant;
import com.harebusiness.form.dtos.request.SubmitResponseRequestDto;
import com.harebusiness.form.dtos.response.SubmitResponseResponseDto;
import com.harebusiness.form.models.AuthenticatedUser;
import com.harebusiness.form.services.ResponseServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ControllerConstant.FORMS_PATH_V1 + "/{slug}" + ControllerConstant.RESPONSE)
@SecurityRequirement(name = OpenApiConstant.AUTHORIZATION)
public class ResponseController {

    @Autowired
    private ResponseServiceImpl responseService;

    @PostMapping
    public ResponseEntity<SubmitResponseResponseDto> submitResponse(
            @PathVariable String slug,
            @Valid @RequestBody SubmitResponseRequestDto request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        SubmitResponseResponseDto response = responseService.submitResponse(slug, request, currentUser.getUserEntity());

        return ResponseEntity.ok(response);
    }
}
