package com.relationshiplogic.infrastructure.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
public class RefreshTokenJpaEntity implements Persistable<String> {

    @Id
    private String token;

    private Long userId;

    private LocalDateTime expiresAt;

    @Transient
    private boolean isNew = true;

    protected RefreshTokenJpaEntity() {
    }

    public RefreshTokenJpaEntity(String token, Long userId, LocalDateTime expiresAt) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }

    @Override
    public String getId() {
        return token;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    // token(PK)이 애플리케이션에서 직접 할당되는 값이라 Hibernate가 insert/update를 구분 못 하고
    // 매번 select-then-merge를 시도하는 것을 막기 위해 Persistable로 새 엔티티 여부를 직접 알려준다.
    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }
}
