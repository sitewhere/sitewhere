/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.service;

import java.io.IOException;
import java.util.List;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;

import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

/**
 * Uses extra information passed by SiteWhere in headers to provide more information about errors.
 * 
 * @author Derek
 */
public class SiteWhereErrorHandler implements ResponseErrorHandler {

	/** Delegate to default error handler */
	private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.client.ResponseErrorHandler#handleError(org.springframework.http.client.
	 * ClientHttpResponse)
	 */
	public void handleError(ClientHttpResponse response) throws IOException {
		String errorCode = null;
		List<String> codeList = response.getHeaders().get(ISiteWhereWebConstants.HEADER_SITEWHERE_ERROR_CODE);
		if ((codeList != null) && (codeList.size() > 0)) {
			errorCode = codeList.get(0);
		}
		try {
			errorHandler.handleError(response);
		} catch (RestClientException e) {
			if (errorCode != null) {
				ErrorCode code = ErrorCode.valueOf(errorCode);
				throw new SiteWhereSystemException(code, ErrorLevel.ERROR, response.getRawStatusCode());
			} else {
				throw new SiteWhereSystemException(ErrorCode.Unknown, ErrorLevel.ERROR,
						response.getRawStatusCode());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.client.ResponseErrorHandler#hasError(org.springframework.http.client.
	 * ClientHttpResponse)
	 */
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return errorHandler.hasError(response);
	}
}