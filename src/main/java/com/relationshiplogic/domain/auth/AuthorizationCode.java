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

    // 영속 저장소에서 읽어온 값으로 객체를 복원할 때 사용 (발급이 아니라 재구성이므로 issue와 구분)
    public static AuthorizationCode of(String code, Long userId, LocalDateTime expiresAt) {
        return new AuthorizationCode(code, userId, expiresAt);
    }

    public boolean isExpired(LocalDateTime now) {
        return !now.isBefore(this.expiresAt);
    }
}
