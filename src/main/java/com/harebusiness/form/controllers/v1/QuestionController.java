package com.harebusiness.form.controllers.v1;

import com.harebusiness.form.constants.ControllerConstant;
import com.harebusiness.form.constants.OpenApiConstant;
import com.harebusiness.form.dtos.request.AddQuestionRequestDto;
import com.harebusiness.form.dtos.response.AddQuestionResponseDto;
import com.harebusiness.form.dtos.response.RemoveQuestionResponseDto;
import com.harebusiness.form.models.AuthenticatedUser;
import com.harebusiness.form.services.QuestionService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ControllerConstant.FORMS_PATH_V1 + "/{slug}" + ControllerConstant.QUESTION)
@SecurityRequirement(name = OpenApiConstant.AUTHORIZATION)
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Operation(summary = "Add question to a form")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Add question successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddQuestionResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "401", ref = OpenApiConstant.UNAUTHENTICATED_ERROR),
            @ApiResponse(responseCode = "404", ref = OpenApiConstant.FORM_NOT_FOUND_ERROR),
            @ApiResponse(responseCode = "403", ref = OpenApiConstant.FORBIDDEN_ACCESS_ERROR),
    })
    @PostMapping
    public ResponseEntity<AddQuestionResponseDto> addQuestion(
            @PathVariable String slug,
            @Valid @RequestBody AddQuestionRequestDto request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        AddQuestionResponseDto response = questionService.addQuestion(slug, request, currentUser.getUserEntity());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remove a question from a form")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Add question successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RemoveQuestionResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", ref = OpenApiConstant.FORM_NOT_FOUND_ERROR),
            @ApiResponse(responseCode = "401", ref = OpenApiConstant.UNAUTHENTICATED_ERROR),
            @ApiResponse(responseCode = "403", ref = OpenApiConstant.FORBIDDEN_ACCESS_ERROR),
    })
    @DeleteMapping("/{questionId}")
    public ResponseEntity<RemoveQuestionResponseDto> removeQuestion(
            @PathVariable String slug,
            @PathVariable Long questionId,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        RemoveQuestionResponseDto response = questionService.removeQuestion(slug, questionId, currentUser.getUserEntity());

        return ResponseEntity.ok(response);
    }
}
