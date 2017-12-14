/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.multitenant;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.context.ApplicationContext;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.configuration.ConfigurationUtils;
import com.sitewhere.microservice.groovy.TenantEngineScriptSynchronizer;
import com.sitewhere.rest.model.microservice.state.TenantEngineState;
import com.sitewhere.rest.model.tenant.TenantTemplate;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Specialized tenant engine that runs within an
 * {@link IMultitenantMicroservice}.
 * 
 * @author Derek
 */
public abstract class MicroserviceTenantEngine extends TenantEngineLifecycleComponent
	implements IMicroserviceTenantEngine {

    /** Suffix appended to module identifier for lock path */
    public static final String MODULE_LOCK_SUFFIX = ".lock";

    /** Suffix appended to module identifier to locate module configuration */
    public static final String MODULE_CONFIGURATION_SUFFIX = ".xml";

    /** Suffix appended to module identifier to indicate data is bootstrapped */
    public static final String MODULE_BOOTSTRAPPED_SUFFIX = ".boot";

    /** Tenant template path (relative to configuration root) */
    public static final String TENANT_TEMPLATE_PATH = "/template.json";

    /** Parent microservice */
    private IMultitenantMicroservice<?> microservice;

    /** Hosted tenant */
    private ITenant tenant;

    /** Tenant script synchronizer */
    private TenantEngineScriptSynchronizer tenantScriptSynchronizer;

    /** Module context information */
    private ApplicationContext moduleContext;

    public MicroserviceTenantEngine(IMultitenantMicroservice<?> microservice, ITenant tenant) {
	this.microservice = microservice;
	this.tenant = tenant;
	this.tenantScriptSynchronizer = new TenantEngineScriptSynchronizer(this);
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

	// Allow subclass to execute initialization logic.
	tenantInitialize(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getModuleConfiguration()
     */
    @Override
    public byte[] getModuleConfiguration() throws SiteWhereException {
	try {
	    CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	    if (curator.checkExists().forPath(getModuleConfigurationPath()) == null) {
		throw new SiteWhereException("Module configuration '" + getModuleConfigurationPath()
			+ "' does not exist for '" + getTenant().getName() + "'.");
	    }
	    byte[] data = curator.getData().forPath(getModuleConfigurationPath());
	    return data;
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to load module configuration.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * updateModuleConfiguration(byte[])
     */
    @Override
    public void updateModuleConfiguration(byte[] content) throws SiteWhereException {
	try {
	    CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	    if (curator.checkExists().forPath(getModuleConfigurationPath()) == null) {
		curator.create().forPath(getModuleConfigurationPath(), content);
	    } else {
		curator.setData().forPath(getModuleConfigurationPath(), content);
	    }
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to update module configuration.", e);
	}
    }

    /**
     * Load Spring configuration into module context.
     * 
     * @throws SiteWhereException
     */
    protected void loadModuleConfiguration() throws SiteWhereException {
	try {
	    byte[] data = getModuleConfiguration();
	    this.moduleContext = ConfigurationUtils.buildSubcontext(data, getMicroservice().getVersion(),
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
	// Allow subclass to execute startup logic.
	tenantStart(monitor);
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
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#setLifecycleStatus(com.
     * sitewhere.spi.server.lifecycle.LifecycleStatus)
     */
    @Override
    public void setLifecycleStatus(LifecycleStatus lifecycleStatus) {
	super.setLifecycleStatus(lifecycleStatus);
	try {
	    getMicroservice().onTenantEngineStateChanged(getCurrentState());
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to calculate current state.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getCurrentState()
     */
    @Override
    public ITenantEngineState getCurrentState() throws SiteWhereException {
	TenantEngineState state = new TenantEngineState();
	state.setMicroserviceDetails(getMicroservice().getMicroserviceDetails());
	state.setLifecycleStatus(getLifecycleStatus());
	state.setTenantId(getTenant().getId());
	return state;
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
	getLogger().debug("Tenant engine configuration path added: " + path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationUpdated(java.lang.String, byte[])
     */
    @Override
    public void onConfigurationUpdated(String path, byte[] data) {
	getLogger().debug("Tenant engine configuration path updated: " + path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationDeleted(java.lang.String)
     */
    @Override
    public void onConfigurationDeleted(String path) {
	getLogger().debug("Tenant engine configuration path deleted: " + path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getTenantTemplate()
     */
    @Override
    public ITenantTemplate getTenantTemplate() throws SiteWhereException {
	String templatePath = getTenantConfigurationPath() + TENANT_TEMPLATE_PATH;
	CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	try {
	    byte[] data = curator.getData().forPath(templatePath);
	    return MarshalUtils.unmarshalJson(data, TenantTemplate.class);
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to load tenant template from Zk.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getTenantConfigurationPath()
     */
    @Override
    public String getTenantConfigurationPath() throws SiteWhereException {
	return getMicroservice().getInstanceTenantConfigurationPath(getTenant().getId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getModuleLockPath()
     */
    @Override
    public String getModuleLockPath() throws SiteWhereException {
	return getTenantConfigurationPath() + "/" + getMicroservice().getIdentifier()
		+ MicroserviceTenantEngine.MODULE_LOCK_SUFFIX;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getModuleConfigurationPath()
     */
    @Override
    public String getModuleConfigurationPath() throws SiteWhereException {
	return getTenantConfigurationPath() + "/" + getMicroservice().getIdentifier()
		+ MicroserviceTenantEngine.MODULE_CONFIGURATION_SUFFIX;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getModuleBootstrappedPath()
     */
    @Override
    public String getModuleBootstrappedPath() throws SiteWhereException {
	return getTenantConfigurationPath() + "/" + getMicroservice().getIdentifier()
		+ MicroserviceTenantEngine.MODULE_BOOTSTRAPPED_SUFFIX;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getMicroservice()
     */
    @Override
    public IMultitenantMicroservice<?> getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMultitenantMicroservice<?> microservice) {
	this.microservice = microservice;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getTenant()
     */
    @Override
    public ITenant getTenant() {
	return tenant;
    }

    public void setTenant(ITenant tenant) {
	this.tenant = tenant;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getTenantScriptSynchronizer()
     */
    @Override
    public TenantEngineScriptSynchronizer getTenantScriptSynchronizer() {
	return tenantScriptSynchronizer;
    }

    public void setTenantScriptSynchronizer(TenantEngineScriptSynchronizer tenantScriptSynchronizer) {
	this.tenantScriptSynchronizer = tenantScriptSynchronizer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * getModuleContext()
     */
    @Override
    public ApplicationContext getModuleContext() {
	return moduleContext;
    }

    public void setModuleContext(ApplicationContext moduleContext) {
	this.moduleContext = moduleContext;
    }
}