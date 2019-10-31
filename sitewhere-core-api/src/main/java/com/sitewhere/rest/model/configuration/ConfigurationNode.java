/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.configuration;

import com.sitewhere.spi.microservice.configuration.model.NodeType;

/**
 * Base class for all configuration node types.
 */
public class ConfigurationNode {

    /** Display name */
    private String name;

    /** Display icon */
    private String icon = "gears";

    /** Display description */
    private String description;

    /** Node type */
    private NodeType nodeType;

    public ConfigurationNode(NodeType nodeType) {
	this.nodeType = nodeType;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getIcon() {
	return icon;
    }

    public void setIcon(String icon) {
	this.icon = icon;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public NodeType getNodeType() {
	return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
	this.nodeType = nodeType;
    }
}