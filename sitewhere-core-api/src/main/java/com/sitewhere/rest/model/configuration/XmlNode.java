/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.configuration;

import com.sitewhere.spi.microservice.configuration.model.IXmlNode;
import com.sitewhere.spi.microservice.configuration.model.NodeType;

/**
 * Common base class for nodes that wrap XML content.
 */
public abstract class XmlNode extends ConfigurationNode implements IXmlNode {

    /** XML local name */
    private String localName;

    /** XML namespace */
    private String namespace;

    public XmlNode(NodeType type) {
	super(type);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IXmlNode#getLocalName()
     */
    @Override
    public String getLocalName() {
	return localName;
    }

    public void setLocalName(String localName) {
	this.localName = localName;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IXmlNode#getNamespace()
     */
    @Override
    public String getNamespace() {
	return namespace;
    }

    public void setNamespace(String namespace) {
	this.namespace = namespace;
    }
}