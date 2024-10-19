package com.ninjaone.dundie_awards.error;

import com.ninjaone.dundie_awards.infrastructure.DundieResource;
import lombok.Getter;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
public class NotFoundException extends DundieException {
    private final long id;
    private final DundieResource resource;

    public NotFoundException(
            long id,
            DundieResource resource
    ) {
        super(
                NOT_FOUND,
                "not.found.exception",
                "The resource was not found",
                String.format(
                        "Resource '%s' with id '%s' was not found",
                        resource.getResource(),
                        id
                )
        );
        this.id = id;
        this.resource = resource;
    }
}
