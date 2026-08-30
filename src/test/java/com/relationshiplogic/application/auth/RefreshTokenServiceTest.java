package com.relationshiplogic.application.auth;

import com.relationshiplogic.domain.auth.FakeRefreshTokenRepository;
import com.relationshiplogic.domain.auth.InvalidRefreshTokenException;
import com.relationshiplogic.domain.support.FixedClockHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefreshTokenServiceTest {

    private FakeRefreshTokenRepository refreshTokenRepository;
    private FixedClockHolder clockHolder;
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshTokenRepository = new FakeRefreshTokenRepository();
        clockHolder = new FixedClockHolder(LocalDateTime.of(2026, 8, 2, 12, 0, 0));
        refreshTokenService = new RefreshTokenService(refreshTokenRepository, clockHolder);
    }

    @Test
    void 리프레시_토큰을_회전하면_새_토큰이_발급되고_이전_토큰은_사용할_수_없다() {
        // given
        Long userId = 1L;
        String oldToken = refreshTokenService.issue(userId);

        // when
        RotatedRefreshToken rotated = refreshTokenService.rotate(oldToken);

        // then
        assertThat(rotated.token()).isNotEqualTo(oldToken);
        assertThat(rotated.userId()).isEqualTo(userId);
        assertThatThrownBy(() -> refreshTokenService.rotate(oldToken))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void 유효기간_14일이_지난_리프레시_토큰은_회전할_수_없다() {
        // given
        Long userId = 1L;
        String oldToken = refreshTokenService.issue(userId);
        clockHolder.advance(Duration.ofDays(14).plusSeconds(1));

        // when & then
        assertThatThrownBy(() -> refreshTokenService.rotate(oldToken))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }
}
