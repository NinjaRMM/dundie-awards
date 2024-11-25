package com.ninjaone.dundie_awards.exception;

import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.notFoundException;
import static com.ninjaone.dundie_awards.exception.ApiExceptionHandler.ExceptionUtil.notValidException;
import static java.lang.String.format;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
	
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
		
		public static Function<String, ErrorResponseException> notFoundException = 
			    detailMessage -> new ErrorResponseException(
			        HttpStatus.NOT_FOUND,
			        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detailMessage),
			        null
			    );
			    
	    public static Function<String, ErrorResponseException> notValidException = 
			    detailMessage -> new ErrorResponseException(
			        HttpStatus.BAD_REQUEST,
			        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detailMessage),
			        null
			    );
	    
	    public static Function<Long, NoSuchElementException> employeeNotFoundException = 
	    		id  -> new NoSuchElementException(format("Employee with id: %d not found", id));
	    		
		public static Function<Long, IllegalArgumentException> organizationNotValidException =
                id -> new IllegalArgumentException(format("Invalid organization with id: %d. Organization not found", id));
        
        public static final Supplier<IllegalArgumentException> invalidIdException = 
                () -> new IllegalArgumentException("The provided organization ID cannot be null.");

	}
}
