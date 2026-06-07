package com.relationshiplogic.domain.payment;

public enum PaymentStatus {
    PENDING,    // 결제 요청됨
    PAID,       // 결제 완료
    FAILED,     // 결제 실패
    CANCELLED   // 취소됨
}
