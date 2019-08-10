/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.microservice;

import com.sitewhere.grpc.client.asset.AssetManagementApiChannel;
import com.sitewhere.grpc.client.asset.CachedAssetManagementApiChannel;
import com.sitewhere.grpc.client.batch.BatchManagementApiChannel;
import com.sitewhere.grpc.client.device.CachedDeviceManagementApiChannel;
import com.sitewhere.grpc.client.device.DeviceManagementApiChannel;
import com.sitewhere.grpc.client.devicestate.DeviceStateApiChannel;
import com.sitewhere.grpc.client.event.DeviceEventManagementApiChannel;
import com.sitewhere.grpc.client.instance.InstanceManagementApiChannel;
import com.sitewhere.grpc.client.label.LabelGenerationApiChannel;
import com.sitewhere.grpc.client.schedule.ScheduleManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IAssetManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IBatchManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceStateApiChannel;
import com.sitewhere.grpc.client.spi.client.IInstanceManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.ILabelGenerationApiChannel;
import com.sitewhere.grpc.client.spi.client.IScheduleManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IUserManagementApiChannel;
import com.sitewhere.grpc.client.user.CachedUserManagementApiChannel;
import com.sitewhere.grpc.client.user.UserManagementApiChannel;
import com.sitewhere.microservice.GlobalMicroservice;
import com.sitewhere.microservice.state.TopologyStateAggregator;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.state.ITopologyStateAggregator;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.web.configuration.WebRestModelProvider;
import com.sitewhere.web.spi.microservice.IWebRestMicroservice;

/**
 * Microservice that provides web/REST functionality.
 * 
 * @author Derek
 */
