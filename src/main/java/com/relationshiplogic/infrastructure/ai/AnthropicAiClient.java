package com.relationshiplogic.infrastructure.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// TODO: Anthropic Java SDK 의존성 추가 후 구현
@Component
public class AnthropicAiClient implements AiClient {

    @Value("${ai.anthropic.api-key}")
    private String apiKey;

    private static final String MODEL = "claude-sonnet-4-6";

    @Override
    public String call(String systemPrompt, String userMessage) {
        throw new UnsupportedOperationException("Anthropic 클라이언트 구현 필요");
    }
}
