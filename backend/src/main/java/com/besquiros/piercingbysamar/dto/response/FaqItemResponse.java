package com.besquiros.piercingbysamar.dto.response;

public record FaqItemResponse(
        Long id,
        String question,
        String answer,
        Integer position,
        Boolean active
) {}
