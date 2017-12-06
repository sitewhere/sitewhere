/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.content;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Corresponds to an XML element in a configuration.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class ElementContent extends XmlContent {

    /** Create a random UUID for the element */
    private String id = UUID.randomUUID().toString();

    /** List of children for this element */
    private List<ElementContent> children;

    /** List of attributes for this element */
    private List<AttributeContent> attributes;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public List<ElementContent> getChildren() {
	return children;
    }

    public void setChildren(List<ElementContent> children) {
	this.children = children;
    }

    public List<AttributeContent> getAttributes() {
	return attributes;
    }

    public void setAttributes(List<AttributeContent> attributes) {
	this.attributes = attributes;
    }
}