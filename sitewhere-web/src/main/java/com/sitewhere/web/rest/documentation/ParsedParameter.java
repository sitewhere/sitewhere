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
 * Parameter-level information parsed from annotations.
 * 
 * @author Derek
 */
public class ParsedParameter {

	public static enum ParameterType {

		/** Parameter on URL path */
		Path,

		/** Request parameter */
		Request;
	}

	/** Parameter name */
	private String name;

	/** Parameter type */
	private ParameterType type;

	/** Is parameter required? */
	private boolean required;

	/** Cross-cutting concerns */
	private List<String> concerns = new ArrayList<String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ParameterType getType() {
		return type;
	}

	public void setType(ParameterType type) {
		this.type = type;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public List<String> getConcerns() {
		return concerns;
	}

	public void setConcerns(List<String> concerns) {
		this.concerns = concerns;
	}
}