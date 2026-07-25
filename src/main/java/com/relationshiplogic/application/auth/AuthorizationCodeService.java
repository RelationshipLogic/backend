package com.relationshiplogic.application.auth;

import com.relationshiplogic.domain.auth.AuthorizationCode;
import com.relationshiplogic.domain.auth.AuthorizationCodeRepository;
import com.relationshiplogic.domain.support.ClockHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class AuthorizationCodeService {

    private static final Duration CODE_TTL = Duration.ofSeconds(60);

    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final ClockHolder clockHolder;

    public AuthorizationCodeService(AuthorizationCodeRepository authorizationCodeRepository, ClockHolder clockHolder) {
        this.authorizationCodeRepository = authorizationCodeRepository;
        this.clockHolder = clockHolder;
    }

    public String issue(Long userId) {
        AuthorizationCode authorizationCode = AuthorizationCode.issue(UUID.randomUUID().toString(), userId, clockHolder.now(), CODE_TTL);
        authorizationCodeRepository.save(authorizationCode);

        return authorizationCode.getCode();
    }

    public Long consume(String code) {
        AuthorizationCode authorizationCode = authorizationCodeRepository.findByCode(code).orElseThrow(IllegalArgumentException::new);
        authorizationCodeRepository.deleteByCode(authorizationCode.getCode());

        if (authorizationCode.isExpired(clockHolder.now())) {
            throw new IllegalArgumentException();
        }

        return authorizationCode.getUserId();
    }
}
