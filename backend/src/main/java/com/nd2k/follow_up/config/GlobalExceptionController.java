package com.nd2k.follow_up.config;

import com.nd2k.follow_up.feed.core.domain.exception.UnauthorizedFeedAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionController {

    @SuppressWarnings("unused")
    @ExceptionHandler(UnauthorizedFeedAccessException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedFeedAccessException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }
}
