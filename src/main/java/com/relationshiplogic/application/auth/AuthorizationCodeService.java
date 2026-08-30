package com.relationshiplogic.application.auth;

import com.relationshiplogic.domain.auth.AuthorizationCode;
import com.relationshiplogic.domain.auth.AuthorizationCodeRepository;
import com.relationshiplogic.domain.auth.InvalidAuthorizationCodeException;
import com.relationshiplogic.domain.support.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorizationCodeService {

    private static final Duration CODE_TTL = Duration.ofSeconds(60);

    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final ClockHolder clockHolder;


    public String issue(Long userId) {
        AuthorizationCode authorizationCode = AuthorizationCode.issue(UUID.randomUUID().toString(), userId, clockHolder.now(), CODE_TTL);
        authorizationCodeRepository.save(authorizationCode);

        return authorizationCode.getCode();
    }

    public Long consume(String code) {
        AuthorizationCode authorizationCode = authorizationCodeRepository.findByCode(code)
                .orElseThrow(InvalidAuthorizationCodeException::new);
        authorizationCodeRepository.deleteByCode(authorizationCode.getCode());

        if (authorizationCode.isExpired(clockHolder.now())) {
            throw new InvalidAuthorizationCodeException();
        }

        return authorizationCode.getUserId();
    }
}
