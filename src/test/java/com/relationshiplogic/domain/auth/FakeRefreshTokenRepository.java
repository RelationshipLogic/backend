package com.relationshiplogic.domain.auth;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FakeRefreshTokenRepository implements RefreshTokenRepository {

    private final Map<String, RefreshToken> store = new ConcurrentHashMap<>();

    @Override
    public void save(RefreshToken refreshToken) {
        store.put(refreshToken.getToken(), refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return Optional.ofNullable(store.get(token));
    }

    @Override
    public void deleteByToken(String token) {
        store.remove(token);
    }
}
