package com.relationshiplogic.domain.auth;

import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class RefreshToken {

    private final String token;
    private final Long userId;
    private final LocalDateTime expiresAt;

    private RefreshToken(String token, Long userId, LocalDateTime expiresAt) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }

    public static RefreshToken issue(String token, Long userId, LocalDateTime issuedAt, Duration ttl) {
        LocalDateTime expiresAt = issuedAt.plus(ttl);
        return new RefreshToken(token, userId, expiresAt);
    }

    public static RefreshToken of(String token, Long userId, LocalDateTime expiresAt) {
        return new RefreshToken(token, userId, expiresAt);
    }

    public boolean isExpired(LocalDateTime now) {
        return !now.isBefore(this.expiresAt);
    }
}
