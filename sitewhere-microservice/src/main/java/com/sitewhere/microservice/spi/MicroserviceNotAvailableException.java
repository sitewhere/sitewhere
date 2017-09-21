package com.sitewhere.microservice.spi;

import com.sitewhere.spi.SiteWhereException;

/**
 * Indicates exception due to microservice lifeycle.
 * 
 * @author Derek
 */
public class MicroserviceNotAvailableException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = -1003297732383971503L;

    public MicroserviceNotAvailableException() {
    }

    public MicroserviceNotAvailableException(String message, Throwable cause) {
	super(message, cause);
    }

    public MicroserviceNotAvailableException(String message) {
	super(message);
    }

    public MicroserviceNotAvailableException(Throwable cause) {
	super(cause);
    }
}