/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.datatype.JsonDateSerializer;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Merges information from an {@link IDeviceCommand} and an
 * {@link IDeviceCommandInvocation} in a form that is easy to use in a user
 * interface.
 * 
 * @author Derek
 */
public class DeviceCommandInvocationSummary extends MetadataProvider implements Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -1400511380311663352L;

    /** Command name */
    private String name;

    /** Command namespace */
    private String namespace;

    /** Formatted invocation date */
    private Date invocationDate;

    private List<Parameter> parameters = new ArrayList<Parameter>();

    /** List of responses for the command invocation */
    private List<Response> responses = new ArrayList<Response>();

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getNamespace() {
	return namespace;
    }

    public void setNamespace(String namespace) {
	this.namespace = namespace;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getInvocationDate() {
	return invocationDate;
    }

    public void setInvocationDate(Date invocationDate) {
	this.invocationDate = invocationDate;
    }

    public List<Parameter> getParameters() {
	return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
	this.parameters = parameters;
    }

    public List<Response> getResponses() {
	return responses;
    }

    public void setResponses(List<Response> responses) {
	this.responses = responses;
    }

    /** Holder for parameter information */
    public static class Parameter {

	/** Parameter name */
	private String name;

	/** Parameter type */
	private String type;

	/** Parameter value */
	private String value;

	/** Required indicator */
	private boolean required;

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getType() {
	    return type;
	}

	public void setType(String type) {
	    this.type = type;
	}

	public String getValue() {
	    return value;
	}

	public void setValue(String value) {
	    this.value = value;
	}

	public boolean isRequired() {
	    return required;
	}

	public void setRequired(boolean required) {
	    this.required = required;
	}
    }

    /** Information about a response to the invocation */
    public static class Response {

	/** Parameter name */
	private String description;

	/** Parameter type */
	private Date date;

	public String getDescription() {
	    return description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getDate() {
	    return date;
	}

	public void setDate(Date date) {
	    this.date = date;
	}
    }
}