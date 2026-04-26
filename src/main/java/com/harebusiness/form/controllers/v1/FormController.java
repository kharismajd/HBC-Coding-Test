package com.harebusiness.form.controllers.v1;

import com.harebusiness.form.constants.ControllerConstant;
import com.harebusiness.form.constants.OpenApiConstant;
import com.harebusiness.form.dtos.request.CreateFormRequestDto;
import com.harebusiness.form.dtos.response.CreateFormResponseDto;
import com.harebusiness.form.dtos.response.GetAllFormsResponseDto;
import com.harebusiness.form.dtos.response.LoginResponseDto;
import com.harebusiness.form.models.AuthenticatedUser;
import com.harebusiness.form.services.FormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Operation(summary = "Create a form")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Create form successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateFormResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid field",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "Invalid Field",
                                      "errors": {
                                        "allowed_domains": [
                                          "The allowed domains must be an array."
                                        ],
                                        "name": [
                                          "The name field is required."
                                        ],
                                        "slug": [
                                          "The slug has already been taken."
                                        ]
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", ref = "UnauthorizedError")
    })
    @PostMapping
    public ResponseEntity<CreateFormResponseDto> createForm(
            @Valid @RequestBody CreateFormRequestDto request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        CreateFormResponseDto response = formService.createForm(request, currentUser.getUserEntity());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all forms")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Get all forms successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetAllFormsResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "401", ref = "UnauthorizedError")
    })
    @GetMapping
    public ResponseEntity<GetAllFormsResponseDto> getAllForms(
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        GetAllFormsResponseDto response = formService.getAllForms(currentUser.getUserEntity());

        return ResponseEntity.ok(response);
    }
}
