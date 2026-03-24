package com.chendev.dishdash.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import java.time.Instant;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final T data;
    private final String message;
    private final Instant timestamp = Instant.now();

    private ApiResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, message);
    }

    /** For operations with no response body (DELETE, PATCH status updates). */
    public static ApiResponse<Void> noContent() {
        return new ApiResponse<>(null, null);
    }
}