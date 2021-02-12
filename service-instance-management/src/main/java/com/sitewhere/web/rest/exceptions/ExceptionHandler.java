/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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