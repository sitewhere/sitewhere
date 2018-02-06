/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.tenant;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;

/**
 * Tenant administration operations.
 * 
 * @author Derek
 */
public interface ITenantAdministration {

    /**
     * Get list of templates available for creating a new tenant.
     * 
     * @return
     * @throws SiteWhereException
     */
    public List<ITenantTemplate> getTenantTemplates() throws SiteWhereException;
}