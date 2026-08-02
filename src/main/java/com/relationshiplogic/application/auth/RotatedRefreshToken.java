package com.relationshiplogic.application.auth;

public record RotatedRefreshToken(String token, Long userId) {
}
