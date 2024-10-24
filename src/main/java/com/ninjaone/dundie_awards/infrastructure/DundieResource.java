package com.ninjaone.dundie_awards.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DundieResource {
    EMPLOYEE("Employee"),
    ORGANIZATION("Organization"),
    ACTIVITY("Activity");

    private final String resource;
}
