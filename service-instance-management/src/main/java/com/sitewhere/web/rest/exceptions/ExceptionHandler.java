/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.web.ISiteWhereWebConstants;

/**
 * Common error handler for {@link SiteWhereException}.
 */
@Provider
public class ExceptionHandler implements ExceptionMapper<SiteWhereException> {

    /*
     * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
     */
    @Override
    public Response toResponse(SiteWhereException e) {
	if (e instanceof SiteWhereSystemException) {
	    SiteWhereSystemException sys = (SiteWhereSystemException) e;

	    ExceptionDetail detail = new ExceptionDetail();
	    detail.setMessage(sys.getMessage());
	    detail.setErrorCode(sys.getCode().getCode());
	    detail.setErrorDescription(sys.getCode().getMessage());

	    int responseCode = sys.getHttpResponseCode() == -1 ? 500 : sys.getHttpResponseCode();
	    ResponseBuilder builder = Response.status(responseCode).entity(detail);
	    builder.header(ISiteWhereWebConstants.HEADER_SITEWHERE_ERROR, sys.getMessage());
	    builder.header(ISiteWhereWebConstants.HEADER_SITEWHERE_ERROR_CODE, sys.getCode().getCode());
	    return builder.build();
	} else {
	    ExceptionDetail detail = new ExceptionDetail();
	    detail.setMessage(e.getMessage());

	    ResponseBuilder builder = Response.status(Status.INTERNAL_SERVER_ERROR).entity(detail);
	    builder.header(ISiteWhereWebConstants.HEADER_SITEWHERE_ERROR, e.getMessage());
	    return builder.build();
	}
    }
}