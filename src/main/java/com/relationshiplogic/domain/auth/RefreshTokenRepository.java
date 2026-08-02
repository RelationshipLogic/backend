package com.relationshiplogic.domain.auth;

import java.util.Optional;

public interface RefreshTokenRepository {
    void save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
}
