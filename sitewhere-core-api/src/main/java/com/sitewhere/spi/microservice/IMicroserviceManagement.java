/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;

/**
 * Management interface offered by all microservices.
 * 
 * @author Derek
 */
public interface IMicroserviceManagement {

    /**
     * Get configuration model for microservice.
     * 
     * @return
     * @throws SiteWhereException
     */
    public IConfigurationModel getConfigurationModel() throws SiteWhereException;

    /**
     * Get configuration for a global microservice.
     * 
     * @return
     * @throws SiteWhereException
     */
    public byte[] getGlobalConfiguration() throws SiteWhereException;

    /**
     * Get configuration for a tenant microservice.
     * 
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    public byte[] getTenantConfiguration(String tenantId) throws SiteWhereException;

    /**
     * Update configuration for a global microservice.
     * 
     * @param config
     * @throws SiteWhereException
     */
    public void updateGlobalConfiguration(byte[] config) throws SiteWhereException;

    /**
     * Update configuration for a tenant microservice.
     * 
     * @param tenantId
     * @param config
     * @throws SiteWhereException
     */
    public void updateTenantConfiguration(String tenantId, byte[] config) throws SiteWhereException;
}