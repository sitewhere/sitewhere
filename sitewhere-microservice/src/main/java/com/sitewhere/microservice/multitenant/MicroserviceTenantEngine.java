package com.sitewhere.microservice.multitenant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.microservice.spi.multitenant.IMultitenantMicroservice;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Specialized tenant engine that runs within an
 * {@link IMultitenantMicroservice}.
 * 
 * @author Derek
 */
public class MicroserviceTenantEngine extends TenantLifecycleComponent implements IMicroserviceTenantEngine {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public MicroserviceTenantEngine(ITenant tenant) {
	setTenant(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}