/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller-level information parsed from annotations.
 * 
 * @author Derek
 */
public class ParsedController {

	/** Controller name */
	private String name;

	/** Resource type */
	private String resource;

	/** Base URI for methods */
	private String baseUri;

	/** Description */
	private String description;

	/** List of methods */
	private List<ParsedMethod> methods = new ArrayList<ParsedMethod>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ParsedMethod> getMethods() {
		return methods;
	}

	public void setMethods(List<ParsedMethod> methods) {
		this.methods = methods;
	}
}