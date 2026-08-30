package com.relationshiplogic.application.auth;

import com.relationshiplogic.infrastructure.auth.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthorizationCodeService authorizationCodeService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    // 인가 코드를 소모해 userId를 얻고, 액세스 토큰 + 리프레시 토큰을 함께 발급한다.
    public TokenPair exchange(String code) {
        Long userId = authorizationCodeService.consume(code);
        String accessToken = jwtProvider.generateAccessToken(userId);
        String refreshToken = refreshTokenService.issue(userId);

        return new TokenPair(accessToken, refreshToken);
    }

    // 리프레시 토큰을 회전하고, 같은 사용자의 새 액세스 토큰을 발급한다.
    public TokenPair refresh(String oldRefreshToken) {
        RotatedRefreshToken rotated = refreshTokenService.rotate(oldRefreshToken);
        String accessToken = jwtProvider.generateAccessToken(rotated.userId());

        return new TokenPair(accessToken, rotated.token());
    }
}
