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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;

import com.sitewhere.Version;
import com.sitewhere.microservice.management.MicroserviceManagementGrpcServer;
import com.sitewhere.microservice.state.InstanceTopologySnapshotsManager;
import com.sitewhere.microservice.state.MicroserviceStateUpdatesKafkaProducer;
import com.sitewhere.rest.model.configuration.ConfigurationModel;
import com.sitewhere.rest.model.microservice.state.MicroserviceDetails;
import com.sitewhere.rest.model.microservice.state.MicroserviceState;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.server.lifecycle.TracerUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.configuration.IZookeeperManager;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.configuration.model.IElementNode;
import com.sitewhere.spi.microservice.configuration.model.IElementRole;
import com.sitewhere.spi.microservice.grpc.IMicroserviceManagementGrpcServer;
import com.sitewhere.spi.microservice.hazelcast.IHazelcastManager;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming;
import com.sitewhere.spi.microservice.security.ISystemUser;
import com.sitewhere.spi.microservice.security.ITokenManagement;
import com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesManager;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
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

    /** Get Hazelcast manager */
    @Autowired
    private IHazelcastManager hazelcastManager;

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

    /** Configuration model */
    private IConfigurationModel configurationModel;

    /** Microservice management GRPC server */
    private IMicroserviceManagementGrpcServer microserviceManagementGrpcServer;

    /** Kafka producer for microservice state updates */
    private IMicroserviceStateUpdatesKafkaProducer stateUpdatesKafkaProducer;

    /** Consumes Kafka instance topology updates and broadcasts them */
    private IInstanceTopologyUpdatesManager instanceTopologyUpdatesManager;

    /** Lifecycle operations thread pool */
    private ExecutorService microserviceOperationsService;

    public Microservice() {
	this.microserviceOperationsService = Executors
		.newSingleThreadExecutor(new MicroserviceOperationsThreadFactory());
	this.stateUpdatesKafkaProducer = new MicroserviceStateUpdatesKafkaProducer(this);
	this.instanceTopologyUpdatesManager = new InstanceTopologySnapshotsManager(this);
	this.configurationModel = buildConfigurationModel();
	((ConfigurationModel) configurationModel).setMicroserviceDetails(getMicroserviceDetails());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Initialize GRPC components.
	initializeGrpcComponents();

	// Organizes steps for initializing microservice.
	ICompositeLifecycleStep initialize = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize Zookeeper configuration management.
	initialize.addInitializeStep(this, getZookeeperManager(), true);

	// Initialize Hazelcast manager.
	initialize.addInitializeStep(this, getHazelcastManager(), true);

	// Start Hazelcast manager.
	initialize.addStartStep(this, getHazelcastManager(), true);

	// Initialize microservice management GRPC server.
	initialize.addInitializeStep(this, getMicroserviceManagementGrpcServer(), true);

	// Start microservice management GRPC server.
	initialize.addStartStep(this, getMicroserviceManagementGrpcServer(), true);

	// Initialize Kafka consumer for instance topology updates.
	initialize.addInitializeStep(this, getInstanceTopologyUpdatesManager(), true);

	// Start Kafka consumer for instance topology updates.
	initialize.addStartStep(this, getInstanceTopologyUpdatesManager(), true);

	// Initialize Kafka producer for reporting state.
	initialize.addInitializeStep(this, getStateUpdatesKafkaProducer(), true);

	// Start Kafka producer for reporting state.
	initialize.addStartStep(this, getStateUpdatesKafkaProducer(), true);

	// Execute initialization steps.
	initialize.execute(monitor);
    }

    /**
     * Initialize GRPC components.
     */
    protected void initializeGrpcComponents() {
	this.microserviceManagementGrpcServer = new MicroserviceManagementGrpcServer(this);
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
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#terminate(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void terminate(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep terminate = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop microservice management GRPC server.
	terminate.addStopStep(this, getMicroserviceManagementGrpcServer());

	// Stop Kafka consumer for instance topology updates.
	terminate.addStopStep(this, getInstanceTopologyUpdatesManager());

	// Stop Kafka producer for reporting state.
	terminate.addStopStep(this, getStateUpdatesKafkaProducer());

	// Terminate Zk manager.
	terminate.addStopStep(this, getZookeeperManager());

	// Stop Hazelcast manager.
	terminate.addStopStep(this, getHazelcastManager());

	// Terminate Hazelcast manager.
	terminate.addTerminateStep(this, getHazelcastManager());

	// Execute shutdown steps.
	terminate.execute(monitor);
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
    public String getHostname() {
	try {
	    InetAddress local = InetAddress.getLocalHost();
	    return local.getHostName();
	} catch (UnknownHostException e) {
	    throw new RuntimeException("Unable to find hostname.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getMicroserviceDetails()
     */
    @Override
    public IMicroserviceDetails getMicroserviceDetails() {
	MicroserviceDetails details = new MicroserviceDetails();
	details.setIdentifier(getIdentifier());
	details.setHostname(getHostname());

	IElementNode root = getRootElementNode();
	details.setName(root.getName());
	details.setIcon(root.getIcon());
	details.setDescription(root.getDescription());
	details.setGlobal(isGlobal());
	return details;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getCurrentState()
     */
    @Override
    public IMicroserviceState getCurrentState() throws SiteWhereException {
	MicroserviceState state = new MicroserviceState();
	state.setMicroserviceDetails(getMicroserviceDetails());
	state.setLifecycleStatus(getLifecycleStatus());
	return state;
    }

    /**
     * Get element node for root configuration role.
     * 
     * @return
     */
    protected IElementNode getRootElementNode() {
	IElementRole role = getConfigurationModel().getRolesById().get(getConfigurationModel().getRootRoleId());
	if (role == null) {
	    throw new RuntimeException("Root role was not found for configuration model.");
	}
	List<IElementNode> matches = getConfigurationModel().getElementsByRole()
		.get(getConfigurationModel().getRootRoleId());
	if (matches == null) {
	    throw new RuntimeException("Configuration had no elements for root role.");
	}
	if (matches.size() != 1) {
	    throw new RuntimeException("Configuration model had " + matches.size() + " elements for root role.");
	}
	return matches.get(0);
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
	    getStateUpdatesKafkaProducer().send(state);
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
	    getStateUpdatesKafkaProducer().send(state);
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
     * @see com.sitewhere.spi.microservice.IMicroservice#getConfigurationModel()
     */
    @Override
    public IConfigurationModel getConfigurationModel() {
	return configurationModel;
    }

    public void setConfigurationModel(IConfigurationModel configurationModel) {
	this.configurationModel = configurationModel;
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
     * @see com.sitewhere.spi.microservice.IMicroservice#getHazelcastManager()
     */
    @Override
    public IHazelcastManager getHazelcastManager() {
	return hazelcastManager;
    }

    public void setHazelcastManager(IHazelcastManager hazelcastManager) {
	this.hazelcastManager = hazelcastManager;
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
     * @see com.sitewhere.spi.microservice.IMicroservice#
     * getMicroserviceManagementGrpcServer()
     */
    @Override
    public IMicroserviceManagementGrpcServer getMicroserviceManagementGrpcServer() {
	return microserviceManagementGrpcServer;
    }

    public void setMicroserviceManagementGrpcServer(
	    IMicroserviceManagementGrpcServer microserviceManagementGrpcServer) {
	this.microserviceManagementGrpcServer = microserviceManagementGrpcServer;
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
     * @see com.sitewhere.spi.microservice.IMicroservice#
     * getInstanceTopologyUpdatesManager()
     */
    @Override
    public IInstanceTopologyUpdatesManager getInstanceTopologyUpdatesManager() {
	return instanceTopologyUpdatesManager;
    }

    public void setInstanceTopologyUpdatesManager(IInstanceTopologyUpdatesManager instanceTopologyUpdatesManager) {
	this.instanceTopologyUpdatesManager = instanceTopologyUpdatesManager;
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

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroservice#getMicroserviceOperationsService
     * ()
     */
    @Override
    public ExecutorService getMicroserviceOperationsService() {
	return microserviceOperationsService;
    }

    public void setMicroserviceOperationsService(ExecutorService microserviceOperationsService) {
	this.microserviceOperationsService = microserviceOperationsService;
    }

    /** Used for naming microservice operation threads */
    private class MicroserviceOperationsThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Microservice Operations " + counter.incrementAndGet());
	}
    }
}