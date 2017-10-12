package com.sitewhere.microservice.multitenant;

import org.apache.curator.framework.CuratorFramework;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.sitewhere.SiteWhere;
import com.sitewhere.configuration.ConfigurationUtils;
import com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.microservice.spi.multitenant.IMultitenantMicroservice;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Specialized tenant engine that runs within an
 * {@link IMultitenantMicroservice}.
 * 
 * @author Derek
 */
public abstract class MicroserviceTenantEngine extends TenantLifecycleComponent implements IMicroserviceTenantEngine {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Parent microservice */
    private IMultitenantMicroservice microservice;

    /** Module context information */
    private ApplicationContext moduleContext;

    public MicroserviceTenantEngine(IMultitenantMicroservice microservice, ITenant tenant) {
	this.microservice = microservice;
	setTenant(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	loadModuleConfiguration();
    }

    /**
     * Load Spring configuration into module context.
     * 
     * @throws SiteWhereException
     */
    protected void loadModuleConfiguration() throws SiteWhereException {
	try {
	    CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	    if (curator.checkExists().forPath(getModuleConfigurationPath()) == null) {
		throw new SiteWhereException("Module configuration '" + getModuleConfigurationPath()
			+ "' does not exist for '" + getTenant().getName() + "'.");
	    }
	    byte[] data = curator.getData().forPath(getModuleConfigurationPath());
	    this.moduleContext = ConfigurationUtils.buildSubcontext(data, SiteWhere.getVersion(),
		    getMicroservice().getInstanceGlobalContext());
	    getLogger().info("Successfully loaded module configuration from '" + getModuleConfigurationPath() + "'.");
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to load module configuration.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#terminate(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void terminate(ILifecycleProgressMonitor monitor) throws SiteWhereException {
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
     * getModuleConfigurationPath()
     */
    @Override
    public String getModuleConfigurationPath() throws SiteWhereException {
	return getTenantConfigurationPath() + "/" + getMicroservice().getModuleIdentifier()
		+ MultitenantMicroservice.MODULE_CONFIGURATION_SUFFIX;
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
     * @see
     * com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getModuleContext()
     */
    @Override
    public ApplicationContext getModuleContext() {
	return moduleContext;
    }

    public void setModuleContext(ApplicationContext moduleContext) {
	this.moduleContext = moduleContext;
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