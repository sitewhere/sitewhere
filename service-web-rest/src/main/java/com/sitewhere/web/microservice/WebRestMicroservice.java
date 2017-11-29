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

import com.sitewhere.grpc.model.client.AssetManagementApiChannel;
import com.sitewhere.grpc.model.client.AssetManagementGrpcChannel;
import com.sitewhere.grpc.model.client.BatchManagementApiChannel;
import com.sitewhere.grpc.model.client.BatchManagementGrpcChannel;
import com.sitewhere.grpc.model.client.DeviceEventManagementApiChannel;
import com.sitewhere.grpc.model.client.DeviceEventManagementGrpcChannel;
import com.sitewhere.grpc.model.client.DeviceManagementGrpcChannel;
import com.sitewhere.grpc.model.client.ScheduleManagementApiChannel;
import com.sitewhere.grpc.model.client.ScheduleManagementGrpcChannel;
import com.sitewhere.grpc.model.client.TenantManagementApiChannel;
import com.sitewhere.grpc.model.client.TenantManagementGrpcChannel;
import com.sitewhere.grpc.model.client.UserManagementApiChannel;
import com.sitewhere.grpc.model.client.UserManagementGrpcChannel;
import com.sitewhere.grpc.model.spi.ApiNotAvailableException;
import com.sitewhere.grpc.model.spi.client.IAssetManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IBatchManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IScheduleManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.ITenantManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IUserManagementApiChannel;
import com.sitewhere.microservice.GlobalMicroservice;
import com.sitewhere.microservice.MicroserviceEnvironment;
import com.sitewhere.microservice.ignite.client.CachedDeviceManagementApiChannel;
import com.sitewhere.rest.model.asset.AssetResolver;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetResolver;
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

    /** Microservice identifier */
    private static final String IDENTIFIER = "web-rest";

    /** Web/REST configuration file name */
    private static final String WEB_REST_CONFIGURATION = IDENTIFIER + ".xml";

    /** List of configuration paths required by microservice */
    private static final String[] CONFIGURATION_PATHS = { WEB_REST_CONFIGURATION };

    /** User management GRPC channel */
    private UserManagementGrpcChannel userManagementGrpcChannel;

    /** User management API channel */
    private IUserManagementApiChannel userManagementApiChannel;

    /** Tenant management GRPC channel */
    private TenantManagementGrpcChannel tenantManagementGrpcChannel;

    /** Tenant management API channel */
    private ITenantManagementApiChannel tenantManagementApiChannel;

    /** Device management GRPC channel */
    private DeviceManagementGrpcChannel deviceManagementGrpcChannel;

    /** Device management API channel */
    private IDeviceManagementApiChannel deviceManagementApiChannel;

    /** Device event management GRPC channel */
    private DeviceEventManagementGrpcChannel deviceEventManagementGrpcChannel;

    /** Device event management API channel */
    private IDeviceEventManagementApiChannel deviceEventManagementApiChannel;

    /** Asset management GRPC channel */
    private AssetManagementGrpcChannel assetManagementGrpcChannel;

    /** Asset management API channel */
    private IAssetManagementApiChannel assetManagementApiChannel;

    /** Batch management GRPC channel */
    private BatchManagementGrpcChannel batchManagementGrpcChannel;

    /** Batch management API channel */
    private IBatchManagementApiChannel batchManagementApiChannel;

    /** Schedule management GRPC channel */
    private ScheduleManagementGrpcChannel scheduleManagementGrpcChannel;

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
	return IDENTIFIER;
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

	// Initialize user management GRPC channel.
	init.addInitializeStep(this, getUserManagementGrpcChannel(), true);

	// Initialize tenant management GRPC channel.
	init.addInitializeStep(this, getTenantManagementGrpcChannel(), true);

	// Initialize device management GRPC channel.
	init.addInitializeStep(this, getDeviceManagementGrpcChannel(), true);

	// Initialize device event management GRPC channel.
	init.addInitializeStep(this, getDeviceEventManagementGrpcChannel(), true);

	// Initialize asset management GRPC channel.
	init.addInitializeStep(this, getAssetManagementGrpcChannel(), true);

	// Initialize batch management GRPC channel.
	init.addInitializeStep(this, getBatchManagementGrpcChannel(), true);

	// Initialize schedule management GRPC channel.
	init.addInitializeStep(this, getScheduleManagementGrpcChannel(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /**
     * Create components that interact via GRPC.
     */
    protected void createGrpcComponents() {
	// User management.
	this.userManagementGrpcChannel = new UserManagementGrpcChannel(this,
		MicroserviceEnvironment.HOST_USER_MANAGEMENT, getInstanceSettings().getGrpcPort());
	this.userManagementApiChannel = new UserManagementApiChannel(getUserManagementGrpcChannel());

	// Tenant management.
	this.tenantManagementGrpcChannel = new TenantManagementGrpcChannel(this,
		MicroserviceEnvironment.HOST_TENANT_MANAGEMENT, getInstanceSettings().getGrpcPort());
	this.tenantManagementApiChannel = new TenantManagementApiChannel(getTenantManagementGrpcChannel());

	// Device management.
	this.deviceManagementGrpcChannel = new DeviceManagementGrpcChannel(this,
		MicroserviceEnvironment.HOST_DEVICE_MANAGEMENT, getInstanceSettings().getGrpcPort());
	this.deviceManagementApiChannel = new CachedDeviceManagementApiChannel(this, getDeviceManagementGrpcChannel());

	// Device event management.
	this.deviceEventManagementGrpcChannel = new DeviceEventManagementGrpcChannel(this,
		MicroserviceEnvironment.HOST_EVENT_MANAGEMENT, getInstanceSettings().getGrpcPort());
	this.deviceEventManagementApiChannel = new DeviceEventManagementApiChannel(
		getDeviceEventManagementGrpcChannel());

	// Asset management.
	this.assetManagementGrpcChannel = new AssetManagementGrpcChannel(this,
		MicroserviceEnvironment.HOST_ASSET_MANAGEMENT, getInstanceSettings().getGrpcPort());
	this.assetManagementApiChannel = new AssetManagementApiChannel(getAssetManagementGrpcChannel());
	this.assetResolver = new AssetResolver(getAssetManagementApiChannel(), getAssetManagementApiChannel());

	// Batch management.
	this.batchManagementGrpcChannel = new BatchManagementGrpcChannel(this,
		MicroserviceEnvironment.HOST_BATCH_OPERATIONS, getInstanceSettings().getGrpcPort());
	this.batchManagementApiChannel = new BatchManagementApiChannel(getBatchManagementGrpcChannel());

	// Schedule management.
	this.scheduleManagementGrpcChannel = new ScheduleManagementGrpcChannel(this,
		MicroserviceEnvironment.HOST_SCHEDULE_MANAGEMENT, getInstanceSettings().getGrpcPort());
	this.scheduleManagementApiChannel = new ScheduleManagementApiChannel(getScheduleManagementGrpcChannel());
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

	// Start user mangement GRPC channel.
	start.addStartStep(this, getUserManagementGrpcChannel(), true);

	// Start tenant mangement GRPC channel.
	start.addStartStep(this, getTenantManagementGrpcChannel(), true);

	// Start device mangement GRPC channel.
	start.addStartStep(this, getDeviceManagementGrpcChannel(), true);

	// Start device event mangement GRPC channel.
	start.addStartStep(this, getDeviceEventManagementGrpcChannel(), true);

	// Start asset mangement GRPC channel.
	start.addStartStep(this, getAssetManagementGrpcChannel(), true);

	// Start batch mangement GRPC channel.
	start.addStartStep(this, getBatchManagementGrpcChannel(), true);

	// Start schedule mangement GRPC channel.
	start.addStartStep(this, getScheduleManagementGrpcChannel(), true);

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

	// Stop user mangement GRPC channel.
	stop.addStopStep(this, getUserManagementGrpcChannel());

	// Stop tenant mangement GRPC channel.
	stop.addStopStep(this, getTenantManagementGrpcChannel());

	// Stop device mangement GRPC channel.
	stop.addStopStep(this, getDeviceManagementGrpcChannel());

	// Stop device event mangement GRPC channel.
	stop.addStopStep(this, getDeviceEventManagementGrpcChannel());

	// Stop asset mangement GRPC channel.
	stop.addStopStep(this, getAssetManagementGrpcChannel());

	// Stop batch mangement GRPC channel.
	stop.addStopStep(this, getBatchManagementGrpcChannel());

	// Stop schedule mangement GRPC channel.
	stop.addStopStep(this, getScheduleManagementGrpcChannel());

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
     * getAssetManagementApiChannel()
     */
    @Override
    public IAssetManagementApiChannel getAssetManagementApiChannel() {
	return assetManagementApiChannel;
    }

    protected void setAssetManagementApiChannel(IAssetManagementApiChannel assetManagementApiChannel) {
	this.assetManagementApiChannel = assetManagementApiChannel;
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

    public UserManagementGrpcChannel getUserManagementGrpcChannel() {
	return userManagementGrpcChannel;
    }

    public void setUserManagementGrpcChannel(UserManagementGrpcChannel userManagementGrpcChannel) {
	this.userManagementGrpcChannel = userManagementGrpcChannel;
    }

    public TenantManagementGrpcChannel getTenantManagementGrpcChannel() {
	return tenantManagementGrpcChannel;
    }

    public void setTenantManagementGrpcChannel(TenantManagementGrpcChannel tenantManagementGrpcChannel) {
	this.tenantManagementGrpcChannel = tenantManagementGrpcChannel;
    }

    public DeviceManagementGrpcChannel getDeviceManagementGrpcChannel() {
	return deviceManagementGrpcChannel;
    }

    public void setDeviceManagementGrpcChannel(DeviceManagementGrpcChannel deviceManagementGrpcChannel) {
	this.deviceManagementGrpcChannel = deviceManagementGrpcChannel;
    }

    public DeviceEventManagementGrpcChannel getDeviceEventManagementGrpcChannel() {
	return deviceEventManagementGrpcChannel;
    }

    public void setDeviceEventManagementGrpcChannel(DeviceEventManagementGrpcChannel deviceEventManagementGrpcChannel) {
	this.deviceEventManagementGrpcChannel = deviceEventManagementGrpcChannel;
    }

    public AssetManagementGrpcChannel getAssetManagementGrpcChannel() {
	return assetManagementGrpcChannel;
    }

    public void setAssetManagementGrpcChannel(AssetManagementGrpcChannel assetManagementGrpcChannel) {
	this.assetManagementGrpcChannel = assetManagementGrpcChannel;
    }

    public BatchManagementGrpcChannel getBatchManagementGrpcChannel() {
	return batchManagementGrpcChannel;
    }

    public void setBatchManagementGrpcChannel(BatchManagementGrpcChannel batchManagementGrpcChannel) {
	this.batchManagementGrpcChannel = batchManagementGrpcChannel;
    }

    protected ScheduleManagementGrpcChannel getScheduleManagementGrpcChannel() {
	return scheduleManagementGrpcChannel;
    }

    protected void setScheduleManagementGrpcChannel(ScheduleManagementGrpcChannel scheduleManagementGrpcChannel) {
	this.scheduleManagementGrpcChannel = scheduleManagementGrpcChannel;
    }

    public IAssetResolver getAssetResolver() {
	return assetResolver;
    }

    public void setAssetResolver(IAssetResolver assetResolver) {
	this.assetResolver = assetResolver;
    }
}