package com.sitewhere.grpc.model.spi;

import com.sitewhere.spi.SiteWhereException;

/**
 * Exception indicating that a SiteWhere API is not available.
 * 
 * @author Derek
 */
public class ApiNotAvailableException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = 9168984351535124935L;

    public ApiNotAvailableException() {
    }

    public ApiNotAvailableException(String message, Throwable cause) {
	super(message, cause);
    }

    public ApiNotAvailableException(String message) {
	super(message);
    }

    public ApiNotAvailableException(Throwable cause) {
	super(cause);
    }
}