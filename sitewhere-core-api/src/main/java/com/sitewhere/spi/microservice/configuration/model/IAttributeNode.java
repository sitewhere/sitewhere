/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration.model;

import java.util.List;

/**
 * Node representing an attribute in the configuration model.
 */
public interface IAttributeNode extends IXmlNode {

    public AttributeType getType();

    public String getDefaultValue();

    public boolean isIndex();

    public List<IAttributeChoice> getChoices();

    public boolean isRequired();

    public String getGroup();
}