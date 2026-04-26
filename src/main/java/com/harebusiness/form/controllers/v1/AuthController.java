package com.harebusiness.form.controllers.v1;

import com.harebusiness.form.constants.ControllerConstant;
import com.harebusiness.form.constants.OpenApiConstant;
import com.harebusiness.form.dtos.request.LoginRequestDto;
import com.harebusiness.form.services.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ControllerConstant.AUTH_PATH_V1)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(ControllerConstant.LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(ControllerConstant.LOGOUT)
    @SecurityRequirement(name = OpenApiConstant.AUTHORIZATION)
    public ResponseEntity<?> logout(HttpServletRequest request) {
        return ResponseEntity.ok(authService.logout(request));
    }
}
