/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.microservice;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.device.grpc.DeviceManagementImpl;
import com.sitewhere.device.initializer.GroovyDeviceModelInitializer;
import com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice;
import com.sitewhere.device.spi.microservice.IDeviceManagementTenantEngine;
import com.sitewhere.grpc.model.spi.client.IAssetManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.service.DeviceManagementGrpc;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.microservice.hazelcast.server.CacheAwareDeviceManagement;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
import com.sitewhere.spi.microservice.spring.DeviceManagementBeans;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements device
 * management functionality.
 * 
 * @author Derek
 */
public class DeviceManagementTenantEngine extends MicroserviceTenantEngine implements IDeviceManagementTenantEngine {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Device management persistence API */
    private IDeviceManagement deviceManagement;

    /** Responds to device management GRPC requests */
    private DeviceManagementGrpc.DeviceManagementImplBase deviceManagementImpl;

    public DeviceManagementTenantEngine(IMultitenantMicroservice<IDeviceManagementTenantEngine> microservice,
	    ITenant tenant) {
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
	// Create management interfaces.
	IDeviceManagement implementation = (IDeviceManagement) getModuleContext()
		.getBean(DeviceManagementBeans.BEAN_DEVICE_MANAGEMENT);
	this.deviceManagement = new CacheAwareDeviceManagement(implementation, getMicroservice());
	this.deviceManagementImpl = new DeviceManagementImpl(getDeviceManagement());

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize discoverable lifecycle components.
	init.addStep(getMicroservice().initializeDiscoverableBeans(getModuleContext(), monitor));

	// Initialize device management persistence.
	init.addInitializeStep(this, getDeviceManagement(), true);

	// Execute initialization steps.
	init.execute(monitor);
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

	// Start device management persistence.
	start.addStartStep(this, getDeviceManagement(), true);

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
	List<String> scripts = template.getInitializers().getDeviceManagement();
	for (String script : scripts) {
	    getTenantScriptSynchronizer().add(script);
	}

	// Wait for remote APIs to become available.
	getAssetManagementApiChannel().waitForApiAvailable();
	getEventManagementApiChannel().waitForApiAvailable();

	// Execute remote calls as superuser.
	Authentication previous = SecurityContextHolder.getContext().getAuthentication();
	try {
	    SecurityContextHolder.getContext()
		    .setAuthentication(getMicroservice().getSystemUser().getAuthenticationForTenant(getTenant()));
	    GroovyConfiguration groovy = new GroovyConfiguration(getTenantScriptSynchronizer());
	    groovy.start(new LifecycleProgressMonitor(new LifecycleProgressContext(1, "Initialize device model."),
		    getMicroservice()));
	    for (String script : scripts) {
		GroovyDeviceModelInitializer initializer = new GroovyDeviceModelInitializer(groovy, script);
		initializer.initialize(getDeviceManagement(), getEventManagementApiChannel(), getAssetResolver());
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

	// Stop device management persistence.
	stop.addStopStep(this, getDeviceManagement());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.spi.microservice.IDeviceManagementTenantEngine#
     * getDeviceManagement()
     */
    @Override
    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.spi.microservice.IDeviceManagementTenantEngine#
     * getDeviceManagementImpl()
     */
    @Override
    public DeviceManagementGrpc.DeviceManagementImplBase getDeviceManagementImpl() {
	return deviceManagementImpl;
    }

    public void setDeviceManagementImpl(DeviceManagementGrpc.DeviceManagementImplBase deviceManagementImpl) {
	this.deviceManagementImpl = deviceManagementImpl;
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

    public IAssetManagementApiChannel getAssetManagementApiChannel() {
	return ((IDeviceManagementMicroservice) getMicroservice()).getAssetManagementApiDemux().getApiChannel();
    }

    public IDeviceEventManagementApiChannel getEventManagementApiChannel() {
	return ((IDeviceManagementMicroservice) getMicroservice()).getEventManagementApiChannel();
    }

    public IAssetResolver getAssetResolver() {
	return ((IDeviceManagementMicroservice) getMicroservice()).getAssetResolver();
    }
}