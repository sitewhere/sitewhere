/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.microservice;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.sitewhere.grpc.model.client.asset.AssetManagementApiDemux;
import com.sitewhere.grpc.model.client.batch.BatchManagementApiChannel;
import com.sitewhere.grpc.model.client.event.DeviceEventManagementApiChannel;
import com.sitewhere.grpc.model.client.schedule.ScheduleManagementApiChannel;
import com.sitewhere.grpc.model.client.tenant.TenantManagementApiChannel;
import com.sitewhere.grpc.model.client.user.UserManagementApiChannel;
import com.sitewhere.grpc.model.spi.ApiNotAvailableException;
import com.sitewhere.grpc.model.spi.client.IAssetManagementApiDemux;
import com.sitewhere.grpc.model.spi.client.IBatchManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IScheduleManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.ITenantManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IUserManagementApiChannel;
import com.sitewhere.microservice.GlobalMicroservice;
import com.sitewhere.microservice.MicroserviceEnvironment;
import com.sitewhere.microservice.asset.AssetResolver;
import com.sitewhere.microservice.hazelcast.client.CachedDeviceManagementApiChannel;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.microservice.IMicroserviceIdentifiers;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.web.spi.microservice.IWebRestMicroservice;

/**
 * Microservice that provides web/REST functionality.
 * 
 * @author Derek
 */
public class WebRestMicroservice extends GlobalMicroservice implements IWebRestMicroservice {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice name */
    private static final String NAME = "Web/REST";

    /** Web/REST configuration file name */
    private static final String WEB_REST_CONFIGURATION = IMicroserviceIdentifiers.WEB_REST + ".xml";

    /** List of configuration paths required by microservice */
    private static final String[] CONFIGURATION_PATHS = { WEB_REST_CONFIGURATION };

    /** User management API channel */
    private IUserManagementApiChannel userManagementApiChannel;

    /** Tenant management API channel */
    private ITenantManagementApiChannel tenantManagementApiChannel;

    /** Device management API channel */
    private IDeviceManagementApiChannel deviceManagementApiChannel;

    /** Device event management API channel */
    private IDeviceEventManagementApiChannel deviceEventManagementApiChannel;

    /** Asset management API connectivity */
    private IAssetManagementApiDemux assetManagementApiDemux;

    /** Batch management API channel */
    private IBatchManagementApiChannel batchManagementApiChannel;

    /** Schedule management API channel */
    private IScheduleManagementApiChannel scheduleManagementApiChannel;

