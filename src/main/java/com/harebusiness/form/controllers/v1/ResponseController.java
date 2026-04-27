package com.harebusiness.form.controllers.v1;

import com.harebusiness.form.constants.ControllerConstant;
import com.harebusiness.form.constants.OpenApiConstant;
import com.harebusiness.form.dtos.request.SubmitResponseRequestDto;
import com.harebusiness.form.dtos.response.GetAllResponsesDto;
import com.harebusiness.form.dtos.response.SubmitResponseResponseDto;
import com.harebusiness.form.models.AuthenticatedUser;
import com.harebusiness.form.services.ResponseServiceImpl;
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
import org.springframework.web.bind.annotation.GetMapping;
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

    @Operation(summary = "Submit answer for a form")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Submit form successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SubmitResponseResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid field",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "Invalid field",
                                      "errors": {
                                        "answers": [
                                          "The answers field is required."
                                        ]
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "422", ref = OpenApiConstant.ONE_RESPONSE_LIMIT_ERROR),
            @ApiResponse(responseCode = "403", ref = OpenApiConstant.FORBIDDEN_ACCESS_ERROR),
            @ApiResponse(responseCode = "401", ref = OpenApiConstant.UNAUTHENTICATED_ERROR)
    })
    @PostMapping
    public ResponseEntity<SubmitResponseResponseDto> submitResponse(
            @PathVariable String slug,
            @Valid @RequestBody SubmitResponseRequestDto request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        SubmitResponseResponseDto response = responseService.submitResponse(slug, request, currentUser.getUserEntity());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all answer for a form")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Get response success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetAllResponsesDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", ref = OpenApiConstant.FORM_NOT_FOUND_ERROR),
            @ApiResponse(responseCode = "403", ref = OpenApiConstant.FORBIDDEN_ACCESS_ERROR),
            @ApiResponse(responseCode = "401", ref = OpenApiConstant.UNAUTHENTICATED_ERROR)
    })
    @GetMapping
    public ResponseEntity<GetAllResponsesDto> getAllResponses(
            @PathVariable String slug,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        GetAllResponsesDto response = responseService.getAllResponses(slug, currentUser.getUserEntity());

        return ResponseEntity.ok(response);
    }
}
