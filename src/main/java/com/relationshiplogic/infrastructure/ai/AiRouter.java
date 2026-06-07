package com.relationshiplogic.infrastructure.ai;

import com.relationshiplogic.domain.payment.UnlockGrade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 결제 등급(UnlockGrade)에 따라 적절한 AiClient 구현체를 반환한다.
 *
 * BASIC     → Claude Sonnet 4.6  (AnthropicAiClient)  ~25원/건
 * DEEP_SCAN → Gemini Flash Lite  (GoogleAiClient)      ~6원/건
 * NEXT_MOVE → Claude Sonnet 4.6  (AnthropicAiClient)  ~25원/건
 */
@Component
@RequiredArgsConstructor
public class AiRouter {

    private final AnthropicAiClient anthropicAiClient;
    private final GoogleAiClient googleAiClient;

    public AiClient route(UnlockGrade grade) {
        return switch (grade) {
            case BASIC, NEXT_MOVE -> anthropicAiClient;
            case DEEP_SCAN -> googleAiClient;
        };
    }
}
