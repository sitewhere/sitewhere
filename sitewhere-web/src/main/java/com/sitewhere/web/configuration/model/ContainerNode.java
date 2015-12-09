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

/**
 * Contains nested elements, but does not represent an XML node. Used for organizing the
 * user interface.
 * 
 * @author Derek
 */
public class ContainerNode extends ConfigurationNode {

	/** List of nested containers */
	private List<ContainerNode> containers = new ArrayList<ContainerNode>();

	/** List of contained elements */
	private List<ElementNode> elements = new ArrayList<ElementNode>();

	public ContainerNode() {
		super(NodeType.Container);
	}

	public List<ContainerNode> getContainers() {
		return containers;
	}

	public void setContainers(List<ContainerNode> containers) {
		this.containers = containers;
	}

	public List<ElementNode> getElements() {
		return elements;
	}

	public void setElements(List<ElementNode> elements) {
		this.elements = elements;
	}

	/**
	 * Builder for creating element nodes.
	 * 
	 * @author Derek
	 */
	public static class Builder {

		private ContainerNode container;

		public Builder(String name) {
			this.container = new ContainerNode();
			container.setName(name);
		}

		public Builder setDescription(String description) {
			container.setDescription(description);
			return this;
		}

		public Builder addChildContainer(ContainerNode child) {
			container.getContainers().add(child);
			return this;
		}

		public Builder addElement(ElementNode element) {
			container.getElements().add(element);
			return this;
		}

		public ContainerNode build() {
			return container;
		}
	}
}