package com.sitewhere.microservice.multitenant;

import java.util.concurrent.Callable;

import org.apache.curator.framework.CuratorFramework;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.microservice.spi.multitenant.IMultitenantMicroservice;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Operation that adds a new tenant engine to an
 * {@link IMultitenantMicroservice}.
 * 
 * @author Derek
 */
public class AddTenantEngineOperation implements Callable<IMicroserviceTenantEngine> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Max time to wait for tenant to be bootstrapped from template */
    private static final long MAX_WAIT_FOR_TENANT_BOOTSTRAPPED = 5 * 1000;

    /** Parent microservice */
    private MultitenantMicroservice microservice;

    /** Tenant information */
    private ITenant tenant;

    public AddTenantEngineOperation(MultitenantMicroservice microservice, ITenant tenant) {
	this.microservice = microservice;
	this.tenant = tenant;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public IMicroserviceTenantEngine call() throws Exception {
	try {
	    LOGGER.info("Creating tenant engine for '" + getTenant().getName() + "'...");
	    IMicroserviceTenantEngine created = new MicroserviceTenantEngine(getMicroservice(), getTenant());
	    getMicroservice().getTenantEnginesByTenantId().put(getTenant().getId(), created);

	    // Configuration files must be present before initialization.
	    LOGGER.info("Verifying tenant '" + getTenant().getName() + "' configuration bootstrapped.");
	    waitForTenantConfigurationBootstrapped();

	    // Initialize new engine.
	    LOGGER.info("Intializing tenant engine for '" + getTenant().getName() + "'.");
	    ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(1, "Initialize tenant engine."));
	    long start = System.currentTimeMillis();
	    created.lifecycleInitialize(monitor);
	    if (created.getLifecycleStatus() == LifecycleStatus.InitializationError) {
		throw created.getLifecycleError();
	    }
	    LOGGER.info("Tenant engine for '" + getTenant().getName() + "' initialized in "
		    + (System.currentTimeMillis() - start) + "ms.");
	    return created;
	} finally {
	    // Make sure that tenant is cleared from the pending map.
	    getMicroservice().getPendingEnginesByTenantId().remove(getTenant().getId());
	}
    }

    /**
     * Wait until tenant configuration has been bootstrapped before starting
     * initialization.
     * 
     * @throws SiteWhereException
     */
    protected void waitForTenantConfigurationBootstrapped() throws SiteWhereException {
	CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	try {
	    long deadline = System.currentTimeMillis() + MAX_WAIT_FOR_TENANT_BOOTSTRAPPED;
	    while ((deadline - System.currentTimeMillis()) > 0) {
		if (curator.checkExists().forPath(
			getMicroservice().getInstanceTenantBootstrappedIndicatorPath(getTenant().getId())) != null) {
		    return;
		}
		Thread.sleep(500);
	    }
	    throw new SiteWhereException("Tenant not bootstrapped within time limit. Aborting");
	} catch (Throwable t) {
	    throw new SiteWhereException("Unable to wait for tenant configuration bootstrap.", t);
	}
    }

    public MultitenantMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(MultitenantMicroservice microservice) {
	this.microservice = microservice;
    }

    public ITenant getTenant() {
	return tenant;
    }

    public void setTenant(ITenant tenant) {
	this.tenant = tenant;
    }
}