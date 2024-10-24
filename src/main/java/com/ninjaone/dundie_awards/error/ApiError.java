package com.ninjaone.dundie_awards.error;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public record ApiError(UUID id,
                       HttpStatus status,
                       String code,
                       String title,
                       String detail) {
}
