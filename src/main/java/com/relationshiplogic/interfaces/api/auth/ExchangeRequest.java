package com.relationshiplogic.interfaces.api.auth;

import jakarta.validation.constraints.NotBlank;

public record ExchangeRequest(
        @NotBlank String code
) {
}
