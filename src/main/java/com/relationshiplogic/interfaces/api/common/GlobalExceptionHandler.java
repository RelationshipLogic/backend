package com.relationshiplogic.interfaces.api.common;

import com.relationshiplogic.domain.auth.InvalidAuthorizationCodeException;
import com.relationshiplogic.domain.auth.InvalidRefreshTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 인가 코드가 존재하지 않거나 만료된 경우. 원인을 구분해 노출하지 않고 통일된 메시지로 400을 응답한다.
    @ExceptionHandler(InvalidAuthorizationCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAuthorizationCode(InvalidAuthorizationCodeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("인가 코드가 유효하지 않습니다."));
    }

    // 리프레시 토큰이 존재하지 않거나 만료된 경우. 원인을 구분해 노출하지 않고 통일된 메시지로 401을 응답한다.
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("인증에 실패했습니다."));
    }
}
