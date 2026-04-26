package com.harebusiness.form.services;

import com.harebusiness.form.constants.ExceptionMessageConstant;
import com.harebusiness.form.constants.ResponseMessageConstant;
import com.harebusiness.form.dtos.request.LoginRequestDto;
import com.harebusiness.form.dtos.response.LoginResponseDto;
import com.harebusiness.form.dtos.response.LogoutResponseDto;
import com.harebusiness.form.exceptions.IncorrectEmailOrPasswordException;
import com.harebusiness.form.models.User;
import com.harebusiness.form.repositories.UserRepository;
import com.harebusiness.form.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.apache.bcel.ExceptionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    public LoginResponseDto login(LoginRequestDto request) {
        String email = request.getEmail();
        String password = request.getPassword();

        User user = userRepository.findByEmailAndIsDeletedFalse(email).orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectEmailOrPasswordException(ExceptionMessageConstant.INCORRECT_EMAIL_OR_PASSWORD_MESSAGE);
        }

        String token = jwtUtil.generateToken(user);

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setMessage(ResponseMessageConstant.LOGIN_SUCCESS_MESSAGE);

        LoginResponseDto.UserData userData = new LoginResponseDto.UserData();
        userData.setName(user.getName());
        userData.setEmail(user.getEmail());
        userData.setAccessToken(token);
        loginResponseDto.setUser(userData);

        return loginResponseDto;
    }

    public LogoutResponseDto logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            Instant expiry = jwtUtil.extractExpiration(token);
            long secondsRemaining = Duration.between(Instant.now(), expiry).toSeconds();

            if (secondsRemaining > 0) {
                tokenBlacklistService.blacklistToken(token, secondsRemaining);
            }
        }

        return new LogoutResponseDto(ResponseMessageConstant.LOGOUT_SUCCESS_MESSAGE);
    }

}
