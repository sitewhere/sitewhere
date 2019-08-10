/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.instance;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;

/**
 * API for instance management operations.
 */
public interface IInstanceManagement {

    /**
     * Get list of tenant templates available for creating a new tenant.
     * 
     * @return
     * @throws SiteWhereException
     */
    public List<ITenantTemplate> getTenantTemplates() throws SiteWhereException;

    /**
     * Get list of dataset templates available for creating a new tenant.
     * 
     * @return
     * @throws SiteWhereException
     */
    public List<IDatasetTemplate> getDatasetTemplates() throws SiteWhereException;
}
