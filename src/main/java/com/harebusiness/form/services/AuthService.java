package com.harebusiness.form.services;

import com.harebusiness.form.dtos.request.LoginRequestDto;
import com.harebusiness.form.dtos.response.LoginResponseDto;

public interface AuthService {

    public LoginResponseDto login(LoginRequestDto request);

}
