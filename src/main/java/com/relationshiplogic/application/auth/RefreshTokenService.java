package com.relationshiplogic.application.auth;

import com.relationshiplogic.domain.auth.RefreshToken;
import com.relationshiplogic.domain.auth.RefreshTokenRepository;
import com.relationshiplogic.domain.support.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(14);

    private final RefreshTokenRepository refreshTokenRepository;
    private final ClockHolder clockHolder;


    // 신규 발급: userId에 대한 리프레시 토큰을 새로 만들어 저장하고, 원문 토큰 문자열을 반환한다
    public String issue(Long userId) {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.issue(token, userId, clockHolder.now(), REFRESH_TOKEN_TTL);
        refreshTokenRepository.save(refreshToken);

        return token;
    }

    // 회전: oldToken이 유효하면(존재 + 미만료) 폐기하고 같은 userId로 새 토큰을 발급해 반환한다.
    // 유효하지 않으면 IllegalArgumentException을 던진다.
    public RotatedRefreshToken rotate(String oldToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(oldToken).orElseThrow(IllegalArgumentException::new);
        if (refreshToken.isExpired(clockHolder.now())) {
            throw new IllegalArgumentException();
        }
        refreshTokenRepository.deleteByToken(refreshToken.getToken());
        Long userId = refreshToken.getUserId();
        String newToken = issue(userId);
        return new RotatedRefreshToken(newToken, userId);
    }
}
