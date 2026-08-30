package com.relationshiplogic.infrastructure.auth;

import com.relationshiplogic.application.auth.AuthorizationCodeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthorizationCodeService authorizationCodeService;

    @Value("${app.oauth2.frontend-redirect-uri}")
    private String frontendRedirectUri;

    public OAuth2LoginSuccessHandler(AuthorizationCodeService authorizationCodeService) {
        this.authorizationCodeService = authorizationCodeService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        AppOAuth2User principal = (AppOAuth2User) authentication.getPrincipal();
        String code = authorizationCodeService.issue(principal.getUserId());

        String redirectUri = UriComponentsBuilder.fromUriString(frontendRedirectUri)
                .queryParam("code", code)
                .build()
                .toUriString();

        response.sendRedirect(redirectUri);
    }
}
