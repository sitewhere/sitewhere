/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration.model;

import java.util.List;
import java.util.Map;

/**
 * Node representing an element in the configuration model.
 */
public interface IElementNode extends IXmlNode {

    public List<IAttributeNode> getAttributes();

    public String getRole();

    public String getOnDeleteWarning();

    public Map<String, String> getSpecializes();

    public Map<String, String> getAttributeGroups();

    public boolean isDeprecated();
}