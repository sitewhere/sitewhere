/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.spi.multitenant;

import com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Microservice that contains engines for multiple tenants.
 * 
 * @author Derek
 */
public interface IMultitenantMicroservice<T extends IMicroserviceTenantEngine> extends IConfigurableMicroservice {

    /**
     * Gets unique identifier for module within the tenant configuration. This
     * is a value such as 'device-management' which can be used to locate the
     * configuration 'device-management.xml' and state file
     * 'device-management.state'.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getModuleIdentifier() throws SiteWhereException;

    /**
     * Create tenant engine specific to microservice function.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public T createTenantEngine(ITenant tenant) throws SiteWhereException;

    /**
     * Get tenant engine corresponding to the given id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public T getTenantEngineByTenantId(String id) throws SiteWhereException;
}