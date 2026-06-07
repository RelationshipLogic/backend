package com.relationshiplogic.domain.analysis;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 심리 엔진 프롬프트(system_v2) 응답을 저장하는 MongoDB Document.
 * 필드 구조는 experiments/psych-engine/prompts/system_v2.md 의 JSON 스키마를 따른다.
 */
@Document(collection = "analysis_results")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisResult {

    @Id
    private String id;

    private Long sessionId;

    private String tier;            // "basic" | "deep" | "next_move"
    private String relationType;    // "썸" | "연인" | "전연인" | "짝사랑" | "친구" | "직장"
    private boolean crisisDetected;

    private BasicBlock basic;
    private DeepBlock deep;         // tier = deep or next_move 일 때만 존재
    private NextMoveBlock nextMove; // tier = next_move 일 때만 존재
    private FunComment funComment;  // MBTI or 사주 입력된 경우에만 존재

    private String disclaimer;
    private LocalDateTime createdAt;

    public static AnalysisResult create(
            Long sessionId,
            String tier,
            String relationType,
            boolean crisisDetected,
            BasicBlock basic,
            DeepBlock deep,
            NextMoveBlock nextMove,
            FunComment funComment,
            String disclaimer
    ) {
        AnalysisResult result = new AnalysisResult();
        result.sessionId = sessionId;
        result.tier = tier;
        result.relationType = relationType;
        result.crisisDetected = crisisDetected;
        result.basic = basic;
        result.deep = deep;
        result.nextMove = nextMove;
        result.funComment = funComment;
        result.disclaimer = disclaimer;
        result.createdAt = LocalDateTime.now();
        return result;
    }

    // -------------------------------------------------------------------------
    // basic 블록
    // -------------------------------------------------------------------------

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class BasicBlock {

        private String headline;
        private SignalEntry keySignal;
        private List<SignalEntry> supportingSignals; // 최소 2개, 최대 4개
        private String frameworkSummary;
        private String overallAssessment;
        private String teaserHiddenIntent;
        private String teaserNextMove;
        private String trendVsLast; // 재분석 시에만 존재

        public List<SignalEntry> getSupportingSignals() {
            return Collections.unmodifiableList(supportingSignals);
        }

        public static BasicBlock of(
                String headline,
                SignalEntry keySignal,
                List<SignalEntry> supportingSignals,
                String frameworkSummary,
                String overallAssessment,
                String teaserHiddenIntent,
                String teaserNextMove,
                String trendVsLast
        ) {
            BasicBlock b = new BasicBlock();
            b.headline = headline;
            b.keySignal = keySignal;
            b.supportingSignals = List.copyOf(supportingSignals);
            b.frameworkSummary = frameworkSummary;
            b.overallAssessment = overallAssessment;
            b.teaserHiddenIntent = teaserHiddenIntent;
            b.teaserNextMove = teaserNextMove;
            b.trendVsLast = trendVsLast;
            return b;
        }
    }

    // -------------------------------------------------------------------------
    // deep 블록
    // -------------------------------------------------------------------------

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DeepBlock {

        private List<SignalDetail> signalDetail;
        private FrameworkDetail frameworkDetail;
        private List<RiskSignal> riskSignals;
        private List<String> calibrationFlags;

        public List<SignalDetail> getSignalDetail() {
            return Collections.unmodifiableList(signalDetail);
        }

        public List<RiskSignal> getRiskSignals() {
            return Collections.unmodifiableList(riskSignals);
        }

        public List<String> getCalibrationFlags() {
            return Collections.unmodifiableList(calibrationFlags);
        }

        public static DeepBlock of(
                List<SignalDetail> signalDetail,
                FrameworkDetail frameworkDetail,
                List<RiskSignal> riskSignals,
                List<String> calibrationFlags
        ) {
            DeepBlock d = new DeepBlock();
            d.signalDetail = List.copyOf(signalDetail);
            d.frameworkDetail = frameworkDetail;
            d.riskSignals = List.copyOf(riskSignals);
            d.calibrationFlags = List.copyOf(calibrationFlags);
            return d;
        }
    }

    // -------------------------------------------------------------------------
    // next_move 블록
    // -------------------------------------------------------------------------

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class NextMoveBlock {

        private String situationRead;
        private List<RecommendedReply> recommendedReplies;
        private String eventStrategy;
        private String expectedResponse;
        private String mbtiToneNote; // MBTI 입력된 경우에만 존재

        public List<RecommendedReply> getRecommendedReplies() {
            return Collections.unmodifiableList(recommendedReplies);
        }

        public static NextMoveBlock of(
                String situationRead,
                List<RecommendedReply> recommendedReplies,
                String eventStrategy,
                String expectedResponse,
                String mbtiToneNote
        ) {
            NextMoveBlock n = new NextMoveBlock();
            n.situationRead = situationRead;
            n.recommendedReplies = List.copyOf(recommendedReplies);
            n.eventStrategy = eventStrategy;
            n.expectedResponse = expectedResponse;
            n.mbtiToneNote = mbtiToneNote;
            return n;
        }
    }

    // -------------------------------------------------------------------------
    // 공유 Value Object
    // -------------------------------------------------------------------------

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignalEntry {
        private String label;       // signal_id (ex. reply_speed_change)
        private String observation; // 대화에서 관찰된 사실
        private String interpretation;

        public static SignalEntry of(String label, String observation, String interpretation) {
            SignalEntry s = new SignalEntry();
            s.label = label;
            s.observation = observation;
            s.interpretation = interpretation;
            return s;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignalDetail {
        private String signalId;
        private String strength;        // "강함" | "중간" | "약함"
        private String confidence;      // "높음" | "중간" | "낮음"
        private int evidenceCount;
        private List<String> rawEvidence;
        private String allowedInterpretation;
        private String forbiddenCheck;

        public List<String> getRawEvidence() {
            return Collections.unmodifiableList(rawEvidence);
        }

        public static SignalDetail of(
                String signalId,
                String strength,
                String confidence,
                int evidenceCount,
                List<String> rawEvidence,
                String allowedInterpretation,
                String forbiddenCheck
        ) {
            SignalDetail s = new SignalDetail();
            s.signalId = signalId;
            s.strength = strength;
            s.confidence = confidence;
            s.evidenceCount = evidenceCount;
            s.rawEvidence = List.copyOf(rawEvidence);
            s.allowedInterpretation = allowedInterpretation;
            s.forbiddenCheck = forbiddenCheck;
            return s;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FrameworkDetail {
        private String primary;
        private String primaryResult;
        private String secondary;       // nullable
        private String secondaryResult; // nullable

        public static FrameworkDetail of(
                String primary,
                String primaryResult,
                String secondary,
                String secondaryResult
        ) {
            FrameworkDetail f = new FrameworkDetail();
            f.primary = primary;
            f.primaryResult = primaryResult;
            f.secondary = secondary;
            f.secondaryResult = secondaryResult;
            return f;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RiskSignal {
        private String type; // "Gottman_비난" | "가스라이팅" | "조종" 등
        private boolean detected;
        private String confidence; // "높음" | "중간" | "낮음"
        private String basis;

        public static RiskSignal of(String type, boolean detected, String confidence, String basis) {
            RiskSignal r = new RiskSignal();
            r.type = type;
            r.detected = detected;
            r.confidence = confidence;
            r.basis = basis;
            return r;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RecommendedReply {
        private String style;   // "직구형" | "부드럽게" | "유머형"
        private String message;

        public static RecommendedReply of(String style, String message) {
            RecommendedReply r = new RecommendedReply();
            r.style = style;
            r.message = message;
            return r;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FunComment {
        private String label;   // "재미로 보는 해석"
        private String content;

        public static FunComment of(String label, String content) {
            FunComment f = new FunComment();
            f.label = label;
            f.content = content;
            return f;
        }
    }
}
