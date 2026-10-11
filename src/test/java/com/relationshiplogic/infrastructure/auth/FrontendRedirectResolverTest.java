package com.relationshiplogic.infrastructure.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FrontendRedirectResolverTest {

    private static final String PROD = "https://app.catchsignal.io.kr/auth/callback";
    private static final String DEV = "https://dev.catchsignal.io.kr/auth/callback";

    private FrontendRedirectResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new FrontendRedirectResolver(PROD + ", " + DEV);
    }

    @Test
    void dev_프론트에서_로그인을_시작하면_dev_콜백으로_리다이렉트된다() {
        // given
        MockHttpServletRequest request = requestWithReferer("https://dev.catchsignal.io.kr/login");

        // when
        resolver.captureFromRequest(request);

        // then
        assertThat(resolver.resolve(request)).isEqualTo(DEV);
    }

    @Test
    void 운영_프론트에서_로그인을_시작하면_운영_콜백으로_리다이렉트된다() {
        // given
        MockHttpServletRequest request = requestWithReferer("https://app.catchsignal.io.kr/login");

        // when
        resolver.captureFromRequest(request);

        // then
        assertThat(resolver.resolve(request)).isEqualTo(PROD);
    }

    @Test
    void Referer가_없으면_기본값인_운영_콜백으로_폴백한다() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        resolver.captureFromRequest(request);

        // then
        assertThat(resolver.resolve(request)).isEqualTo(PROD);
    }

    @Test
    void allowlist에_없는_Referer면_기본값인_운영_콜백으로_폴백한다() {
        // given
        MockHttpServletRequest request = requestWithReferer("https://evil.com/login");

        // when
        resolver.captureFromRequest(request);

        // then
        assertThat(resolver.resolve(request)).isEqualTo(PROD);
    }

    @Test
    void 허용_도메인으로_시작하지만_다른_도메인인_Referer는_매칭되지_않는다() {
        // given
        MockHttpServletRequest request = requestWithReferer("https://dev.catchsignal.io.kr.evil.com/login");

        // when
        resolver.captureFromRequest(request);

        // then
        assertThat(resolver.resolve(request)).isEqualTo(PROD);
    }

    @Test
    void 포트가_다른_Referer는_매칭되지_않는다() {
        // given
        MockHttpServletRequest request = requestWithReferer("https://dev.catchsignal.io.kr:8443/login");

        // when
        resolver.captureFromRequest(request);

        // then
        assertThat(resolver.resolve(request)).isEqualTo(PROD);
    }

    @Test
    void 형식이_잘못된_Referer면_기본값인_운영_콜백으로_폴백한다() {
        // given
        MockHttpServletRequest request = requestWithReferer("not a url ::");

        // when
        resolver.captureFromRequest(request);

        // then
        assertThat(resolver.resolve(request)).isEqualTo(PROD);
    }

    @Test
    void allowlist가_비어있으면_생성할_수_없다() {
        // when & then
        assertThatThrownBy(() -> new FrontendRedirectResolver(" , "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private MockHttpServletRequest requestWithReferer(String referer) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", referer);
        return request;
    }
}
