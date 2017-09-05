/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Configuration node associated with an XML element attribute.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class AttributeNode extends XmlNode {

    /** Attribute type */
    private AttributeType type;

    /** Default value */
    private String defaultValue;

    /** Indicates if field is the unique index */
    private boolean index;

    /** Choices available for attribute */
    private List<String> choices;

    /** Indicates if attribute is required */
    private boolean required;

    /** Name for grouping related attributes */
    private String group;

    public AttributeNode() {
	super(NodeType.Attribute);
    }

    public AttributeType getType() {
	return type;
    }

    public void setType(AttributeType type) {
	this.type = type;
    }

    public String getDefaultValue() {
	return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
	this.defaultValue = defaultValue;
    }

    public boolean isIndex() {
	return index;
    }

    public void setIndex(boolean index) {
	this.index = index;
    }

    public List<String> getChoices() {
	return choices;
    }

    public void setChoices(List<String> choices) {
	this.choices = choices;
    }

    public boolean isRequired() {
	return required;
    }

    public void setRequired(boolean required) {
	this.required = required;
    }

    public String getGroup() {
	return group;
    }

    public void setGroup(String group) {
	this.group = group;
    }

    /**
     * Builder for creating attribute nodes.
     * 
     * @author Derek
     */
    public static class Builder {

	private AttributeNode attribute;

	public Builder(String name, String localName, AttributeType type) {
	    this.attribute = new AttributeNode();
	    attribute.setName(name);
	    attribute.setLocalName(localName);
	    attribute.setType(type);
	}

	public Builder description(String description) {
	    attribute.setDescription(description);
	    return this;
	}

	public Builder defaultValue(String value) {
	    attribute.setDefaultValue(value);
	    return this;
	}

	public Builder makeRequired() {
	    attribute.setRequired(true);
	    return this;
	}

	public Builder makeIndex() {
	    attribute.setIndex(true);
	    return this;
	}

	public Builder choice(String choice) {
	    if (attribute.getChoices() == null) {
		attribute.setChoices(new ArrayList<String>());
	    }
	    attribute.getChoices().add(choice);
	    return this;
	}

	public Builder group(String group) {
	    attribute.setGroup(group);
	    return this;
	}

	public AttributeNode build() {
	    return attribute;
	}
    }
}