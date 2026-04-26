package com.harebusiness.form.services;

import com.harebusiness.form.dtos.request.LoginRequestDto;
import com.harebusiness.form.dtos.response.LoginResponseDto;
import com.harebusiness.form.dtos.response.LogoutResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto request);

    LogoutResponseDto logout(HttpServletRequest request);
}
