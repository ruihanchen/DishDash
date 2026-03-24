package com.chendev.dishdash.common.exception;

import com.chendev.dishdash.common.response.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest req) {
        return buildResponse(ex.getErrorCode(), ex.getMessage(), req);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(
            BusinessException ex, HttpServletRequest req) {
        return buildResponse(ex.getErrorCode(), ex.getMessage(), req);
    }

    //Handles Valid failures on request bodies.

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest req) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return buildResponse(ErrorCode.VALIDATION_FAILED, detail, req);
    }

    //Catch-all handler.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception [{} {}]", req.getMethod(), req.getRequestURI(), ex);
        return buildResponse(
                ErrorCode.INTERNAL_ERROR,
                ErrorCode.INTERNAL_ERROR.getDefaultMessage(),
                req);
    }

    // All four handlers funnel through this single assembly method.
    private ResponseEntity<ApiError> buildResponse(
            ErrorCode code, String message, HttpServletRequest req) {
        ApiError body = ApiError.builder()
                .status(code.getHttpStatus().value())
                .error(code.name())
                .message(message)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.status(code.getHttpStatus()).body(body);
    }
}