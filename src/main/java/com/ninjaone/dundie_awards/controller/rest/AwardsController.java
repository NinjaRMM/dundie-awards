package com.ninjaone.dundie_awards.controller.rest;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ninjaone.dundie_awards.validation.BlockedOrganizationId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Awards API", description = "API for managing awards")
@RequestMapping
public interface AwardsController {

	@Operation(
	        summary = "Give Dundie Awards to an organization",
	        description = """
	            Adds Dundie Awards to all employees within a specified organization. 
	            The organization must not be blocked, and no concurrent modifications should be in progress.
	        """,
	        responses = {
	            @ApiResponse(
	                responseCode = "200",
	                description = "Successfully processed the Dundie Awards for the organization."
	            ),
	            @ApiResponse(
	                responseCode = "400",
	                description = "Validation failure. The organization is blocked and cannot perform this operation.",
	                content = @Content(
	                    mediaType = "application/json",
	                    schema = @Schema(implementation = ProblemDetail.class),
	                    examples = @ExampleObject(
	                        value = """
	                        {
	                          "type": "about:blank",
	                          "title": "Bad Request",
	                          "status": 400,
	                          "detail": "Validation failure",
	                          "instance": "/give-dundie-awards/5",
	                          "errors": [
	                            {
	                              "field": "method 'giveOrganizationDundieAwards' parameter 0",
	                              "message": "The organization is blocked and cannot perform this operation."
	                            }
	                          ]
	                        }
	                        """
	                    )
	                )
	            ),
	            @ApiResponse(
	                responseCode = "404",
	                description = "Organization not found.",
	                content = @Content(
	                    mediaType = "application/json",
	                    schema = @Schema(implementation = ProblemDetail.class),
	                    examples = @ExampleObject(
	                        value = """
	                        {
	                          "type": "about:blank",
	                          "title": "Not Found",
	                          "status": 404,
	                          "detail": "Organization with ID: 123 not found.",
	                          "instance": "/give-dundie-awards/{organizationId}",
	                          "errors": null
	                        }
	                        """
	                    )
	                )
	            ),
	            @ApiResponse(
	                responseCode = "409",
	                description = "Conflict. Concurrent modification detected. Try again later.",
	                content = @Content(
	                    mediaType = "application/json",
	                    schema = @Schema(implementation = ProblemDetail.class),
	                    examples = @ExampleObject(
	                        value = """
	                        {
	                          "type": "about:blank",
	                          "title": "Conflict",
	                          "status": 409,
	                          "detail": "Concurrent modification detected. Try again later.",
	                          "instance": "/give-dundie-awards/5",
	                          "errors": [
	                            {
	                              "field": "organization",
	                              "message": "Concurrent modification detected. Try again later."
	                            }
	                          ]
	                        }
	                        """
	                    )
	                )
	            )
	        }
	    )
    @PostMapping("/give-dundie-awards/{organizationId}")
    public void giveOrganizationDundieAwards(@Valid @BlockedOrganizationId @PathVariable("organizationId") long organizationId);

}