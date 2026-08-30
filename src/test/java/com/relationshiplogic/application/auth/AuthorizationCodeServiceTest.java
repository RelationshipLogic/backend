package com.relationshiplogic.application.auth;

import com.relationshiplogic.domain.auth.FakeAuthorizationCodeRepository;
import com.relationshiplogic.domain.auth.InvalidAuthorizationCodeException;
import com.relationshiplogic.domain.support.FixedClockHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthorizationCodeServiceTest {

    private FakeAuthorizationCodeRepository authorizationCodeRepository;
    private FixedClockHolder clockHolder;
    private AuthorizationCodeService authorizationCodeService;

    @BeforeEach
    void setUp() {
        authorizationCodeRepository = new FakeAuthorizationCodeRepository();
        clockHolder = new FixedClockHolder(LocalDateTime.of(2026, 7, 25, 12, 0, 0));
        authorizationCodeService = new AuthorizationCodeService(authorizationCodeRepository, clockHolder);
    }

    @Test
    void 발급된_인가코드는_1회_사용하면_다시_사용할_수_없다() {
        // given
        Long userId = 1L;
        String code = authorizationCodeService.issue(userId);

        // when
        Long consumedUserId = authorizationCodeService.consume(code);

        // then
        assertThat(consumedUserId).isEqualTo(userId);
        assertThatThrownBy(() -> authorizationCodeService.consume(code))
                .isInstanceOf(InvalidAuthorizationCodeException.class);
    }

    @Test
    void 유효시간_60초가_지난_인가코드는_사용할_수_없다() {
        // given
        Long userId = 1L;
        String code = authorizationCodeService.issue(userId);
        clockHolder.advance(Duration.ofSeconds(61));

        // when & then
        assertThatThrownBy(() -> authorizationCodeService.consume(code))
                .isInstanceOf(InvalidAuthorizationCodeException.class);
    }
}
