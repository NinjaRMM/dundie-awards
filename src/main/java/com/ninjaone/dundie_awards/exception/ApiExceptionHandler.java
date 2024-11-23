package com.ninjaone.dundie_awards.exception;

import static java.lang.String.format;

import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
	
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
	    
	    public static Function<Long, ErrorResponseException> employeeNotFoundException = 
	    		id  -> notFoundException.apply(format("Employee with id: %d not found", id));
	    		
		public static Function<Long, ErrorResponseException> organizationNotFoundException = 
				id  -> notFoundException.apply(format("Organization with id: %d not found", id));
		
		public static Function<Long, ErrorResponseException> organizationNotValidException =
                id -> notValidException.apply(format("Invalid organization with id: %d. Organization not found", id));
	}
}
