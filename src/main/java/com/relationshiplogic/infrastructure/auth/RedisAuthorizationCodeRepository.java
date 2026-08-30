package com.relationshiplogic.infrastructure.auth;

import com.relationshiplogic.domain.auth.AuthorizationCode;
import com.relationshiplogic.domain.auth.AuthorizationCodeRepository;
import com.relationshiplogic.domain.support.ClockHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

@Repository
public class RedisAuthorizationCodeRepository implements AuthorizationCodeRepository {

    private static final String KEY_PREFIX = "auth:code:";

    private final StringRedisTemplate redisTemplate;
    private final ClockHolder clockHolder;

    public RedisAuthorizationCodeRepository(StringRedisTemplate redisTemplate, ClockHolder clockHolder) {
        this.redisTemplate = redisTemplate;
        this.clockHolder = clockHolder;
    }

    @Override
    public void save(AuthorizationCode authorizationCode) {
        String key = key(authorizationCode.getCode());
        String value = authorizationCode.getUserId() + "|" + authorizationCode.getExpiresAt();
        Duration ttl = Duration.between(clockHolder.now(), authorizationCode.getExpiresAt());

        redisTemplate.opsForValue().set(key, value, ttl);
    }

    @Override
    public Optional<AuthorizationCode> findByCode(String code) {
        String value = redisTemplate.opsForValue().get(key(code));
        if (value == null) {
            return Optional.empty();
        }

        String[] parts = value.split("\\|");
        Long userId = Long.valueOf(parts[0]);
        LocalDateTime expiresAt = LocalDateTime.parse(parts[1]);

        return Optional.of(AuthorizationCode.of(code, userId, expiresAt));
    }

    @Override
    public void deleteByCode(String code) {
        redisTemplate.delete(key(code));
    }

    private String key(String code) {
        return KEY_PREFIX + hash(code);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
