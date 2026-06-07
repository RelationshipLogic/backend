package com.relationshiplogic.domain.analysis;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // nullable — 비로그인 허용 여부 확정 전

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelationshipType relationshipType;

    @Column(columnDefinition = "TEXT")
    private String contextMemo; // 옵셔널

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InputType inputType;

    private LocalDateTime createdAt;

    public static AnalysisSession create(
            Long userId,
            RelationshipType relationshipType,
            String contextMemo,
            InputType inputType
    ) {
        AnalysisSession session = new AnalysisSession();
        session.userId = userId;
        session.relationshipType = relationshipType;
        session.contextMemo = contextMemo;
        session.inputType = inputType;
        session.createdAt = LocalDateTime.now();
        return session;
    }
}
