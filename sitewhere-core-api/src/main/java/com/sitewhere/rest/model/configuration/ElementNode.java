/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.microservice.configuration.model.IAttributeGroup;
import com.sitewhere.spi.microservice.configuration.model.IAttributeNode;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModelProvider;
import com.sitewhere.spi.microservice.configuration.model.IElementNode;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;
import com.sitewhere.spi.microservice.configuration.model.NodeType;

/**
 * Configuration node associated with an XML element.
 */
@JsonInclude(Include.NON_NULL)
public class ElementNode extends XmlNode implements IElementNode {

    /** List of attribute nodes */
    private List<IAttributeNode> attributes;

    /** Element role */
    private String role;

    /** Message shown to warn users before deleting element */
    private String onDeleteWarning;

    /** Indicates roles that require specific subtypes */
    private Map<String, String> specializes;

    /** Maps attribute group names to titles */
    private Map<String, String> attributeGroups;

    /** Indicates if the element is deprecated */
    private boolean deprecated;

    public ElementNode() {
	super(NodeType.Element);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IElementNode#getAttributes
     * ()
     */
    @Override
    public List<IAttributeNode> getAttributes() {
	return attributes;
    }

    public void setAttributes(List<IAttributeNode> attributes) {
	this.attributes = attributes;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IElementNode#getRole()
     */
    @Override
    public String getRole() {
	return role;
    }

    public void setRole(String role) {
	this.role = role;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IElementNode#
     * getOnDeleteWarning()
     */
    @Override
    public String getOnDeleteWarning() {
	return onDeleteWarning;
    }

    public void setOnDeleteWarning(String onDeleteWarning) {
	this.onDeleteWarning = onDeleteWarning;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IElementNode#
     * getSpecializes()
     */
    @Override
    public Map<String, String> getSpecializes() {
	return specializes;
    }

    public void setSpecializes(Map<String, String> specializes) {
	this.specializes = specializes;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IElementNode#
     * getAttributeGroups()
     */
    @Override
    public Map<String, String> getAttributeGroups() {
	return attributeGroups;
    }

    public void setAttributeGroups(Map<String, String> attributeGroups) {
	this.attributeGroups = attributeGroups;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IElementNode#isDeprecated(
     * )
     */
    @Override
    public boolean isDeprecated() {
	return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
	this.deprecated = deprecated;
    }

    /**
     * If an attribute is used as an index, return the name.
     * 
     * @return
     */
    public String getIndexAttribute() {
	if (attributes != null) {
	    for (IAttributeNode attribute : attributes) {
		if (attribute.isIndex()) {
		    return attribute.getLocalName();
		}
	    }
	}
	return null;
    }

    /**
     * Builder for creating element nodes.
     */
    public static class Builder {

	private ElementNode element;

	public Builder(String name, String localName, String icon, IRoleKey role,
		IConfigurationModelProvider modelProvider) {
	    this.element = new ElementNode();
	    element.setName(name);
	    element.setLocalName(localName);
	    element.setNamespace(modelProvider.getDefaultXmlNamespace());
	    element.setIcon(icon);
	    element.setRole(role.getId());
	}

	public Builder description(String description) {
	    element.setDescription(description);
	    return this;
	}

	public Builder namespace(String namespace) {
	    element.setNamespace(namespace);
	    return this;
	}

	public Builder attribute(AttributeNode attribute) {
	    if (element.getAttributes() == null) {
		element.setAttributes(new ArrayList<IAttributeNode>());
	    }
	    element.getAttributes().add(attribute);
	    return this;
	}

	public Builder specializes(IRoleKey type, IRoleKey subtype) {
	    if (element.getSpecializes() == null) {
		element.setSpecializes(new HashMap<String, String>());
	    }
	    element.getSpecializes().put(type.getId(), subtype.getId());
	    return this;
	}

	public Builder attributeGroup(IAttributeGroup group) {
	    if (element.getAttributeGroups() == null) {
		element.setAttributeGroups(new HashMap<String, String>());
	    }
	    element.getAttributeGroups().put(group.getId(), group.getName());
	    return this;
	}

	public Builder warnOnDelete(String warning) {
	    element.setOnDeleteWarning(warning);
	    return this;
	}

	public Builder makeDeprecated() {
	    element.setDeprecated(true);
	    return this;
	}

	public ElementNode build() {
	    return element;
	}
    }
}