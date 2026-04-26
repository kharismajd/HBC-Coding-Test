package com.harebusiness.form.service;

import com.harebusiness.form.constants.ExceptionMessageConstant;
import com.harebusiness.form.constants.ResponseMessageConstant;
import com.harebusiness.form.dtos.request.LoginRequestDto;
import com.harebusiness.form.dtos.response.LoginResponseDto;
import com.harebusiness.form.dtos.response.LogoutResponseDto;
import com.harebusiness.form.exceptions.IncorrectEmailOrPasswordException;
import com.harebusiness.form.models.User;
import com.harebusiness.form.repositories.UserRepository;
import com.harebusiness.form.services.AuthServiceImpl;
import com.harebusiness.form.services.TokenBlacklistService;
import com.harebusiness.form.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginRequestDto loginRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDto("test@example.com", "password123");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("encodedPassword");
    }

    @Test
    void login_Success() {
        when(userRepository.findByEmailAndIsDeletedFalse(loginRequest.getEmail()))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), mockUser.getPassword()))
                .thenReturn(true);
        when(jwtUtil.generateToken(mockUser)).thenReturn("mocked-jwt-token");

        LoginResponseDto response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(ResponseMessageConstant.LOGIN_SUCCESS_MESSAGE, response.getMessage());
        assertEquals(mockUser.getName(), response.getUser().getName());
        assertEquals(mockUser.getEmail(), response.getUser().getEmail());
        assertEquals("mocked-jwt-token", response.getUser().getAccessToken());

        verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(anyString());
        verify(jwtUtil, times(1)).generateToken(any(User.class));
    }

    @Test
    void login_whenUserNotFound_shouldThrowsException() {
        when(userRepository.findByEmailAndIsDeletedFalse(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        IncorrectEmailOrPasswordException exception = assertThrows(
                IncorrectEmailOrPasswordException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals(ExceptionMessageConstant.INCORRECT_EMAIL_OR_PASSWORD_MESSAGE, exception.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void login_whenWrongPassword_shouldThrowsException() {
        when(userRepository.findByEmailAndIsDeletedFalse(loginRequest.getEmail()))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), mockUser.getPassword()))
                .thenReturn(false);

        assertThrows(IncorrectEmailOrPasswordException.class, () -> authService.login(loginRequest));

        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void logout_Success() {
        String token = "valid-jwt-token";
        String authHeader = "Bearer " + token;
        Instant now = Instant.parse("2026-04-27T10:00:00Z");
        Instant expiry = now.plusSeconds(3600);
        Duration mockDiff = Duration.ofSeconds(3600);

        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class);
             MockedStatic<Duration> mockedDuration = mockStatic(Duration.class)) {
            mockedInstant.when(Instant::now).thenReturn(now);
            mockedDuration.when(() -> Duration.between(any(Instant.class), any(Instant.class)))
                    .thenReturn(mockDiff);

            when(request.getHeader("Authorization")).thenReturn(authHeader);
            when(jwtUtil.extractExpiration(token)).thenReturn(expiry);

            LogoutResponseDto response = authService.logout(request);


            assertEquals(ResponseMessageConstant.LOGOUT_SUCCESS_MESSAGE, response.getMessage());
            verify(tokenBlacklistService, times(1)).blacklistToken(eq(token), anyLong());
        }
    }

    @Test
    void logout_whenNoHeader_shouldSuccess() {
        when(request.getHeader("Authorization")).thenReturn(null);

        LogoutResponseDto response = authService.logout(request);

        assertEquals(ResponseMessageConstant.LOGOUT_SUCCESS_MESSAGE, response.getMessage());
        verify(tokenBlacklistService, never()).blacklistToken(anyString(), anyLong());
    }

    @Test
    void logout_whenInvalidHeader_shouldSuccess() {
        when(request.getHeader("Authorization")).thenReturn("Invalid auth");

        LogoutResponseDto response = authService.logout(request);

        assertEquals(ResponseMessageConstant.LOGOUT_SUCCESS_MESSAGE, response.getMessage());
        verify(tokenBlacklistService, never()).blacklistToken(anyString(), anyLong());
    }

    @Test
    void logout_whenTokenAlreadyExpired_shouldSuccess() {
        String token = "expired-token";
        String authHeader = "Bearer " + token;
        Instant now = Instant.parse("2026-04-27T10:00:00Z");
        Instant expiry = now.minusSeconds(60);
        Duration mockDiff = Duration.ofSeconds(-60);

        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class);
             MockedStatic<Duration> mockedDuration = mockStatic(Duration.class)) {
            mockedInstant.when(Instant::now).thenReturn(now);
            mockedDuration.when(() -> Duration.between(any(Instant.class), any(Instant.class)))
                    .thenReturn(mockDiff);

            when(request.getHeader("Authorization")).thenReturn(authHeader);
            when(jwtUtil.extractExpiration(token)).thenReturn(expiry);

            authService.logout(request);

            verify(tokenBlacklistService, never()).blacklistToken(anyString(), anyLong());
        }
    }
}
