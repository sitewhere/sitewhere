package com.sitewhere.microservice.multitenant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.microservice.spi.multitenant.IMultitenantMicroservice;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
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

    /** Parent microservice */
    private IMultitenantMicroservice microservice;

    public MicroserviceTenantEngine(IMultitenantMicroservice microservice, ITenant tenant) {
	this.microservice = microservice;
	setTenant(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationCacheInitialized()
     */
    @Override
    public void onConfigurationCacheInitialized() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationAdded(java.lang.String, byte[])
     */
    @Override
    public void onConfigurationAdded(String path, byte[] data) {
	getLogger().info("Tenant engine configuration path added: " + path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationUpdated(java.lang.String, byte[])
     */
    @Override
    public void onConfigurationUpdated(String path, byte[] data) {
	getLogger().info("Tenant engine configuration path updated: " + path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationDeleted(java.lang.String)
     */
    @Override
    public void onConfigurationDeleted(String path) {
	getLogger().info("Tenant engine configuration path deleted: " + path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getTenantConfigurationPath()
     */
    @Override
    public String getTenantConfigurationPath() throws SiteWhereException {
	return getMicroservice().getInstanceTenantConfigurationPath(getTenant().getId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getMicroservice()
     */
    @Override
    public IMultitenantMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMultitenantMicroservice microservice) {
	this.microservice = microservice;
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