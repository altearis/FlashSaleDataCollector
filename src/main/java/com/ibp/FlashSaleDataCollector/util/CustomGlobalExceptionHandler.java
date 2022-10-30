package com.ibp.FlashSaleDataCollector.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ibp.FlashSaleDataCollector.model.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	 @Override
	    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
	            HttpMediaTypeNotSupportedException ex,
	            HttpHeaders headers,
	            HttpStatus status,
	            WebRequest request) {
	        StringBuilder builder = new StringBuilder();
	        builder.append(ex.getContentType()+ " media type is not supported.");	 
			logger.debug("[93] "+ex.getLocalizedMessage());       
	        return new ResponseEntity<Object>(new APIResponse("93", builder.toString()), HttpStatus.BAD_REQUEST);
	        
	    }
	 @ExceptionHandler(javax.validation.ConstraintViolationException.class)
	    protected ResponseEntity<Object> handleConstraintViolation(javax.validation.ConstraintViolationException ex) {
		 List<Object> errorMsg = new ArrayList<>();
		 ex.getConstraintViolations().forEach(entry->{
			 errorMsg.add(entry.getMessage());
		 });
	     return new ResponseEntity<Object>(new APIResponse(errorMsg,"95", "Validation error"), HttpStatus.BAD_REQUEST);
	    }
	// error handle for @Valid
	@Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {

       List<Object> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());
	       StringBuilder sb = new StringBuilder();
	       for(Object ob:errors){
	    	   sb.append(ob.toString());
	    	   sb.append(",");
	       }
		logger.equals("[92] Validation fail. "+sb.toString());
        return new ResponseEntity<Object>(new APIResponse(errors,"92", "Validation failed"), headers, status);

    }
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {		
		String error = "Malformed JSON request"; 
		logger.debug("[91] "+ex.getLocalizedMessage());
		return new ResponseEntity<Object>(new APIResponse("91", error), status);
	}
	
	@ExceptionHandler(MissingRequestHeaderException.class)
    public final ResponseEntity<Object> handleHeaderException(Exception ex, WebRequest request) 
    { 
		logger.debug("[94] "+ex.getLocalizedMessage());
		return new ResponseEntity<Object>(new APIResponse(("94"), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
	@Override
    public final ResponseEntity<Object> handleHttpRequestMethodNotSupported( HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) 
    { 
		logger.debug("[96] "+ex.getLocalizedMessage());
		return new ResponseEntity<Object>(new APIResponse(("96"), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {	 

		logger.debug("[99] "+ex.getLocalizedMessage());
		return new ResponseEntity<Object>(new APIResponse(("99"), "Your request cannot be processed at this momment."), HttpStatus.SERVICE_UNAVAILABLE);
	}

}
