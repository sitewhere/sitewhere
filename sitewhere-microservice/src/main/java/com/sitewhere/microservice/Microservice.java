/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;

import com.sitewhere.Version;
import com.sitewhere.grpc.kafka.model.KafkaModel.GStateUpdate;
import com.sitewhere.grpc.model.converter.KafkaModelConverter;
import com.sitewhere.grpc.model.marshaling.KafkaModelMarshaler;
import com.sitewhere.microservice.state.MicroserviceStateUpdatesKafkaProducer;
import com.sitewhere.rest.model.microservice.state.MicroserviceState;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.server.lifecycle.TracerUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.configuration.IZookeeperManager;
import com.sitewhere.spi.microservice.ignite.IIgniteManager;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming;
import com.sitewhere.spi.microservice.security.ISystemUser;
import com.sitewhere.spi.microservice.security.ITokenManagement;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaProducer;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.system.IVersion;

import io.opentracing.ActiveSpan;
import io.opentracing.Tracer;

/**
 * Common base class for all SiteWhere microservices.
 * 
 * @author Derek
 */
public abstract class Microservice extends LifecycleComponent implements IMicroservice {

    /** Instance configuration folder name */
    private static final String INSTANCE_CONFIGURATION_FOLDER = "/conf";

    /** Relative path to instance bootstrap marker */
    private static final String INSTANCE_BOOTSTRAP_MARKER = "/bootstrapped";

    /** Instance settings */
    @Autowired
    private IInstanceSettings instanceSettings;

    /** Zookeeper manager */
    @Autowired
    private IZookeeperManager zookeeperManager;

    /** Get Apache Ignite manager */
    @Autowired
    private IIgniteManager igniteManager;

    /** JWT token management */
    @Autowired
    private ITokenManagement tokenManagement;

    /** System superuser */
    @Autowired
    private ISystemUser systemUser;

    /** Kafka topic naming */
    @Autowired
    private IKafkaTopicNaming kafkaTopicNaming;

    /** Tracer implementation */
    @Autowired
    private Tracer tracer;

    /** Version information */
    private IVersion version = new Version();

    /** Kafka producer for microservice state updates */
    private IMicroserviceStateUpdatesKafkaProducer stateUpdatesKafkaProducer;

