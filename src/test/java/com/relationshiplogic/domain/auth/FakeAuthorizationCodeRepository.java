package com.relationshiplogic.domain.auth;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FakeAuthorizationCodeRepository implements AuthorizationCodeRepository {

    private final Map<String, AuthorizationCode> store = new ConcurrentHashMap<>();

    @Override
    public void save(AuthorizationCode authorizationCode) {
        store.put(authorizationCode.getCode(), authorizationCode);
    }

    @Override
    public Optional<AuthorizationCode> findByCode(String code) {
        return Optional.ofNullable(store.get(code));
    }

    @Override
    public void deleteByCode(String code) {
        store.remove(code);
    }
}
