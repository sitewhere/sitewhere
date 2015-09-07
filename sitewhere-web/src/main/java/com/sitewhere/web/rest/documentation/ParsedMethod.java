/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Method-level information parsed from annotations.
 * 
 * @author Derek
 */
public class ParsedMethod {

	/** Base URL */
	private String baseUri;

	/** Relative URI for method */
	private String relativeUri;

	/** Request method */
	private RequestMethod requestMethod;

	/** HTML description */
	private String description;

	public String getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public String getRelativeUri() {
		return relativeUri;
	}

	public void setRelativeUri(String relativeUri) {
		this.relativeUri = relativeUri;
	}

	public RequestMethod getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(RequestMethod requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}