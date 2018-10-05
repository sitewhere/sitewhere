/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Slf4jReporter;
import com.google.common.net.HostAndPort;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.sitewhere.Version;
import com.sitewhere.microservice.logging.MicroserviceLogProducer;
import com.sitewhere.microservice.management.MicroserviceManagementGrpcServer;
import com.sitewhere.microservice.scripting.ScriptTemplateManager;
import com.sitewhere.microservice.state.MicroserviceStateUpdatesKafkaProducer;
import com.sitewhere.rest.model.configuration.ConfigurationModel;
import com.sitewhere.rest.model.microservice.state.MicroserviceDetails;
import com.sitewhere.rest.model.microservice.state.MicroserviceState;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.server.lifecycle.TracerUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceAnalytics;
import com.sitewhere.spi.microservice.configuration.IZookeeperManager;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.configuration.model.IElementNode;
import com.sitewhere.spi.microservice.configuration.model.IElementRole;
import com.sitewhere.spi.microservice.grpc.IMicroserviceManagementGrpcServer;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming;
import com.sitewhere.spi.microservice.logging.IMicroserviceLogProducer;
import com.sitewhere.spi.microservice.scripting.IScriptTemplateManager;
import com.sitewhere.spi.microservice.security.ISystemUser;
import com.sitewhere.spi.microservice.security.ITokenManagement;
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
public abstract class Microservice<T extends IFunctionIdentifier> extends LifecycleComponent
	implements IMicroservice<T> {

    /** Instance configuration folder name */
    private static final String INSTANCE_CONFIGURATION_FOLDER = "/conf";

    /** Instance state folder name */
    private static final String INSTANCE_STATE_FOLDER = "/state";

    /** Relative path to instance bootstrap marker */
    private static final String INSTANCE_BOOTSTRAP_MARKER = "/bootstrapped";

    /** Number of seconds to wait between checks for isntance bootstrap marker */
    private static final int INSTANCE_BOOTSTRAP_CHECK_INTERVAL_SECS = 3;

    /** Heartbeat interval in seconds */
    private static final int HEARTBEAT_INTERVAL_SECS = 10;

    /** Relative path on local filesystem to script templates folder */
    private static final String SCRIPT_TEMPLATES_FOLDER_PATH = "/script-templates";

    /** Instance settings */
    @Autowired
    private IInstanceSettings instanceSettings;

    /** Zookeeper manager */
    @Autowired
    private IZookeeperManager zookeeperManager;

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

    /** Client for interacting with Consul */
    private Consul consulClient;

    /** Version information */
    private IVersion version = new Version();

    /** Configuration model */
    private IConfigurationModel configurationModel;

    /** Microservice management GRPC server */
    private IMicroserviceManagementGrpcServer microserviceManagementGrpcServer;

    /** Kafka producer for microservice state updates */
    private IMicroserviceStateUpdatesKafkaProducer stateUpdatesKafkaProducer;

    /** Kafka producer for centralized log handling */
    private IMicroserviceLogProducer microserviceLogProducer;

    /** Script template manager instance */
    private IScriptTemplateManager scriptTemplateManager;

    /** Microservice runtime analytics interface */
    private IMicroserviceAnalytics microserviceAnalytics = new MicroserviceAnalytics();

    /** Lifecycle operations thread pool */
    private ExecutorService microserviceOperationsService;

    /** Executor for heartbeat */
    private ExecutorService microserviceHeartbeatService;

    /** Metric registry */
    private MetricRegistry metricRegistry = new MetricRegistry();

    /** Metrics reporter */
    private ScheduledReporter metricsReporter;

    /** Unique id for microservice */
    private UUID id = UUID.randomUUID();

    /** Timestamp in milliseconds when service started */
    private long startTime;

    public Microservice() {
	this.microserviceOperationsService = Executors
		.newSingleThreadExecutor(new MicroserviceOperationsThreadFactory());
	this.configurationModel = buildConfigurationModel();
	((ConfigurationModel) configurationModel).setMicroserviceDetails(getMicroserviceDetails());

	// Create log producer so it can buffer log output.
	this.microserviceLogProducer = new MicroserviceLogProducer();

	// Create script template manager.
	this.scriptTemplateManager = new ScriptTemplateManager();
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    /*
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#getMicroservice()
     */
    @Override
    public IMicroservice<T> getMicroservice() {
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Initialize metrics logging.
	initializeMetrics();

	// Initialize GRPC components.
	initializeGrpcComponents();

	// Initialize state management components.
	initializeStateManagement();

	// Organizes steps for initializing microservice.
	ICompositeLifecycleStep initialize = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize script template manager.
	initialize.addInitializeStep(this, getScriptTemplateManager(), true);

	// Start script template manager.
	initialize.addStartStep(this, getScriptTemplateManager(), true);

	// Initialize Kafka producer for centralized logging.
	initialize.addInitializeStep(this, getMicroserviceLogProducer(), true);

	// Start Kafka producer for centralized logging.
	initialize.addStartStep(this, getMicroserviceLogProducer(), true);

	// Initialize Zookeeper configuration management.
	initialize.addInitializeStep(this, getZookeeperManager(), true);

	// Initialize microservice management GRPC server.
	initialize.addInitializeStep(this, getMicroserviceManagementGrpcServer(), true);

	// Start microservice management GRPC server.
	initialize.addStartStep(this, getMicroserviceManagementGrpcServer(), true);

	// Initialize Kafka producer for reporting state.
	initialize.addInitializeStep(this, getStateUpdatesKafkaProducer(), true);

	// Start Kafka producer for reporting state.
	initialize.addStartStep(this, getStateUpdatesKafkaProducer(), true);

	// Execute initialization steps.
	initialize.execute(monitor);

	// Initialize Consul client.
	initializeConsulClient();

	// Start sending heartbeats.
	getMicroserviceHeartbeatService().execute(new Heartbeat());

	// Record start time.
	this.startTime = System.currentTimeMillis();
	getMicroserviceAnalytics().sendMicroserviceStarted(this);
    }

    /**
     * Initialize metrics.
     */
    protected void initializeMetrics() {
	if (getInstanceSettings().isLogMetrics()) {
	    this.metricsReporter = Slf4jReporter.forRegistry(getMetricRegistry()).convertRatesTo(TimeUnit.SECONDS)
		    .convertDurationsTo(TimeUnit.MILLISECONDS).build();
	    getMetricsReporter().start(20, TimeUnit.SECONDS);
	} else {
	    getLogger().info(MicroserviceMessages.METRICS_REPORTING_DISABLED);
	}
    }

    /**
     * Initialize Consul client based on instance settings.
     */
    protected void initializeConsulClient() {
	// Create Consul client.
	this.consulClient = Consul.builder().withHostAndPort(
		HostAndPort.fromParts(getInstanceSettings().getConsulHost(), getInstanceSettings().getConsulPort()))
		.build();

	// Register microservice with Consul.
	AgentClient agentClient = getConsulClient().agentClient();
	List<String> tags = new ArrayList<>();
	tags.add("microservice");
	tags.add(getIdentifier().getShortName());
	Registration service = ImmutableRegistration.builder().id(getId().toString())
		.name(getIdentifier().getShortName()).address(getHostname()).port(getInstanceSettings().getGrpcPort())
		.check(Registration.RegCheck.ttl(30L)).tags(tags)
		.meta(Collections.singletonMap("version", getVersion().getVersionIdentifier())).build();
	agentClient.register(service);
    }

    /**
     * Initialize GRPC components.
     */
    protected void initializeGrpcComponents() {
	this.microserviceManagementGrpcServer = new MicroserviceManagementGrpcServer(this);
    }

    /**
     * Initialize components related to state management.
     */
    protected void initializeStateManagement() {
	this.stateUpdatesKafkaProducer = new MicroserviceStateUpdatesKafkaProducer();
	this.microserviceHeartbeatService = Executors.newSingleThreadExecutor(new MicroserviceHeartbeatThreadFactory());
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
	getMicroserviceAnalytics().sendMicroserviceStopped(this);

	// Stop reporting metrics.
	if (getMetricsReporter() != null) {
	    getMetricsReporter().stop();
	}

	// Stop sending heartbeats.
	if (getMicroserviceHeartbeatService() != null) {
	    getMicroserviceHeartbeatService().shutdownNow();
	}

	// Create step that will stop components.
	ICompositeLifecycleStep terminate = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop microservice management GRPC server.
	terminate.addStopStep(this, getMicroserviceManagementGrpcServer());

	// Stop Kafka producer for reporting state.
	terminate.addStopStep(this, getStateUpdatesKafkaProducer());

	// Terminate Zk manager.
	terminate.addStopStep(this, getZookeeperManager());

	// Terminate log producer.
	terminate.addStopStep(this, getMicroserviceLogProducer());

	// Terminate script template manager.
	terminate.addStopStep(this, getScriptTemplateManager());

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
	    getLogger().info(MicroserviceMessages.INSTANCE_VERIFY_BOOTSTRAPPED);
	    while (true) {
		if (getZookeeperManager().getCurator().checkExists().forPath(getInstanceBootstrappedMarker()) != null) {
		    break;
		}
		getLogger().info(MicroserviceMessages.INSTANCE_BOOTSTRAP_MARKER_NOT_FOUND,
			getInstanceBootstrappedMarker());
		Thread.sleep(INSTANCE_BOOTSTRAP_CHECK_INTERVAL_SECS * 1000);
	    }
	    getLogger().info(MicroserviceMessages.INSTANCE_BOOTSTRAP_CONFIRMED);
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
	details.setIdentifier(getIdentifier().getPath());
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
	state.setMicroservice(getMicroserviceDetails());
	state.setLifecycleStatus(getLifecycleStatus());
	state.setUptime(System.currentTimeMillis() - startTime);
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
	getLogger().info(MicroserviceMessages.LIFECYCLE_STATUS_SENDING, getLifecycleStatus().name());
	sendChangedState();
    }

    /**
     * Send current state for microservice to Kafka topic.
     */
    protected void sendChangedState() {
	if ((getStateUpdatesKafkaProducer() != null)
		&& (getStateUpdatesKafkaProducer().getLifecycleStatus() == LifecycleStatus.Started)) {
	    try {
		IMicroserviceState state = getCurrentState();
		getStateUpdatesKafkaProducer().send(state);
	    } catch (SiteWhereException e) {
		getLogger().error(e, MicroserviceMessages.LIFECYCLE_STATUS_SEND_EXCEPTION);
	    }
	} else {
	    getLogger().warn(MicroserviceMessages.LIFECYCLE_STATUS_FAILED_NO_KAFKA);
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
     * @see com.sitewhere.spi.microservice.IMicroservice#getInstanceStatePath()
     */
    @Override
    public String getInstanceStatePath() {
	return getInstanceZkPath() + INSTANCE_STATE_FOLDER;
    }

    /*
     * @see com.sitewhere.microservice.spi.IMicroservice#
     * getInstanceBootstrappedMarker()
     */
    @Override
    public String getInstanceBootstrappedMarker() throws SiteWhereException {
	return getInstanceStatePath() + INSTANCE_BOOTSTRAP_MARKER;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getScriptTemplatesRoot()
     */
    @Override
    public File getScriptTemplatesRoot() {
	return new File(SCRIPT_TEMPLATES_FOLDER_PATH);
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
     * @see com.sitewhere.spi.microservice.IMicroservice#getConsulClient()
     */
    @Override
    public Consul getConsulClient() {
	return consulClient;
    }

    public void setConsulClient(Consul consulClient) {
	this.consulClient = consulClient;
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
     * @see
     * com.sitewhere.spi.microservice.IMicroservice#getMicroserviceLogProducer()
     */
    @Override
    public IMicroserviceLogProducer getMicroserviceLogProducer() {
	return microserviceLogProducer;
    }

    public void setMicroserviceLogProducer(IMicroserviceLogProducer microserviceLogProducer) {
	this.microserviceLogProducer = microserviceLogProducer;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getScriptTemplateManager()
     */
    @Override
    public IScriptTemplateManager getScriptTemplateManager() {
	return scriptTemplateManager;
    }

    public void setScriptTemplateManager(IScriptTemplateManager scriptTemplateManager) {
	this.scriptTemplateManager = scriptTemplateManager;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getMicroserviceAnalytics()
     */
    @Override
    public IMicroserviceAnalytics getMicroserviceAnalytics() {
	return microserviceAnalytics;
    }

    public void setMicroserviceAnalytics(IMicroserviceAnalytics microserviceAnalytics) {
	this.microserviceAnalytics = microserviceAnalytics;
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
     * @see com.sitewhere.spi.microservice.IMicroservice#getMetricRegistry()
     */
    @Override
    public MetricRegistry getMetricRegistry() {
	return metricRegistry;
    }

    public void setMetricRegistry(MetricRegistry metricRegistry) {
	this.metricRegistry = metricRegistry;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getMetricsReporter()
     */
    @Override
    public ScheduledReporter getMetricsReporter() {
	return metricsReporter;
    }

    public void setMetricsReporter(ScheduledReporter metricsReporter) {
	this.metricsReporter = metricsReporter;
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

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroservice#getMicroserviceHeartbeatService(
     * )
     */
    @Override
    public ExecutorService getMicroserviceHeartbeatService() {
	return microserviceHeartbeatService;
    }

    public void setMicroserviceHeartbeatService(ExecutorService microserviceHeartbeatService) {
	this.microserviceHeartbeatService = microserviceHeartbeatService;
    }

    /**
     * Delivers microservice state as a heartbeat indication to other microservices.
     * 
     * @author Derek
     */
    private class Heartbeat implements Runnable {

	@Override
	public void run() {
	    while (true) {
		try {
		    getLogger().debug("Sending heartbeat.");
		    AgentClient agentClient = getConsulClient().agentClient();
		    agentClient.pass(getId().toString());
		    sendChangedState();
		} catch (Throwable t) {
		    getLogger().error("Unable to send state for heartbeat.", t);
		}

		try {
		    Thread.sleep(HEARTBEAT_INTERVAL_SECS * 1000);
		} catch (InterruptedException e) {
		    getLogger().warn("Heartbeat service shutting down.");
		    return;
		}
	    }
	}
    }

    /** Used for naming microservice operation threads */
    private class MicroserviceOperationsThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Microservice Operations " + counter.incrementAndGet());
	}
    }

    /** Used for naming microservice heartbeat thread */
    private class MicroserviceHeartbeatThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Microservice Heartbeat " + counter.incrementAndGet());
	}
    }
}