package com.relationshiplogic.infrastructure.auth;

import com.relationshiplogic.domain.support.ClockHolder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtProvider {

    private static final Duration ACCESS_TOKEN_TTL = Duration.ofMinutes(30);

    private final String secret;
    private final ClockHolder clockHolder;

    public JwtProvider(@Value("${jwt.secret}") String secret, ClockHolder clockHolder) {
        this.secret = secret;
        this.clockHolder = clockHolder;
    }

    public String generateAccessToken(Long userId) {
        LocalDateTime issuedAt = clockHolder.now();
        LocalDateTime expiration = issuedAt.plus(ACCESS_TOKEN_TTL);
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

        JwtBuilder jwtBuilder = Jwts.builder()
                .signWith(secretKey)
                .subject(userId.toString())
                .issuedAt(Date.from(issuedAt.atZone(ZoneId.systemDefault()).toInstant()))
                .expiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()));

        return jwtBuilder.compact();
    }

    public Long parseUserId(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        Jws<Claims> claimsJws = Jwts.parser()
                .clock(() -> Date.from(clockHolder.now().atZone(ZoneId.systemDefault()).toInstant()))
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
        Long userId = Long.parseLong(claimsJws.getPayload().getSubject());

        return userId;
    }
}