public class WebRestMicroservice extends GlobalMicroservice<MicroserviceIdentifier>
	implements IWebRestMicroservice<MicroserviceIdentifier> {

    /** Microservice name */
    private static final String NAME = "Web/REST";

    /** Web/REST configuration file name */
    private static final String CONFIGURATION_PATH = MicroserviceIdentifier.WebRest.getPath() + ".xml";

    /** Instance management API channel */
    private IInstanceManagementApiChannel<?> instanceManagementApiChannel;

    /** User management API channel */
    private IUserManagementApiChannel<?> userManagementApiChannel;

    /** Cached user management implementation */
    private IUserManagement cachedUserManagement;

    /** Device management API channel */
    private IDeviceManagementApiChannel<?> deviceManagementApiChannel;

    /** Cached device management implementation */
    private IDeviceManagement cachedDeviceManagement;

    /** Device event management API channel */
    private IDeviceEventManagementApiChannel<?> deviceEventManagementApiChannel;

    /** Asset management API channel */
    private IAssetManagementApiChannel<?> assetManagementApiChannel;

    /** Cached asset management implementation */
    private IAssetManagement cachedAssetManagement;

    /** Batch management API channel */
    private IBatchManagementApiChannel<?> batchManagementApiChannel;

    /** Schedule management API channel */
    private IScheduleManagementApiChannel<?> scheduleManagementApiChannel;

    /** Label generation API channel */
    private ILabelGenerationApiChannel<?> labelGenerationApiChannel;

    /** Device state API channel */
    private IDeviceStateApiChannel<?> deviceStateApiChannel;

    /** Aggregates microservice state info into a topology */
    private ITopologyStateAggregator topologyStateAggregator = new TopologyStateAggregator();

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
     * @see com.sitewhere.spi.microservice.IMicroservice#getIdentifier()
     */
    @Override
    public MicroserviceIdentifier getIdentifier() {
	return MicroserviceIdentifier.WebRest;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#isGlobal()
     */
    @Override
    public boolean isGlobal() {
	return true;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#buildConfigurationModel()
     */
    @Override
    public IConfigurationModel buildConfigurationModel() {
	return new WebRestModelProvider().buildModel();
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getConfigurationPath()
     */
    @Override
    public String getConfigurationPath() throws SiteWhereException {
	return CONFIGURATION_PATH;
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

	// Initialize topology state aggregator.
	init.addInitializeStep(this, getTopologyStateAggregator(), true);

	// Initialize instance management API channel.
	init.addInitializeStep(this, getInstanceManagementApiChannel(), true);

	// Initialize user management API channel + cache.
	init.addInitializeStep(this, getCachedUserManagement(), true);

	// Initialize device management API channel + cache.
	init.addInitializeStep(this, getCachedDeviceManagement(), true);

	// Initialize device event management API channel.
	init.addInitializeStep(this, getDeviceEventManagementApiChannel(), true);

	// Initialize asset management API channel + cache.
	init.addInitializeStep(this, getCachedAssetManagement(), true);

	// Initialize batch management API channel.
	init.addInitializeStep(this, getBatchManagementApiChannel(), true);

	// Initialize schedule management API channel.
	init.addInitializeStep(this, getScheduleManagementApiChannel(), true);

	// Initialize label generation API channel.
	init.addInitializeStep(this, getLabelGenerationApiChannel(), true);

	// Initialize device state API channel.
	init.addInitializeStep(this, getDeviceStateApiChannel(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /**
     * Create components that interact via GRPC.
     * 
     * @throws SiteWhereException
     */
    protected void createGrpcComponents() throws SiteWhereException {
	// Instance management.
	this.instanceManagementApiChannel = new InstanceManagementApiChannel(getInstanceSettings());

	// User management.
	this.userManagementApiChannel = new UserManagementApiChannel(getInstanceSettings());
	this.cachedUserManagement = new CachedUserManagementApiChannel(userManagementApiChannel,
		new CachedUserManagementApiChannel.CacheSettings());

	// Device management.
	this.deviceManagementApiChannel = new DeviceManagementApiChannel(getInstanceSettings());
	this.cachedDeviceManagement = new CachedDeviceManagementApiChannel(deviceManagementApiChannel,
		new CachedDeviceManagementApiChannel.CacheSettings());

	// Device event management.
	this.deviceEventManagementApiChannel = new DeviceEventManagementApiChannel(getInstanceSettings());

	// Asset management.
	this.assetManagementApiChannel = new AssetManagementApiChannel(getInstanceSettings());
	this.cachedAssetManagement = new CachedAssetManagementApiChannel(assetManagementApiChannel,
		new CachedAssetManagementApiChannel.CacheSettings());

	// Batch management.
	this.batchManagementApiChannel = new BatchManagementApiChannel(getInstanceSettings());

	// Schedule management.
	this.scheduleManagementApiChannel = new ScheduleManagementApiChannel(getInstanceSettings());

	// Label generation.
	this.labelGenerationApiChannel = new LabelGenerationApiChannel(getInstanceSettings());

	// Device state.
	this.deviceStateApiChannel = new DeviceStateApiChannel(getInstanceSettings());
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

	// Start topology state aggregator.
	start.addStartStep(this, getTopologyStateAggregator(), true);

	// Start instance mangement API channel.
	start.addStartStep(this, getInstanceManagementApiChannel(), true);

	// Start user mangement API channel + cache.
	start.addStartStep(this, getCachedUserManagement(), true);

	// Start device mangement API channel + cache.
	start.addStartStep(this, getCachedDeviceManagement(), true);

	// Start device event mangement API channel.
	start.addStartStep(this, getDeviceEventManagementApiChannel(), true);

	// Start asset mangement API channel + cache.
	start.addStartStep(this, getCachedAssetManagement(), true);

	// Start batch mangement API channel.
	start.addStartStep(this, getBatchManagementApiChannel(), true);

	// Start schedule mangement API channel.
	start.addStartStep(this, getScheduleManagementApiChannel(), true);

	// Start label generation API channel.
	start.addStartStep(this, getLabelGenerationApiChannel(), true);

	// Start device state API channel.
	start.addStartStep(this, getDeviceStateApiChannel(), true);

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

	// Stop instance mangement API channel.
	stop.addStopStep(this, getInstanceManagementApiChannel());

	// Stop user mangement API channel + cache.
	stop.addStopStep(this, getCachedUserManagement());

	// Stop device mangement API channel + cache.
	stop.addStopStep(this, getCachedDeviceManagement());

	// Stop device event mangement API channel.
	stop.addStopStep(this, getDeviceEventManagementApiChannel());

	// Stop asset mangement API channel + cache.
	stop.addStopStep(this, getCachedAssetManagement());

	// Stop batch mangement API channel.
	stop.addStopStep(this, getBatchManagementApiChannel());

	// Stop schedule mangement API channel.
	stop.addStopStep(this, getScheduleManagementApiChannel());

	// Stop label generation API channel.
	stop.addStopStep(this, getLabelGenerationApiChannel());

	// Stop device state API channel.
	stop.addStopStep(this, getDeviceStateApiChannel());

	// Stop topology state aggregator.
	stop.addStopStep(this, getTopologyStateAggregator());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getInstanceManagementApiChannel()
     */
    @Override
    public IInstanceManagementApiChannel<?> getInstanceManagementApiChannel() {
	return instanceManagementApiChannel;
    }

    public void setInstanceManagementApiChannel(IInstanceManagementApiChannel<?> instanceManagementApiChannel) {
	this.instanceManagementApiChannel = instanceManagementApiChannel;
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getUserManagementApiChannel()
     */
    @Override
    public IUserManagementApiChannel<?> getUserManagementApiChannel() {
	return userManagementApiChannel;
    }

    public void setUserManagementApiChannel(IUserManagementApiChannel<?> userManagementApiChannel) {
	this.userManagementApiChannel = userManagementApiChannel;
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getCachedUserManagement()
     */
    @Override
    public IUserManagement getCachedUserManagement() {
	return cachedUserManagement;
    }

    public void setCachedUserManagement(IUserManagement cachedUserManagement) {
	this.cachedUserManagement = cachedUserManagement;
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getDeviceManagementApiChannel()
     */
    @Override
    public IDeviceManagementApiChannel<?> getDeviceManagementApiChannel() {
	return deviceManagementApiChannel;
    }

    public void setDeviceManagementApiChannel(IDeviceManagementApiChannel<?> deviceManagementApiChannel) {
	this.deviceManagementApiChannel = deviceManagementApiChannel;
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getCachedDeviceManagement()
     */
    @Override
    public IDeviceManagement getCachedDeviceManagement() {
	return cachedDeviceManagement;
    }

    public void setCachedDeviceManagement(IDeviceManagement cachedDeviceManagement) {
	this.cachedDeviceManagement = cachedDeviceManagement;
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getDeviceEventManagementApiChannel()
     */
    @Override
    public IDeviceEventManagementApiChannel<?> getDeviceEventManagementApiChannel() {
	return deviceEventManagementApiChannel;
    }

    public void setDeviceEventManagementApiChannel(
	    IDeviceEventManagementApiChannel<?> deviceEventManagementApiChannel) {
	this.deviceEventManagementApiChannel = deviceEventManagementApiChannel;
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getAssetManagementApiChannel()
     */
    @Override
    public IAssetManagementApiChannel<?> getAssetManagementApiChannel() {
	return assetManagementApiChannel;
    }

    public void setAssetManagementApiChannel(IAssetManagementApiChannel<?> assetManagementApiChannel) {
	this.assetManagementApiChannel = assetManagementApiChannel;
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getCachedAssetManagement()
     */
    @Override
    public IAssetManagement getCachedAssetManagement() {
	return cachedAssetManagement;
    }

    public void setCachedAssetManagement(IAssetManagement cachedAssetManagement) {
	this.cachedAssetManagement = cachedAssetManagement;
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getBatchManagementApiChannel()
     */
    @Override
    public IBatchManagementApiChannel<?> getBatchManagementApiChannel() {
	return batchManagementApiChannel;
    }

    public void setBatchManagementApiChannel(IBatchManagementApiChannel<?> batchManagementApiChannel) {
	this.batchManagementApiChannel = batchManagementApiChannel;
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getScheduleManagementApiChannel()
     */
    @Override
    public IScheduleManagementApiChannel<?> getScheduleManagementApiChannel() {
	return scheduleManagementApiChannel;
    }

    public void setScheduleManagementApiChannel(IScheduleManagementApiChannel<?> scheduleManagementApiChannel) {
	this.scheduleManagementApiChannel = scheduleManagementApiChannel;
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getLabelGenerationApiChannel()
     */
    @Override
    public ILabelGenerationApiChannel<?> getLabelGenerationApiChannel() {
	return labelGenerationApiChannel;
    }

    public void setLabelGenerationApiChannel(ILabelGenerationApiChannel<?> labelGenerationApiChannel) {
	this.labelGenerationApiChannel = labelGenerationApiChannel;
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getDeviceStateApiChannel()
     */
    @Override
    public IDeviceStateApiChannel<?> getDeviceStateApiChannel() {
	return deviceStateApiChannel;
    }

    public void setDeviceStateApiChannel(IDeviceStateApiChannel<?> deviceStateApiChannel) {
	this.deviceStateApiChannel = deviceStateApiChannel;
    }

    /*
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getTopologyStateAggregator()
     */
    @Override
    public ITopologyStateAggregator getTopologyStateAggregator() {
	return topologyStateAggregator;
    }

    public void setTopologyStateAggregator(ITopologyStateAggregator topologyStateAggregator) {
	this.topologyStateAggregator = topologyStateAggregator;
    }
}