package com.relationshiplogic.infrastructure.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// OAuth2 로그인이 시작되는 시점(/oauth2/authorization/**)에 요청 출처를 세션에 기록해둔다.
// 로그인 성공 콜백 시점엔 이미 이 정보가 없어지므로, 로그인 시작 시점에 미리 캡처해야 한다.
@Component
@RequiredArgsConstructor
public class OAuth2RedirectTargetCaptureFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_REQUEST_PATH_PREFIX = "/oauth2/authorization/";

    private final FrontendRedirectResolver frontendRedirectResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().startsWith(AUTHORIZATION_REQUEST_PATH_PREFIX)) {
            frontendRedirectResolver.captureFromRequest(request);
        }

        filterChain.doFilter(request, response);
    }
}
