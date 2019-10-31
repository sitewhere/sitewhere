/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.configuration;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IAttributeChoice;
import com.sitewhere.spi.microservice.configuration.model.IAttributeGroup;
import com.sitewhere.spi.microservice.configuration.model.IAttributeNode;
import com.sitewhere.spi.microservice.configuration.model.NodeType;

/**
 * Configuration node associated with an XML element attribute.
 */
@JsonInclude(Include.NON_NULL)
public class AttributeNode extends XmlNode implements IAttributeNode {

    /** Attribute type */
    private AttributeType type;

    /** Default value */
    private String defaultValue;

    /** Indicates if field is the unique index */
    private boolean index;

    /** Choices available for attribute */
    private List<IAttributeChoice> choices;

    /** Indicates if attribute is required */
    private boolean required;

    /** Name for grouping related attributes */
    private String group;

    public AttributeNode() {
	super(NodeType.Attribute);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IAttributeNode#getType()
     */
    @Override
    public AttributeType getType() {
	return type;
    }

    public void setType(AttributeType type) {
	this.type = type;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IAttributeNode#
     * getDefaultValue()
     */
    @Override
    public String getDefaultValue() {
	return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
	this.defaultValue = defaultValue;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IAttributeNode#isIndex()
     */
    @Override
    public boolean isIndex() {
	return index;
    }

    public void setIndex(boolean index) {
	this.index = index;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IAttributeNode#getChoices(
     * )
     */
    @Override
    public List<IAttributeChoice> getChoices() {
	return choices;
    }

    public void setChoices(List<IAttributeChoice> choices) {
	this.choices = choices;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IAttributeNode#isRequired(
     * )
     */
    @Override
    public boolean isRequired() {
	return required;
    }

    public void setRequired(boolean required) {
	this.required = required;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IAttributeNode#getGroup()
     */
    @Override
    public String getGroup() {
	return group;
    }

    public void setGroup(String group) {
	this.group = group;
    }

    /**
     * Builder for creating attribute nodes.
     */
    public static class Builder {

	private AttributeNode attribute;

	public Builder(String name, String localName, AttributeType type, IAttributeGroup group) {
	    this.attribute = new AttributeNode();
	    attribute.setName(name);
	    attribute.setLocalName(localName);
	    attribute.setType(type);
	    attribute.setGroup(group.getId());
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
	    attribute.setRequired(true);
	    return this;
	}

	public Builder choice(String name, String value) {
	    if (attribute.getChoices() == null) {
		attribute.setChoices(new ArrayList<IAttributeChoice>());
	    }
	    attribute.getChoices().add(new AttributeChoice(name, value));
	    return this;
	}

	public AttributeNode build() {
	    return attribute;
	}
    }
}