    /** Asset resolver */
    private IAssetResolver assetResolver;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#getName()
     */
    @Override
    public String getName() {
	return NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#getIdentifier()
     */
    @Override
    public String getIdentifier() {
	return IMicroserviceIdentifiers.WEB_REST;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IGlobalMicroservice#getConfigurationPaths( )
     */
    @Override
    public String[] getConfigurationPaths() throws SiteWhereException {
	return CONFIGURATION_PATHS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IGlobalMicroservice#
     * initializeFromSpringContexts(org.springframework.context. ApplicationContext,
     * java.util.Map)
     */
    @Override
    public void initializeFromSpringContexts(ApplicationContext global, Map<String, ApplicationContext> contexts)
	    throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.Microservice#afterMicroserviceStarted()
     */
    @Override
    public void afterMicroserviceStarted() {
	try {
	    waitForApisAvailable();
	    getLogger().info("All required APIs detected as available.");
	} catch (ApiNotAvailableException e) {
	    getLogger().error("Required APIs not available for web/REST.", e);
	}
    }

    /**
     * Wait for required APIs to become available.
     * 
     * @throws ApiNotAvailableException
     */
    protected void waitForApisAvailable() throws ApiNotAvailableException {
	getUserManagementApiChannel().waitForApiAvailable();
	getLogger().info("User management API detected as available.");
	getTenantManagementApiChannel().waitForApiAvailable();
	getLogger().info("Tenant management API detected as available.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IGlobalMicroservice#microserviceInitialize
     * (com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create GRPC components.
	createGrpcComponents();

	// Composite step for initializing microservice.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize discoverable lifecycle components.
	init.addStep(initializeDiscoverableBeans(getWebRestApplicationContext(), monitor));

	// Initialize user management API channel.
	init.addInitializeStep(this, getUserManagementApiChannel(), true);

	// Initialize tenant management API channel.
	init.addInitializeStep(this, getTenantManagementApiChannel(), true);

	// Initialize device management API channel.
	init.addInitializeStep(this, getDeviceManagementApiChannel(), true);

	// Initialize device event management API channel.
	init.addInitializeStep(this, getDeviceEventManagementApiChannel(), true);

	// Initialize asset management API demux.
	init.addInitializeStep(this, getAssetManagementApiDemux(), true);

	// Initialize batch management API channel.
	init.addInitializeStep(this, getBatchManagementApiChannel(), true);

	// Initialize schedule management API channel.
	init.addInitializeStep(this, getScheduleManagementApiChannel(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /**
     * Create components that interact via GRPC.
     * 
     * @throws SiteWhereException
     */
    protected void createGrpcComponents() throws SiteWhereException {
	// User management.
	this.userManagementApiChannel = new UserManagementApiChannel(this,
		MicroserviceEnvironment.HOST_USER_MANAGEMENT);

	// Tenant management.
	this.tenantManagementApiChannel = new TenantManagementApiChannel(this,
		MicroserviceEnvironment.HOST_TENANT_MANAGEMENT);

	// Device management.
	this.deviceManagementApiChannel = new CachedDeviceManagementApiChannel(this,
		MicroserviceEnvironment.HOST_DEVICE_MANAGEMENT);

	// Device event management.
	this.deviceEventManagementApiChannel = new DeviceEventManagementApiChannel(this,
		MicroserviceEnvironment.HOST_EVENT_MANAGEMENT);

	// Asset management.
	this.assetManagementApiDemux = new AssetManagementApiDemux(this);
	this.assetResolver = new AssetResolver(getAssetManagementApiDemux());

	// Batch management.
	this.batchManagementApiChannel = new BatchManagementApiChannel(this,
		MicroserviceEnvironment.HOST_BATCH_OPERATIONS);

	// Schedule management.
	this.scheduleManagementApiChannel = new ScheduleManagementApiChannel(this,
		MicroserviceEnvironment.HOST_SCHEDULE_MANAGEMENT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IGlobalMicroservice#microserviceStart(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for starting microservice.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getName());

	// Start discoverable lifecycle components.
	start.addStep(startDiscoverableBeans(getWebRestApplicationContext(), monitor));

	// Start user mangement API channel.
	start.addStartStep(this, getUserManagementApiChannel(), true);

	// Start tenant mangement API channel.
	start.addStartStep(this, getTenantManagementApiChannel(), true);

	// Start device mangement API channel.
	start.addStartStep(this, getDeviceManagementApiChannel(), true);

	// Start device event mangement API channel.
	start.addStartStep(this, getDeviceEventManagementApiChannel(), true);

	// Start asset mangement API demux.
	start.addStartStep(this, getAssetManagementApiDemux(), true);

	// Start batch mangement API channel.
	start.addStartStep(this, getBatchManagementApiChannel(), true);

	// Start schedule mangement API channel.
	start.addStartStep(this, getScheduleManagementApiChannel(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IGlobalMicroservice#microserviceStop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for stopping microservice.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop user mangement API channel.
	stop.addStopStep(this, getUserManagementApiChannel());

	// Stop tenant mangement API channel.
	stop.addStopStep(this, getTenantManagementApiChannel());

	// Stop device mangement API channel.
	stop.addStopStep(this, getDeviceManagementApiChannel());

	// Stop device event mangement API channel.
	stop.addStopStep(this, getDeviceEventManagementApiChannel());

	// Stop asset mangement API demux.
	stop.addStopStep(this, getAssetManagementApiDemux());

	// Stop batch mangement API channel.
	stop.addStopStep(this, getBatchManagementApiChannel());

	// Stop schedule mangement API channel.
	stop.addStopStep(this, getScheduleManagementApiChannel());

	// Stop discoverable lifecycle components.
	stop.addStep(stopDiscoverableBeans(getWebRestApplicationContext(), monitor));

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getUserManagementApiChannel()
     */
    @Override
    public IUserManagementApiChannel getUserManagementApiChannel() {
	return userManagementApiChannel;
    }

    protected void setUserManagementApiChannel(IUserManagementApiChannel userManagementApiChannel) {
	this.userManagementApiChannel = userManagementApiChannel;
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getTenantManagementApiChannel()
     */
    @Override
    public ITenantManagementApiChannel getTenantManagementApiChannel() {
	return tenantManagementApiChannel;
    }

    protected void setTenantManagementApiChannel(ITenantManagementApiChannel tenantManagementApiChannel) {
	this.tenantManagementApiChannel = tenantManagementApiChannel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getDeviceManagementApiChannel()
     */
    @Override
    public IDeviceManagementApiChannel getDeviceManagementApiChannel() {
	return deviceManagementApiChannel;
    }

    protected void setDeviceManagementApiChannel(IDeviceManagementApiChannel deviceManagementApiChannel) {
	this.deviceManagementApiChannel = deviceManagementApiChannel;
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getDeviceEventManagementApiChannel()
     */
    @Override
    public IDeviceEventManagementApiChannel getDeviceEventManagementApiChannel() {
	return deviceEventManagementApiChannel;
    }

    protected void setDeviceEventManagementApiChannel(
	    IDeviceEventManagementApiChannel deviceEventManagementApiChannel) {
	this.deviceEventManagementApiChannel = deviceEventManagementApiChannel;
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getAssetManagementApiDemux()
     */
    @Override
    public IAssetManagementApiDemux getAssetManagementApiDemux() {
	return assetManagementApiDemux;
    }

    public void setAssetManagementApiDemux(IAssetManagementApiDemux assetManagementApiDemux) {
	this.assetManagementApiDemux = assetManagementApiDemux;
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getBatchManagementApiChannel()
     */
    @Override
    public IBatchManagementApiChannel getBatchManagementApiChannel() {
	return batchManagementApiChannel;
    }

    public void setBatchManagementApiChannel(IBatchManagementApiChannel batchManagementApiChannel) {
	this.batchManagementApiChannel = batchManagementApiChannel;
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getScheduleManagementApiChannel()
     */
    @Override
    public IScheduleManagementApiChannel getScheduleManagementApiChannel() {
	return scheduleManagementApiChannel;
    }

    protected void setScheduleManagementApiChannel(IScheduleManagementApiChannel scheduleManagementApiChannel) {
	this.scheduleManagementApiChannel = scheduleManagementApiChannel;
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

    protected ApplicationContext getWebRestApplicationContext() {
	return getGlobalContexts().get(WEB_REST_CONFIGURATION);
    }

    public IAssetResolver getAssetResolver() {
	return assetResolver;
    }

    public void setAssetResolver(IAssetResolver assetResolver) {
	this.assetResolver = assetResolver;
    }
}