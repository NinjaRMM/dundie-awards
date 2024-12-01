package com.ninjaone.dundie_awards.controller.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ninjaone.dundie_awards.dto.EmployeeDto;
import com.ninjaone.dundie_awards.dto.EmployeeUpdateRequestDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Employee API", description = "API for managing employees")
@RequestMapping("/employees")
public interface EmployeeController {

	@Operation(
	    summary = "Fetch all employees",
	    description = "Retrieves a list of all employees.",
	    responses = {
	        @ApiResponse(
	            responseCode = "200",
	            description = "Successfully retrieved the list of employees",
	            content = @Content(
	                mediaType = "application/json",
	                array = @ArraySchema(schema = @Schema(implementation = EmployeeDto.class))
	            )
	        )
	    }
	)
    @GetMapping
    List<EmployeeDto> getAllEmployees();

    @Operation(
        summary = "Fetch employee by ID",
        description = "Retrieves the details of a specific employee by their ID.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved the employee",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = EmployeeDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
        		description = "Employee not found",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          "type": "about:blank",
                          "title": "Not Found",
                          "status": 404,
                          "detail": "Employee with id: 1 not found",
                          "instance": "/employees/0",
                          "errors": null
                        }
                        """
                    )
                )
            )
        }
    )
    @GetMapping("/{id}")
    EmployeeDto getEmployeeById(@PathVariable("id") long id);

    @Operation(
        summary = "Create a new employee",
        description = "Creates a new employee and returns the created employee.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Details of the employee to create",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EmployeeDto.class)
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Funcionário criado com sucesso",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = EmployeeDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Conteúdo da requisição inválido",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          "type": "about:blank",
                          "title": "Bad Request",
                          "status": 400,
                          "detail": "Invalid request content.",
                          "instance": "/employees",
                          "errors": [
                            {
                              "field": "firstName",
                              "message": "must not be blank"
                            },
                            {
                              "field": "lastName",
                              "message": "must not be blank"
                            }
                          ]
                        }
                        """
                    )
                )
            )
        }
    )
    @PostMapping
    @ResponseStatus(CREATED)
    EmployeeDto createEmployee(@Valid @RequestBody EmployeeDto employee);

    @Operation(
        summary = "Update an employee",
        description = "Updates an existing employee by their ID.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Details to update the employee",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EmployeeUpdateRequestDto.class)
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Employee successfully updated",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = EmployeeDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request content.",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          "type": "about:blank",
                          "title": "Bad Request",
                          "status": 400,
                          "detail": "Invalid request content.",
                          "instance": "/employees/1",
                          "errors": [
                            {
                              "field": "dundieAwards",
                              "message": "must be greater than or equal to 0"
                            }
                          ]
                        }
                        """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Employee not found",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          "type": "about:blank",
                          "title": "Not Found",
                          "status": 404,
                          "detail": "Employee with id: 1 not found",
                          "instance": "/employees/1",
                          "errors": null
                        }
                        """
                    )
                )
            )
        }
    )
    @PutMapping("/{id}")
    EmployeeDto updateEmployee(@PathVariable("id") long id, @Valid @RequestBody EmployeeUpdateRequestDto updateRequest);

    @Operation(
        summary = "Delete an employee",
        description = "Deletes an employee by their ID.",
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "Employee successfully deleted"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Employee not found",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          "type": "about:blank",
                          "title": "Not Found",
                          "status": 404,
                          "detail": "Employee with id: 1 not found",
                          "instance": "/employees/1",
                          "errors": null
                        }
                        """
                    )
                )
            )
        }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    void deleteEmployee(@PathVariable("id") long id);

}
