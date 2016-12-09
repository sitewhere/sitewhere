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

import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Method-level information parsed from annotations.
 * 
 * @author Derek
 */
public class ParsedMethod {

    /** Method name */
    private String name;

    /** Short description */
    private String summary;

    /** Base URL */
    private String baseUri;

    /** Relative URI for method */
    private String relativeUri;

    /** Request method */
    private RequestMethod requestMethod;

    /** HTML description */
    private String description;

    /** List of parsed parameters */
    private List<ParsedParameter> parameters = new ArrayList<ParsedParameter>();

    /** List of examples */
    private List<ParsedExample> examples = new ArrayList<ParsedExample>();

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getSummary() {
	return summary;
    }

    public void setSummary(String summary) {
	this.summary = summary;
    }

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

    public List<ParsedParameter> getParameters() {
	return parameters;
    }

    public void setParameters(List<ParsedParameter> parameters) {
	this.parameters = parameters;
    }

    public List<ParsedExample> getExamples() {
	return examples;
    }

    public void setExamples(List<ParsedExample> examples) {
	this.examples = examples;
    }
}