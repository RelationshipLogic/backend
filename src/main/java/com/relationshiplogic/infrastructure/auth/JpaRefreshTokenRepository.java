package com.relationshiplogic.infrastructure.auth;

import com.relationshiplogic.domain.auth.RefreshToken;
import com.relationshiplogic.domain.auth.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaRefreshTokenRepository implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;

    @Override
    public void save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity entity = new RefreshTokenJpaEntity(
                refreshToken.getToken(), refreshToken.getUserId(), refreshToken.getExpiresAt());
        jpaRepository.save(entity);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRepository.findById(token)
                .map(entity -> RefreshToken.of(entity.getToken(), entity.getUserId(), entity.getExpiresAt()));
    }

    @Override
    public void deleteByToken(String token) {
        jpaRepository.deleteById(token);
    }
}
