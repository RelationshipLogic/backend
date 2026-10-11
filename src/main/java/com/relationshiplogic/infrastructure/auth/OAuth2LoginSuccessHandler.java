package com.relationshiplogic.infrastructure.auth;

import com.relationshiplogic.application.auth.AuthorizationCodeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthorizationCodeService authorizationCodeService;
    private final FrontendRedirectResolver frontendRedirectResolver;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        AppOAuth2User principal = (AppOAuth2User) authentication.getPrincipal();
        String code = authorizationCodeService.issue(principal.getUserId());

        String redirectUri = UriComponentsBuilder.fromUriString(frontendRedirectResolver.resolve(request))
                .queryParam("code", code)
                .build()
                .toUriString();

        response.sendRedirect(redirectUri);
    }
}
