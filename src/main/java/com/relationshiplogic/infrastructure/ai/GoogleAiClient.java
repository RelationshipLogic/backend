package com.relationshiplogic.infrastructure.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// TODO: Google Generative AI Java SDK 의존성 추가 후 구현
@Component
public class GoogleAiClient implements AiClient {

    @Value("${ai.google.api-key}")
    private String apiKey;

    private static final String MODEL = "gemini-2.0-flash-lite";

    @Override
    public String call(String systemPrompt, String userMessage) {
        throw new UnsupportedOperationException("Google AI 클라이언트 구현 필요");
    }
}
