package com.relationshiplogic.interfaces.api.auth;

import com.relationshiplogic.application.auth.AuthService;
import com.relationshiplogic.application.auth.TokenPair;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final AuthService authService;

    // authService.exchange(code)로 TokenPair를 받아, refreshToken은 쿠키로 내려주고
    // accessToken만 응답 본문에 담는다.
    @PostMapping("/exchange")
    public AuthTokenResponse exchange(@Valid @RequestBody ExchangeRequest request, HttpServletResponse response) {
        TokenPair tokenPair = authService.exchange(request.code());
        setRefreshTokenCookie(response, tokenPair.refreshToken());

        return new AuthTokenResponse(tokenPair.accessToken());
    }

    // authService.refresh(refreshToken)로 TokenPair를 받아, 새 refreshToken으로 쿠키를 갱신하고
    // accessToken만 응답 본문에 담는다.
    @PostMapping("/refresh")
    public AuthTokenResponse refresh(@CookieValue(REFRESH_TOKEN_COOKIE_NAME) String refreshToken, HttpServletResponse response) {
        TokenPair tokenPair = authService.refresh(refreshToken);
        setRefreshTokenCookie(response, tokenPair.refreshToken());

        return new AuthTokenResponse(tokenPair.accessToken());
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(Duration.ofDays(14))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
