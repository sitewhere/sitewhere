package com.sitewhere.microservice.spi.multitenant;

import com.sitewhere.microservice.spi.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.SiteWhereException;

/**
 * Microservice that contains engines for multiple tenants.
 * 
 * @author Derek
 */
public interface IMultitenantMicroservice extends IConfigurableMicroservice {

    /**
     * Get tenant engine corresponding to the given id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IMicroserviceTenantEngine getTenantEngineByTenantId(String id) throws SiteWhereException;
}