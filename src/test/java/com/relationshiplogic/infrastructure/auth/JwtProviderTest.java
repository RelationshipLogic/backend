package com.relationshiplogic.infrastructure.auth;

import com.relationshiplogic.domain.support.FixedClockHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        FixedClockHolder clockHolder = new FixedClockHolder(LocalDateTime.of(2026, 7, 26, 12, 0, 0));
        jwtProvider = new JwtProvider("test-secret-key-which-is-long-enough-for-hmac-sha256", clockHolder);
    }

    @Test
    void 발급한_토큰을_파싱하면_원래_userId가_나온다() {
        // given
        Long userId = 1L;

        // when
        String token = jwtProvider.generateAccessToken(userId);
        Long parsedUserId = jwtProvider.parseUserId(token);

        // then
        assertThat(parsedUserId).isEqualTo(userId);
    }
}
