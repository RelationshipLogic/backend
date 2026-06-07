package com.relationshiplogic.infrastructure.ai;

/**
 * AI 모델 호출 인터페이스.
 * 결제 등급별로 AnthropicAiClient / GoogleAiClient 구현체를 선택한다.
 */
public interface AiClient {

    /**
     * @param systemPrompt 시스템 프롬프트 (system_v2.md 기반)
     * @param userMessage  분석 입력 (관계 유형 + 티어 + 대화 내용 조합)
     * @return LLM 응답 JSON 문자열
     */
    String call(String systemPrompt, String userMessage);
}
