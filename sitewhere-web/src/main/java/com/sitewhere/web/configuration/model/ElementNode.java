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
 * Configuration node associated with an XML element.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class ElementNode extends XmlNode {

	/** List of attribute nodes */
	private List<AttributeNode> attributes;

	/** List of contained elements */
	private List<ElementNode> elements;

	/** Element role */
	private String role;

	/** Message shown to warn users before deleting element */
	private String onDeleteWarning;

	public ElementNode() {
		super(NodeType.Element);
	}

	public List<AttributeNode> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<AttributeNode> attributes) {
		this.attributes = attributes;
	}

	public List<ElementNode> getElements() {
		return elements;
	}

	public void setElements(List<ElementNode> elements) {
		this.elements = elements;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getOnDeleteWarning() {
		return onDeleteWarning;
	}

	public void setOnDeleteWarning(String onDeleteWarning) {
		this.onDeleteWarning = onDeleteWarning;
	}

	/**
	 * If an attribute is used as an index, return the name.
	 * 
	 * @return
	 */
	public String getIndexAttribute() {
		if (attributes != null) {
			for (AttributeNode attribute : attributes) {
				if (attribute.isIndex()) {
					return attribute.getLocalName();
				}
			}
		}
		return null;
	}

	/**
	 * Builder for creating element nodes.
	 * 
	 * @author Derek
	 */
	public static class Builder {

		private ElementNode element;

		public Builder(String name, String localName, String icon, ElementRole role) {
			this.element = new ElementNode();
			element.setName(name);
			element.setLocalName(localName);
			element.setIcon(icon);
			element.setRole(role.name());
		}

		public Builder setDescription(String description) {
			element.setDescription(description);
			return this;
		}

		public Builder addAttribute(AttributeNode attribute) {
			if (element.getAttributes() == null) {
				element.setAttributes(new ArrayList<AttributeNode>());
			}
			element.getAttributes().add(attribute);
			return this;
		}

		public Builder addElement(ElementNode child) {
			if (element.getElements() == null) {
				element.setElements(new ArrayList<ElementNode>());
			}
			element.getElements().add(child);
			return this;
		}

		public Builder setRequired(boolean required) {
			element.setRequired(required);
			return this;
		}

		public Builder warnOnDelete(String warning) {
			element.setOnDeleteWarning(warning);
			return this;
		}

		public ElementNode build() {
			return element;
		}
	}
}