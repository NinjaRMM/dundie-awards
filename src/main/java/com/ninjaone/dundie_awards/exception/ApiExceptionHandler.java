package com.ninjaone.dundie_awards.exception;

import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.notFoundException;
import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.notValidException;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.Error;

import lombok.Builder;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	
	/*
	 * Customize ResponseEntityExceptionHandler handler
	 * to provide more info in case of Bean Validation exception 
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		
		List<Error> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> Error.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .toList();
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, "Invalid request content.");
        problemDetail.setProperty("errors", errors);
        return new ResponseEntity<>(problemDetail, headers, BAD_REQUEST);
    }
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
	        HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
	    String error = ofNullable(ex.getMessage())
	            .orElse("Invalid JSON input. Unable to parse the provided request.");
	    List<Error> errors = List.of(
	            Error.builder()
	                    .field("requestBody")
	                    .message(error)
	                    .build()
	    );
	    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, "Failed to read request");
	    problemDetail.setProperty("errors", errors);
	    return new ResponseEntity<>(problemDetail, headers, BAD_REQUEST);
	}
	
	/*
	 * Persistence layer exceptions mapped to Http status errors exceptions
	 */
	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<Object> handleDataAccessException(DataAccessException ex, WebRequest request) {
	    String detailMessage = ofNullable(ex.getRootCause())
	            .map(Throwable::getMessage)
	            .orElse("A database error occurred.");
	    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, detailMessage);
	    problemDetail.setProperty("errors", List.of(
	        Error.builder()
	            .field("database")  
	            .message(detailMessage)
	            .build()
	    ));
	    return new ResponseEntity<>(problemDetail, INTERNAL_SERVER_ERROR);
	}
	
	/*
	 * Service layer exceptions mapped to Http status errors exceptions
	 */
	@ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponseException handleIllegalArgumentException(IllegalArgumentException ex) {
        return notValidException.apply(ex.getMessage());
    }
	
	@ExceptionHandler(NoSuchElementException.class)
    public ErrorResponseException handleNoSuchElementException(NoSuchElementException ex) {
        return notFoundException.apply(ex.getMessage());
    }
	
	/**
	* Utility class to provide customization over {@link ErrorResponseException}.
	*/
	public static class ExceptionUtil {
		
		@Builder
		public static record Error(String field, String message) { }
		
	    public static Function<String, ErrorResponseException> notFoundException = 
			    detailMessage -> {
	                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, detailMessage);
	                problemDetail.setProperty("errors", null);
	                return new ErrorResponseException(
	                    HttpStatus.NOT_FOUND,
	                    problemDetail,
	                    null
	                );
	            };
			    
	    public static Function<String, ErrorResponseException> notValidException = 
			    detailMessage -> {
	                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, detailMessage);
	                problemDetail.setProperty("errors", null);
	                return new ErrorResponseException(
	                    HttpStatus.BAD_REQUEST,
	                    problemDetail,
	                    null
	                );
	            };
			    
	    public static Function<Long, NoSuchElementException> employeeNotFoundException = 
	    		id  -> new NoSuchElementException(format("Employee with id: %d not found", id));
		
		public static Function<Long, NoSuchElementException> organizationNotFoundException = 
	    		id  -> new NoSuchElementException(format("Organization with id: %d not found", id));
	    		
//		public static Function<Long, IllegalArgumentException> organizationNotValidException =
//                id -> new IllegalArgumentException(format("Invalid organization with id: %d. Organization not found", id));
//        
//        public static final Supplier<IllegalArgumentException> invalidIdException = 
//                () -> new IllegalArgumentException("The provided organization ID cannot be null.");

	}
	
}
