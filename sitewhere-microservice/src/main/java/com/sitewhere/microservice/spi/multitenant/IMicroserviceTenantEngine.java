package com.sitewhere.microservice.spi.multitenant;

import com.sitewhere.microservice.spi.configuration.IConfigurationListener;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Engine that manages operations for a single tenant within an
 * {@link IMultitenantMicroservice}.
 * 
 * @author Derek
 */
public interface IMicroserviceTenantEngine extends ITenantLifecycleComponent, IConfigurationListener {

    /**
     * Get parent microservice.
     * 
     * @return
     */
    public IMultitenantMicroservice getMicroservice();

    /**
     * Get Zk configuration path for tenant.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getTenantConfigurationPath() throws SiteWhereException;
}