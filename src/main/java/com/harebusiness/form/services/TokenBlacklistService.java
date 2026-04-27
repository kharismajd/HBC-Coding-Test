package com.harebusiness.form.services;

public interface TokenBlacklistService {

    void blacklistToken(String token, long expirationTimeLeft);

    boolean isTokenBlacklisted(String token);
}
