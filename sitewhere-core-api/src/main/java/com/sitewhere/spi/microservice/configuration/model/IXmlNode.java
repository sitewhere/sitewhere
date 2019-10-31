/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration.model;

/**
 * Node in the configuration model that contains XML information.
 */
public interface IXmlNode {

    public String getName();

    public String getIcon();

    public String getDescription();

    public NodeType getNodeType();

    public String getLocalName();

    public String getNamespace();
}