package com.nd2k.follow_up.config;

import com.nd2k.follow_up.baby.core.domain.BabyNotFoundException;
import com.nd2k.follow_up.baby.core.domain.ParentNotFoundException;
import com.nd2k.follow_up.baby.core.domain.UnauthorizedBabyAccessException;
import com.nd2k.follow_up.feed.core.domain.FeedNotFoundException;
import com.nd2k.follow_up.feed.core.domain.UnauthorizedFeedAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionController {

    @SuppressWarnings("unused")
    @ExceptionHandler(FeedNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(FeedNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @SuppressWarnings("unused")
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(UnauthorizedBabyAccessException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedBabyAccessException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }

    @SuppressWarnings("unused")
    @ExceptionHandler({BabyNotFoundException.class, ParentNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(UnauthorizedFeedAccessException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedFeed(UnauthorizedFeedAccessException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }
}
