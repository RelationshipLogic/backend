package com.relationshiplogic.infrastructure.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class AppOAuth2User implements OAuth2User {

    private final Long userId;
    private final OAuth2User delegate;

    public AppOAuth2User(Long userId, OAuth2User delegate) {
        this.userId = userId;
        this.delegate = delegate;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return delegate.getAuthorities();
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }
}
