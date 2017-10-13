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