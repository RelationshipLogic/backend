package com.relationshiplogic.domain.payment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "analysis_unlocks",
    uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "grade"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisUnlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnlockGrade grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnlockMethod method;

    private Long paymentId; // AD unlock 시 null

    private LocalDateTime unlockedAt;

    public static AnalysisUnlock byAd(Long sessionId, UnlockGrade grade) {
        AnalysisUnlock unlock = new AnalysisUnlock();
        unlock.sessionId = sessionId;
        unlock.grade = grade;
        unlock.method = UnlockMethod.AD;
        unlock.paymentId = null;
        unlock.unlockedAt = LocalDateTime.now();
        return unlock;
    }

    public static AnalysisUnlock byCash(Long sessionId, UnlockGrade grade, Long paymentId) {
        AnalysisUnlock unlock = new AnalysisUnlock();
        unlock.sessionId = sessionId;
        unlock.grade = grade;
        unlock.method = UnlockMethod.CASH;
        unlock.paymentId = paymentId;
        unlock.unlockedAt = LocalDateTime.now();
        return unlock;
    }
}
