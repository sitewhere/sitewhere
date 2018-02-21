/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.microservice;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.asset.DirectAssetResolver;
import com.sitewhere.asset.grpc.AssetManagementImpl;
import com.sitewhere.asset.initializer.GroovyAssetModelInitializer;
import com.sitewhere.asset.modules.AssetManagementTriggers;
import com.sitewhere.asset.modules.AssetModuleManagementAdapter;
import com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine;
import com.sitewhere.asset.spi.modules.IAssetModuleManager;
import com.sitewhere.grpc.service.AssetManagementGrpc;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.microservice.hazelcast.server.CacheAwareAssetManagement;
import com.sitewhere.microservice.hazelcast.server.CacheAwareAssetModuleManagement;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
import com.sitewhere.spi.microservice.spring.AssetManagementBeans;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements asset
 * management functionality.
 * 
 * @author Derek
 */
public class AssetManagementTenantEngine extends MicroserviceTenantEngine implements IAssetManagementTenantEngine {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(AssetManagementTenantEngine.class);

    /** Asset resolver */
    private IAssetResolver assetResolver;

    /** Asset management persistence API */
    private IAssetManagement assetManagement;

    /** Asset module manager */
    private IAssetModuleManager assetModuleManager;

    /** Responds to asset management GRPC requests */
    private AssetManagementGrpc.AssetManagementImplBase assetManagementImpl;

    public AssetManagementTenantEngine(IMultitenantMicroservice<?> microservice, ITenant tenant) {
	super(microservice, tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Initialize underlying APIs.
	initializeAssetManagementApis();

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize discoverable lifecycle components.
	init.addStep(initializeDiscoverableBeans(getModuleContext()));

	// Initialize asset management persistence.
	init.addInitializeStep(this, getAssetManagement(), true);

	// Initialize asset module manager.
	init.addInitializeStep(this, getAssetModuleManager(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /**
     * Initialize underlying APIs from Spring.
     * 
     * @throws SiteWhereException
     */
    protected void initializeAssetManagementApis() throws SiteWhereException {
	// Load Spring bean implementations.
	IAssetManagement implementation = (IAssetManagement) getModuleContext()
		.getBean(AssetManagementBeans.BEAN_ASSET_MANAGEMENT);
	this.assetModuleManager = (IAssetModuleManager) getModuleContext()
		.getBean(AssetManagementBeans.BEAN_ASSET_MODULE_MANAGER);
	getAssetModuleManager().setAssetManagement(implementation);

	this.assetManagement = new CacheAwareAssetManagement(
		new AssetManagementTriggers(implementation, getAssetModuleManager(), getMicroservice()));
	this.assetResolver = new DirectAssetResolver(getAssetManagement(), new CacheAwareAssetModuleManagement(
		new AssetModuleManagementAdapter(getAssetModuleManager()), getMicroservice()));
	this.assetManagementImpl = new AssetManagementImpl(getAssetResolver());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantStart(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start discoverable lifecycle components.
	start.addStep(startDiscoverableBeans(getModuleContext()));

	// Start asset management persistence.
	start.addStartStep(this, getAssetManagement(), true);

	// Start asset module manager.
	start.addStartStep(this, getAssetModuleManager(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantBootstrap(com.sitewhere.microservice.spi.multitenant. ITenantTemplate,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantBootstrap(ITenantTemplate template, ILifecycleProgressMonitor monitor) throws SiteWhereException {
	List<String> scripts = template.getInitializers().getAssetManagement();
	for (String script : scripts) {
	    getTenantScriptSynchronizer().add(script);
	}

	GroovyConfiguration groovy = new GroovyConfiguration(getTenantScriptSynchronizer());
	groovy.start(new LifecycleProgressMonitor(new LifecycleProgressContext(1, "Initialize asset model."),
		getMicroservice()));
	for (String script : scripts) {
	    GroovyAssetModelInitializer initializer = new GroovyAssetModelInitializer(groovy, script);
	    initializer.initialize(getAssetResolver());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantStop(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop asset management persistence.
	stop.addStopStep(this, getAssetManagement());

	// Stop asset module manager.
	stop.addStopStep(this, getAssetModuleManager());

	// Stop discoverable lifecycle components.
	stop.addStep(stopDiscoverableBeans(getModuleContext()));

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine#
     * getAssetResolver()
     */
    @Override
    public IAssetResolver getAssetResolver() {
	return assetResolver;
    }

    public void setAssetResolver(IAssetResolver assetResolver) {
	this.assetResolver = assetResolver;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine#
     * getAssetManagement()
     */
    @Override
    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    /*
     * @see com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine#
     * getAssetModuleManager()
     */
    @Override
    public IAssetModuleManager getAssetModuleManager() {
	return assetModuleManager;
    }

    public void setAssetModuleManager(IAssetModuleManager assetModuleManager) {
	this.assetModuleManager = assetModuleManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine#
     * getAssetManagementImpl()
     */
    @Override
    public AssetManagementGrpc.AssetManagementImplBase getAssetManagementImpl() {
	return assetManagementImpl;
    }

    public void setAssetManagementImpl(AssetManagementGrpc.AssetManagementImplBase assetManagementImpl) {
	this.assetManagementImpl = assetManagementImpl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }
}