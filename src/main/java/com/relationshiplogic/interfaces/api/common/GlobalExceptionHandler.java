package com.relationshiplogic.interfaces.api.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 인가 코드/리프레시 토큰이 존재하지 않거나 만료된 경우 AuthorizationCodeService, RefreshTokenService가 던진다.
    // 원인을 구분해 노출하지 않고 통일된 메시지로 401을 응답한다.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("인증에 실패했습니다."));
    }
}
