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
	private List<AttributeNode> attributes = new ArrayList<AttributeNode>();

	/** List of contained elements */
	private List<ElementNode> elements = new ArrayList<ElementNode>();

	/** Element role */
	private ElementRole role;

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

	public ElementRole getRole() {
		return role;
	}

	public void setRole(ElementRole role) {
		this.role = role;
	}

	/**
	 * If an attribute is used as an index, return the name.
	 * 
	 * @return
	 */
	public String getIndexAttribute() {
		for (AttributeNode attribute : attributes) {
			if (attribute.isIndex()) {
				return attribute.getLocalName();
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
			element.setRole(role);
		}

		public Builder setDescription(String description) {
			element.setDescription(description);
			return this;
		}

		public Builder addAttribute(AttributeNode attribute) {
			element.getAttributes().add(attribute);
			return this;
		}

		public Builder addElement(ElementNode child) {
			element.getElements().add(child);
			return this;
		}

		public ElementNode build() {
			return element;
		}
	}
}