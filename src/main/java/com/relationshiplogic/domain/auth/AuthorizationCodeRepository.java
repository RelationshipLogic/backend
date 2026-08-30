package com.relationshiplogic.domain.auth;

import java.util.Optional;

public interface AuthorizationCodeRepository {

    void save(AuthorizationCode authorizationCode);

    Optional<AuthorizationCode> findByCode(String code);

    void deleteByCode(String code);
}
