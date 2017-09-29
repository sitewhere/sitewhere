package com.sitewhere.web.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sitewhere.grpc.model.security.NotAuthorizedException;
import com.sitewhere.grpc.model.security.UnauthenticatedException;
import com.sitewhere.microservice.security.JwtExpiredException;
import com.sitewhere.rest.ISiteWhereWebConstants;
import com.sitewhere.spi.SiteWhereSystemException;

/**
 * Common exception handling for REST responses.
 * 
 * @author Derek
 */
@ControllerAdvice
public class RestExceptionHandling extends ResponseEntityExceptionHandler {

    /**
     * Handle unauthorized requests.
     * 
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(value = { NotAuthorizedException.class })
    protected ResponseEntity<Object> handleNotAuthorized(NotAuthorizedException e, WebRequest request) {
	return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    /**
     * Handles unauthenticated access.
     * 
     * @param e
     * @param response
     */
    @ExceptionHandler(value = { UnauthenticatedException.class })
    protected ResponseEntity<Object> handleUnauthenticated(NotAuthorizedException e, WebRequest request) {
	return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    /**
     * Handles expired JWT credentials.
     * 
     * @param e
     * @param response
     */
    @ExceptionHandler(value = { JwtExpiredException.class })
    protected ResponseEntity<Object> handleJwtExpired(NotAuthorizedException e, WebRequest request) {
	return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    /**
     * Handles a system exception by setting the HTML response code and response
     * headers.
     * 
     * @param e
     * @param response
     */
    @ExceptionHandler(value = { SiteWhereSystemException.class })
    protected ResponseEntity<Object> handleSystemException(SiteWhereSystemException e, WebRequest request) {
	String combined = e.getCode() + ":" + e.getMessage();
	HttpHeaders headers = new HttpHeaders();
	headers.add(ISiteWhereWebConstants.HEADER_SITEWHERE_ERROR, e.getMessage());
	headers.add(ISiteWhereWebConstants.HEADER_SITEWHERE_ERROR_CODE, String.valueOf(e.getCode()));
	HttpStatus responseCode = (e.hasHttpResponseCode()) ? HttpStatus.valueOf(e.getHttpResponseCode())
		: HttpStatus.BAD_REQUEST;
	return handleExceptionInternal(e, combined, headers, responseCode, request);
    }
}