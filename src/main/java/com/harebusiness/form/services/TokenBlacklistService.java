package com.harebusiness.form.services;

import org.springframework.data.redis.core.StringRedisTemplate;

public interface TokenBlacklistService {

    public void blacklistToken(String token, long expirationTimeLeft);

    public boolean isTokenBlacklisted(String token);
}
