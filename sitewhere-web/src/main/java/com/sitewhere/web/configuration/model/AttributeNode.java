/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration.model;

/**
 * Configuration node associated with an XML element attribute.
 * 
 * @author Derek
 */
public class AttributeNode extends XmlNode {

	/** Attribute type */
	private AttributeType type;

	/** Indicates if attribute is required */
	private boolean required;

	public AttributeNode() {
		super(NodeType.Attribute);
	}

	public AttributeType getType() {
		return type;
	}

	public void setType(AttributeType type) {
		this.type = type;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
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

		public Builder setDescription(String description) {
			attribute.setDescription(description);
			return this;
		}

		public Builder makeRequired() {
			attribute.setRequired(true);
			return this;
		}

		public AttributeNode build() {
			return attribute;
		}
	}
}