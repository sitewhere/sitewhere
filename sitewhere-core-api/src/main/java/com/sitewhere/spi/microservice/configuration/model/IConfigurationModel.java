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
 * Contains a map of all model elements grouped by role.
 * 
 * @author Derek
 */
public interface IConfigurationModel {

    /**
     * Add a new element to the model.
     * 
     * @param element
     */
    public void addElement(IElementNode element);

    /**
     * Get map of elements by role.
     * 
     * @return
     */
    public Map<String, List<IElementNode>> getElementsByRole();
}