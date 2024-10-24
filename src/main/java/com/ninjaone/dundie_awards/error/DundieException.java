package com.ninjaone.dundie_awards.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
public class DundieException extends RuntimeException {
    private final ApiError apiError;

    public DundieException(HttpStatus status,
                           String code,
                           String title,
                           String detail) {
        super(detail);

        this.apiError = new ApiError(
                UUID.randomUUID(),
                status,
                code,
                title,
                detail
        );
    }
}
