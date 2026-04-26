package com.harebusiness.form.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void blacklistToken(String token, long expirationTimeLeft) {
        stringRedisTemplate.opsForValue().set(token, "blacklisted", expirationTimeLeft, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(token));
    }
}
