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

import com.sitewhere.spi.microservice.state.IMicroserviceDetails;

/**
 * Contains a map of all model elements grouped by role.
 */
public interface IConfigurationModel {

    /**
     * Get information about microservice that owns the model.
     * 
     * @return
     */
    public IMicroserviceDetails getMicroserviceDetails();

    /**
     * Get default XML namespace used for elements that do not provide one.
     * 
     * @return
     */
    public String getDefaultXmlNamespace();

    /**
     * Get root role id.
     * 
     * @return
     */
    public String getRootRoleId();

    /**
     * Get map of elements by role.
     * 
     * @return
     */
    public Map<String, List<IElementNode>> getElementsByRole();

    /**
     * Get map of element roles by id.
     * 
     * @return
     */
    public Map<String, IElementRole> getRolesById();
}