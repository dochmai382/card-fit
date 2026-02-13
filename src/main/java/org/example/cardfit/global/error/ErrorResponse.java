package org.example.cardfit.global.error;

import java.util.List;

public record ErrorResponse(
        String message,
        List<String> details
) {
    public static ErrorResponse of(String message) {
        return new ErrorResponse(message, List.of());
    }

    public static ErrorResponse of(String message, List<String> details) {
        return new ErrorResponse(message, details);
    }

    public static ErrorResponse of(ErrorCode code) {
        return new ErrorResponse(code.getMessage(), List.of());
    }

    public static ErrorResponse of(ErrorCode code, List<String> details) {
        return new ErrorResponse(code.getMessage(), details);
    }
}
