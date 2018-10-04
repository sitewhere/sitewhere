/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.microservice;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.asset.grpc.AssetManagementImpl;
import com.sitewhere.asset.initializer.GroovyAssetModelInitializer;
import com.sitewhere.asset.spi.microservice.IAssetManagementMicroservice;
import com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine;
import com.sitewhere.grpc.service.AssetManagementGrpc;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
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

    /** Asset management persistence API */
    private IAssetManagement assetManagement;

    /** Responds to asset management GRPC requests */
    private AssetManagementGrpc.AssetManagementImplBase assetManagementImpl;

    public AssetManagementTenantEngine(ITenant tenant) {
	super(tenant);
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

	// Execute initialization steps.
	init.execute(monitor);
    }

    /**
     * Initialize underlying APIs from Spring.
     * 
     * @throws SiteWhereException
     */
    protected void initializeAssetManagementApis() throws SiteWhereException {
	this.assetManagement = (IAssetManagement) getModuleContext()
		.getBean(AssetManagementBeans.BEAN_ASSET_MANAGEMENT);
	this.assetManagementImpl = new AssetManagementImpl((IAssetManagementMicroservice) getMicroservice(),
		getAssetManagement());
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

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantBootstrap(com.sitewhere.spi.microservice.multitenant.IDatasetTemplate,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantBootstrap(IDatasetTemplate template, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	List<String> scripts = Collections.emptyList();
	if (template.getInitializers() != null) {
	    scripts = template.getInitializers().getAssetManagement();
	    for (String script : scripts) {
		getTenantScriptSynchronizer().add(script);
	    }
	}

	// Execute remote calls as superuser.
	Authentication previous = SecurityContextHolder.getContext().getAuthentication();
	try {
	    SecurityContextHolder.getContext()
		    .setAuthentication(getMicroservice().getSystemUser().getAuthenticationForTenant(getTenant()));
	    GroovyConfiguration groovy = new GroovyConfiguration(getTenantScriptSynchronizer());
	    groovy.start(new LifecycleProgressMonitor(new LifecycleProgressContext(1, "Initialize asset model."),
		    getMicroservice()));
	    for (String script : scripts) {
		GroovyAssetModelInitializer initializer = new GroovyAssetModelInitializer(groovy, script);
		initializer.initialize(getAssetManagement());
	    }
	} finally {
	    SecurityContextHolder.getContext().setAuthentication(previous);
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

	// Stop discoverable lifecycle components.
	stop.addStep(stopDiscoverableBeans(getModuleContext()));

	// Execute shutdown steps.
	stop.execute(monitor);
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
}