package com.relationshiplogic.domain.archive;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 사용자가 저장한 분석 결과 요약.
 * 원문·대화 내용은 저장하지 않는다. 핵심 시그널·결과 요약만 보관.
 * RAG 전 단계 — 인물별 묶어보기와 직전 결과 라이트 비교 용도.
 */
@Document(collection = "analysis_archives")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisArchive {

    @Id
    private String id;

    @Indexed
    private Long userId;

    private Long sessionId;

    private String personAlias;     // 상대 별명 (사용자 입력)
    private String relationType;    // "썸" | "연인" | ...

    private String summary;         // overall_assessment 요약
    private String headline;        // basic.headline

    private List<String> keySignalLabels; // 핵심 시그널 ID 목록
    private List<String> riskFlags;       // 감지된 위험 신호 타입 목록

    private LocalDateTime savedAt;

    public List<String> getKeySignalLabels() {
        return Collections.unmodifiableList(keySignalLabels);
    }

    public List<String> getRiskFlags() {
        return Collections.unmodifiableList(riskFlags);
    }

    public static AnalysisArchive create(
            Long userId,
            Long sessionId,
            String personAlias,
            String relationType,
            String summary,
            String headline,
            List<String> keySignalLabels,
            List<String> riskFlags
    ) {
        AnalysisArchive archive = new AnalysisArchive();
        archive.userId = userId;
        archive.sessionId = sessionId;
        archive.personAlias = personAlias;
        archive.relationType = relationType;
        archive.summary = summary;
        archive.headline = headline;
        archive.keySignalLabels = List.copyOf(keySignalLabels);
        archive.riskFlags = List.copyOf(riskFlags);
        archive.savedAt = LocalDateTime.now();
        return archive;
    }
}
