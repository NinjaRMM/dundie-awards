package com.ninjaone.dundie_awards.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler
    private ResponseEntity<ApiError> dundieExceptionHandler(DundieException exception) {

        HttpStatus status = exception.getApiError().status();

        // Log and throw can be an antipattern, but I feel it's better to
        // at the API layer
        if (status.is4xxClientError()) {
            // We may not want to log 4xx at all
            log.warn("An API error occurred", exception);
        } else if (status.is5xxServerError()) {
            log.error("An API error occurred", exception);
        }

        return ResponseEntity
                .status(status)
                .body(exception.getApiError());
    }
}
