package com.relationshiplogic.infrastructure.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

// 로그인 성공 후 리다이렉트할 프론트 URL을 환경(prod/dev)별로 결정한다.
// Referer 등 외부 입력을 리다이렉트 대상으로 직접 사용하지 않고, 항상 설정된 allowlist 중 하나로만 귀결시켜
// open redirect를 방지한다. (환경 추가 시 allowlist 설정값만 늘리면 되고 이 클래스는 변경하지 않는다)
@Component
public class FrontendRedirectResolver {

    static final String SESSION_ATTRIBUTE = "oauth2.frontend-redirect-uri";

    private final List<String> allowedRedirectUris;

    public FrontendRedirectResolver(@Value("${app.oauth2.frontend-redirect-uris}") String allowedRedirectUris) {
        this.allowedRedirectUris = Arrays.stream(allowedRedirectUris.split(","))
                .map(String::trim)
                .filter(uri -> !uri.isEmpty())
                .toList();

        if (this.allowedRedirectUris.isEmpty()) {
            throw new IllegalArgumentException("app.oauth2.frontend-redirect-uris must contain at least one URI");
        }
    }

    // 로그인 시작 요청(Referer)의 origin이 allowlist 항목의 origin과 정확히 일치하면 해당 항목을 세션에 미리 기록해둔다.
    public void captureFromRequest(HttpServletRequest request) {
        String refererOrigin = originOf(request.getHeader("Referer"));
        if (refererOrigin == null) {
            return;
        }

        allowedRedirectUris.stream()
                .filter(uri -> refererOrigin.equals(originOf(uri)))
                .findFirst()
                .ifPresent(matched -> request.getSession().setAttribute(SESSION_ATTRIBUTE, matched));
    }

    // 로그인 성공 시점에 세션에 기록된 값을 꺼낸다. 없거나 allowlist에 없는 값이면 기본값(첫 번째 = 운영)으로 폴백한다.
    public String resolve(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object stored = session != null ? session.getAttribute(SESSION_ATTRIBUTE) : null;

        if (stored instanceof String redirectUri && allowedRedirectUris.contains(redirectUri)) {
            return redirectUri;
        }

        return allowedRedirectUris.get(0);
    }

    private static String originOf(String url) {
        if (url == null) {
            return null;
        }

        try {
            URI uri = new URI(url);
            if (uri.getScheme() == null || uri.getHost() == null) {
                return null;
            }
            String port = uri.getPort() == -1 ? "" : ":" + uri.getPort();
            return uri.getScheme().toLowerCase() + "://" + uri.getHost().toLowerCase() + port;
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