    public Microservice() {
	this.stateUpdatesKafkaProducer = new MicroserviceStateUpdatesKafkaProducer(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Organizes steps for initializing microservice.
	ICompositeLifecycleStep initialize = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize Zookeeper configuration management.
	initialize.addInitializeStep(this, getZookeeperManager(), true);

	// Initialize Apache Ignite manager.
	initialize.addInitializeStep(this, getIgniteManager(), true);

	// Initialize Kafka producer for reporting state.
	initialize.addInitializeStep(this, getStateUpdatesKafkaProducer(), true);

	// Execute initialization steps.
	initialize.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getName());

	// Start Apache Ignite manager.
	start.addStartStep(this, getIgniteManager(), true);

	// Start Kafka producer for reporting state.
	start.addStartStep(this, getStateUpdatesKafkaProducer(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#afterMicroserviceStarted()
     */
    @Override
    public void afterMicroserviceStarted() {
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop Kafka producer for reporting state.
	start.addStopStep(this, getStateUpdatesKafkaProducer());

	// Execute shutdown steps.
	start.execute(monitor);
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
	// Terminate Zk manager.
	getZookeeperManager().lifecycleTerminate(monitor);

	// Terminate Ignite manager.
	getIgniteManager().lifecycleTerminate(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#
     * waitForInstanceInitialization()
     */
    @Override
    public void waitForInstanceInitialization() throws SiteWhereException {
	ActiveSpan span = null;
	try {
	    span = getTracer().buildSpan("Wait for instance to be bootstrapped").startActive();
	    getLogger().info("Verifying that instance has been bootstrapped...");
	    while (true) {
		if (getZookeeperManager().getCurator().checkExists().forPath(getInstanceBootstrappedMarker()) != null) {
		    break;
		}
		getLogger().info("Bootstrap marker not found at '" + getInstanceBootstrappedMarker() + "'. Waiting...");
		Thread.sleep(1000);
	    }
	    getLogger().info("Confirmed that instance was bootstrapped.");
	} catch (Exception e) {
	    TracerUtils.handleErrorInTracerSpan(span, e);
	    throw new SiteWhereException("Error waiting on instance to be bootstrapped.", e);
	} finally {
	    TracerUtils.finishTracerSpan(span);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getHostname()
     */
    @Override
    public String getHostname() throws SiteWhereException {
	try {
	    InetAddress local = InetAddress.getLocalHost();
	    return local.getHostName();
	} catch (UnknownHostException e) {
	    throw new SiteWhereException("Unable to find hostname.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getCurrentState()
     */
    @Override
    public IMicroserviceState getCurrentState() throws SiteWhereException {
	MicroserviceState state = new MicroserviceState();
	state.setMicroserviceIdentifier(getIdentifier());
	state.setMicroserviceHostname(getHostname());
	state.setLifecycleStatus(getLifecycleStatus());
	return state;
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
	    IMicroserviceState state = getCurrentState();
	    GStateUpdate update = KafkaModelConverter.asGrpcGenericStateUpdate(state);
	    byte[] payload = KafkaModelMarshaler.buildStateUpdatePayloadMessage(update);
	    if (getStateUpdatesKafkaProducer().getLifecycleStatus() == LifecycleStatus.Started) {
		getStateUpdatesKafkaProducer().send(state.getMicroserviceIdentifier(), payload);
	    }
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to report microservice state.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroservice#onTenantEngineStateChanged(com.
     * sitewhere.spi.microservice.state.ITenantEngineState)
     */
    @Override
    public void onTenantEngineStateChanged(ITenantEngineState state) {
	try {
	    GStateUpdate update = KafkaModelConverter.asGrpcGenericStateUpdate(state);
	    byte[] payload = KafkaModelMarshaler.buildStateUpdatePayloadMessage(update);
	    if (getStateUpdatesKafkaProducer().getLifecycleStatus() == LifecycleStatus.Started) {
		getStateUpdatesKafkaProducer().send(state.getMicroserviceIdentifier(), payload);
	    }
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to report tenant engine state.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#getInstanceZkPath()
     */
    @Override
    public String getInstanceZkPath() {
	return "/" + getInstanceSettings().getInstanceId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroservice#getInstanceConfigurationPath ()
     */
    @Override
    public String getInstanceConfigurationPath() {
	return getInstanceZkPath() + INSTANCE_CONFIGURATION_FOLDER;
    }

    /*
     * @see com.sitewhere.microservice.spi.IMicroservice#
     * getInstanceBootstrappedMarker()
     */
    @Override
    public String getInstanceBootstrappedMarker() throws SiteWhereException {
	return getInstanceConfigurationPath() + INSTANCE_BOOTSTRAP_MARKER;
    }

    /*
     * @see com.sitewhere.microservice.spi.IMicroservice#getZookeeperManager()
     */
    @Override
    public IZookeeperManager getZookeeperManager() {
	return zookeeperManager;
    }

    public void setZookeeperManager(IZookeeperManager zookeeperManager) {
	this.zookeeperManager = zookeeperManager;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getIgniteManager()
     */
    @Override
    public IIgniteManager getIgniteManager() {
	return igniteManager;
    }

    public void setIgniteManager(IIgniteManager igniteManager) {
	this.igniteManager = igniteManager;
    }

    /*
     * @see com.sitewhere.microservice.spi.IMicroservice#getTokenManagement()
     */
    @Override
    public ITokenManagement getTokenManagement() {
	return tokenManagement;
    }

    public void setTokenManagement(ITokenManagement tokenManagement) {
	this.tokenManagement = tokenManagement;
    }

    /*
     * @see com.sitewhere.microservice.spi.IMicroservice#getSystemUser()
     */
    @Override
    public ISystemUser getSystemUser() {
	return systemUser;
    }

    public void setSystemUser(ISystemUser systemUser) {
	this.systemUser = systemUser;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getKafkaTopicNaming()
     */
    @Override
    public IKafkaTopicNaming getKafkaTopicNaming() {
	return kafkaTopicNaming;
    }

    public void setKafkaTopicNaming(IKafkaTopicNaming kafkaTopicNaming) {
	this.kafkaTopicNaming = kafkaTopicNaming;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroservice#getStateUpdatesKafkaProducer()
     */
    @Override
    public IMicroserviceStateUpdatesKafkaProducer getStateUpdatesKafkaProducer() {
	return stateUpdatesKafkaProducer;
    }

    public void setStateUpdatesKafkaProducer(IMicroserviceStateUpdatesKafkaProducer stateUpdatesKafkaProducer) {
	this.stateUpdatesKafkaProducer = stateUpdatesKafkaProducer;
    }

    /*
     * @see com.sitewhere.microservice.spi.IMicroservice#getInstanceSettings()
     */
    @Override
    public IInstanceSettings getInstanceSettings() {
	return instanceSettings;
    }

    public void setInstanceSettings(IInstanceSettings instanceSettings) {
	this.instanceSettings = instanceSettings;
    }

    /*
     * @see com.sitewhere.spi.tracing.ITracerProvider#getTracer()
     */
    @Override
    public Tracer getTracer() {
	return tracer;
    }

    public void setTracer(Tracer tracer) {
	this.tracer = tracer;
    }

    /*
     * @see com.sitewhere.microservice.spi.IMicroservice#getVersion()
     */
    @Override
    public IVersion getVersion() {
	return version;
    }

    public void setVersion(IVersion version) {
	this.version = version;
    }
}