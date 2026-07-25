package com.relationshiplogic.domain.auth;

import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class AuthorizationCode {

    private final String code;
    private final Long userId;
    private final LocalDateTime expiresAt;

    private AuthorizationCode(String code, Long userId, LocalDateTime expiresAt) {
        this.code = code;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }

    public static AuthorizationCode issue(String code, Long userId, LocalDateTime issuedAt, Duration ttl) {
        LocalDateTime expiresAt = issuedAt.plus(ttl);
        return new AuthorizationCode(code, userId, expiresAt);
    }

    public boolean isExpired(LocalDateTime now) {
        return !now.isBefore(this.expiresAt);
    }
}
