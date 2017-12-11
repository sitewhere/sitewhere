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
 * Entity used to resolve a microservice configuration model.
 * 
 * @author Derek
 */
public interface IConfigurationModelProvider {

    /**
     * Get default XML namespace used for elements that do not provide one.
     * 
     * @return
     */
    public String getDefaultXmlNamespace();

    /**
     * Get root role for model.
     * 
     * @return
     */
    public IConfigurationRoleProvider getRootRole();

    /**
     * Get elements by role.
     * 
     * @return
     */
    public Map<String, List<IElementNode>> getElementsByRole();

    /**
     * Get other configuration model that serve as dependencies.
     * 
     * @return
     */
    public List<IConfigurationModel> getDependencies();

    /**
     * Build the configuration model.
     * 
     * @return
     */
    public IConfigurationModel buildModel();
}