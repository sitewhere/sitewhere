package com.sitewhere.spi.error;

import com.sitewhere.spi.SiteWhereException;

/**
 * Indicates that a "create" operation resulted in a duplicate key.
 * 
 * @author Derek
 */
public class ResourceExistsException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = 997625714231990638L;

    /** SiteWhere error code */
    private ErrorCode code;

    public ResourceExistsException(ErrorCode code) {
	this.code = code;
    }

    public ErrorCode getCode() {
	return code;
    }

    public void setCode(ErrorCode code) {
	this.code = code;
    }
}