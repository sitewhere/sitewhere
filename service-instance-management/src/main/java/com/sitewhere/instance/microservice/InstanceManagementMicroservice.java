/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.microservice;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.data.Stat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.grpc.model.client.TenantManagementApiChannel;
import com.sitewhere.grpc.model.client.TenantManagementGrpcChannel;
import com.sitewhere.grpc.model.client.UserManagementApiChannel;
import com.sitewhere.grpc.model.client.UserManagementGrpcChannel;
import com.sitewhere.grpc.model.spi.client.ITenantManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IUserManagementApiChannel;
import com.sitewhere.instance.initializer.GroovyTenantModelInitializer;
import com.sitewhere.instance.initializer.GroovyUserModelInitializer;
import com.sitewhere.instance.kafka.StateAggregatorKafkaConsumer;
import com.sitewhere.instance.spi.kafka.IStateAggregatorKafkaConsumer;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.instance.spi.templates.IInstanceTemplate;
import com.sitewhere.instance.spi.templates.IInstanceTemplateManager;
import com.sitewhere.instance.templates.InstanceTemplateManager;
import com.sitewhere.microservice.Microservice;
import com.sitewhere.microservice.MicroserviceEnvironment;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.microservice.groovy.InstanceScriptSynchronizer;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.server.lifecycle.SimpleLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;

/**
 * Microservice that provides instance management functionality.
 * 
 * @author Derek
 */
public class InstanceManagementMicroservice extends Microservice implements IInstanceManagementMicroservice {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice name */
    private static final String NAME = "Instance Management";

    /** Microservice identifier */
    private static final String IDENTIFIER = "instance-management";

    /** Instance template manager */
    private IInstanceTemplateManager instanceTemplateManager = new InstanceTemplateManager();

    /** User management GRPC channel */
    private UserManagementGrpcChannel userManagementGrpcChannel;

    /** User management API channel */
    private IUserManagementApiChannel userManagementApiChannel;

    /** Tenant management GRPC channel */
    private TenantManagementGrpcChannel tenantManagementGrpcChannel;

    /** Tenant management API channel */
    private ITenantManagementApiChannel tenantManagementApiChannel;

    /** State aggregator Kafka consumer */
    private IStateAggregatorKafkaConsumer stateAggregatorKafkaConsumer;

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
     * @see com.sitewhere.microservice.Microservice#initialize(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Create GRPC components.
	createGrpcComponents();

	// Create state aggregator.
	this.stateAggregatorKafkaConsumer = new StateAggregatorKafkaConsumer(this);
    }

    /**
     * Create components that interact via GRPC.
     */
    protected void createGrpcComponents() {
	this.userManagementGrpcChannel = new UserManagementGrpcChannel(this,
		MicroserviceEnvironment.HOST_USER_MANAGEMENT, getInstanceSettings().getGrpcPort());
	this.userManagementApiChannel = new UserManagementApiChannel(getUserManagementGrpcChannel());

	this.tenantManagementGrpcChannel = new TenantManagementGrpcChannel(this,
		MicroserviceEnvironment.HOST_TENANT_MANAGEMENT, getInstanceSettings().getGrpcPort());
	this.tenantManagementApiChannel = new TenantManagementApiChannel(getTenantManagementGrpcChannel());
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
	super.start(monitor);

	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getName());

	// Verify or create Zk node for instance information.
	start.addStep(verifyOrCreateInstanceNode());

	// Start instance template manager.
	start.addStartStep(this, getInstanceTemplateManager(), true);

	// Initialize user management GRPC channel.
	start.addInitializeStep(this, getUserManagementGrpcChannel(), true);

	// Start user mangement GRPC channel.
	start.addStartStep(this, getUserManagementGrpcChannel(), true);

	// Initialize tenant management GRPC channel.
	start.addInitializeStep(this, getTenantManagementGrpcChannel(), true);

	// Start tenant mangement GRPC channel.
	start.addStartStep(this, getTenantManagementGrpcChannel(), true);

	// Initialize state aggregator consumer.
	start.addInitializeStep(this, getStateAggregatorKafkaConsumer(), true);

	// Start state aggregator consumer.
	start.addStartStep(this, getStateAggregatorKafkaConsumer(), true);

	// Verify Zk node for instance configuration or bootstrap instance.
	start.addStep(verifyOrBootstrapConfiguration());

	// Execute initialization steps.
	start.execute(monitor);
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
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop state aggregator consumer.
	stop.addStopStep(this, getStateAggregatorKafkaConsumer());

	// Stop tenant management GRPC channel.
	stop.addStopStep(this, getTenantManagementGrpcChannel());

	// Stop user management GRPC channel.
	stop.addStopStep(this, getUserManagementGrpcChannel());

	// Stop instance template manager.
	stop.addStopStep(this, getInstanceTemplateManager());

	// Execute shutdown steps.
	stop.execute(monitor);

	super.stop(monitor);
    }

    /**
     * Verify that a Zk node exists to hold instance information. Create the folder
     * if it does not exist. Other microservices block while waiting on this node to
     * be created.
     * 
     * @return
     */
    public ILifecycleStep verifyOrCreateInstanceNode() {
	return new SimpleLifecycleStep("Verify instance bootstrapped") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		try {
		    Stat existing = getZookeeperManager().getCurator().checkExists().forPath(getInstanceZkPath());
		    if (existing == null) {
			LOGGER.info("Zk node for instance not found. Creating...");
			getZookeeperManager().getCurator().create().forPath(getInstanceZkPath());
			LOGGER.info("Created instance Zk node.");
		    } else {
			LOGGER.info("Found Zk node for instance.");
		    }
		} catch (Exception e) {
		    throw new SiteWhereException(e);
		}
	    }
	};
    }

    /**
     * Verify that a Zk node exists to hold instance configuration information.
     * Create the folder and bootstrap from the instance template if it does not
     * exist. Other microservices block while waiting on this node to be created.
     * 
     * @return
     */
    public ILifecycleStep verifyOrBootstrapConfiguration() {
	return new SimpleLifecycleStep("Verify instance configured") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		try {
		    Stat existing = getZookeeperManager().getCurator().checkExists()
			    .forPath(getInstanceBootstrappedMarker());
		    if (existing == null) {
			LOGGER.info("Bootstrap marker node '" + getInstanceBootstrappedMarker()
				+ "' not found. Bootstrapping...");
			bootstrapInstanceConfiguration();
			LOGGER.info("Bootstrapped instance configuration from template.");
		    } else {
			LOGGER.info("Found bootstrap marker node. Skipping instance bootstrap.");
		    }
		} catch (SiteWhereException e) {
		    throw e;
		} catch (Exception e) {
		    throw new SiteWhereException(e);
		}
	    }
	};
    }

    /**
     * Bootstrap instance configuration data from chosen instance template.
     * 
     * @throws SiteWhereException
     */
    protected void bootstrapInstanceConfiguration() throws SiteWhereException {
	try {
	    getInstanceTemplateManager().copyTemplateContentsToZk(getInstanceSettings().getInstanceTemplateId(),
		    getZookeeperManager().getCurator(), getInstanceZkPath());
	    getZookeeperManager().getCurator().create().forPath(getInstanceBootstrappedMarker());
	    initializeModelFromInstanceTemplate();
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    /**
     * Initialize user/tenant model from scripts included in instance template.
     * Note: The scripts execute in the context of the system superuser so that the
     * initial users/tenants can be populated.
     * 
     * @throws SiteWhereException
     */
    protected void initializeModelFromInstanceTemplate() throws SiteWhereException {
	Authentication previous = SecurityContextHolder.getContext().getAuthentication();
	try {
	    SecurityContextHolder.getContext().setAuthentication(getSystemUser().getAuthentication());
	    IInstanceTemplate template = getChosenInstanceTemplate();
	    getLogger().info("Initializing instance from template '" + template.getName() + "'.");
	    String templatePath = getInstanceZkPath() + "/" + template.getId();
	    if (template.getInitializers() != null) {
		List<String> userScripts = template.getInitializers().getUserManagement();
		initializeUserModelFromInstanceTemplateScripts(templatePath, userScripts);

		List<String> tenantScripts = template.getInitializers().getTenantManagement();
		initializeTenantModelFromInstanceTemplateScripts(templatePath, tenantScripts);
	    }
	} finally {
	    SecurityContextHolder.getContext().setAuthentication(previous);
	}
    }

    /**
     * Initialize user model from scripts included in instance template scripts.
     * 
     * @param templatePath
     * @param scripts
     * @throws SiteWhereException
     */
    protected void initializeUserModelFromInstanceTemplateScripts(String templatePath, List<String> scripts)
	    throws SiteWhereException {
	InstanceScriptSynchronizer synchronizer = new InstanceScriptSynchronizer(this);
	for (String script : scripts) {
	    synchronizer.add(script);
	}

	// Wait for user management APIs to become available.
	getUserManagementApiChannel().waitForApiAvailable();
	getLogger().info("User management API detected as available.");

	GroovyConfiguration groovy = new GroovyConfiguration(synchronizer);
	groovy.start(new LifecycleProgressMonitor(new LifecycleProgressContext(1, "Initialize user model."), this));
	for (String script : scripts) {
	    GroovyUserModelInitializer initializer = new GroovyUserModelInitializer(groovy, script);
	    initializer.initialize(getUserManagementApiChannel());
	}
    }

    /**
     * Initialize tenant model from scripts included in instance template scripts.
     * 
     * @param templatePath
     * @param scripts
     * @throws SiteWhereException
     */
    protected void initializeTenantModelFromInstanceTemplateScripts(String templatePath, List<String> scripts)
	    throws SiteWhereException {
	InstanceScriptSynchronizer synchronizer = new InstanceScriptSynchronizer(this);
	for (String script : scripts) {
	    synchronizer.add(script);
	}

	// Wait for tenant management APIs to become available.
	getTenantManagementApiChannel().waitForApiAvailable();
	getLogger().info("Tenant management API detected as available.");

	GroovyConfiguration groovy = new GroovyConfiguration(synchronizer);
	groovy.start(new LifecycleProgressMonitor(new LifecycleProgressContext(1, "Initialize tenant model."), this));
	for (String script : scripts) {
	    GroovyTenantModelInitializer initializer = new GroovyTenantModelInitializer(groovy, script);
	    initializer.initialize(getTenantManagementApiChannel());
	}
    }

    /**
     * Get instance template chosen via enviroment variable or default.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IInstanceTemplate getChosenInstanceTemplate() throws SiteWhereException {
	String templateId = getInstanceSettings().getInstanceTemplateId();
	IInstanceTemplate template = getInstanceTemplateManager().getInstanceTemplates().get(templateId);
	if (template == null) {
	    throw new SiteWhereException("Unable to locate instance template: " + templateId);
	}
	return template;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.instance.spi.microservice.IInstanceManagement#
     * getInstanceTemplateManager()
     */
    @Override
    public IInstanceTemplateManager getInstanceTemplateManager() {
	return instanceTemplateManager;
    }

    public void setInstanceTemplateManager(IInstanceTemplateManager instanceTemplateManager) {
	this.instanceTemplateManager = instanceTemplateManager;
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

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice#
     * getUserManagementApiChannel()
     */
    @Override
    public IUserManagementApiChannel getUserManagementApiChannel() {
	return userManagementApiChannel;
    }

    public void setUserManagementApiChannel(IUserManagementApiChannel userManagementApiChannel) {
	this.userManagementApiChannel = userManagementApiChannel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice#
     * getTenantManagementApiChannel()
     */
    @Override
    public ITenantManagementApiChannel getTenantManagementApiChannel() {
	return tenantManagementApiChannel;
    }

    public void setTenantManagementApiChannel(ITenantManagementApiChannel tenantManagementApiChannel) {
	this.tenantManagementApiChannel = tenantManagementApiChannel;
    }

    /*
     * @see com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice#
     * getStateAggregatorKafkaConsumer()
     */
    @Override
    public IStateAggregatorKafkaConsumer getStateAggregatorKafkaConsumer() {
	return stateAggregatorKafkaConsumer;
    }

    public void setStateAggregatorKafkaConsumer(IStateAggregatorKafkaConsumer stateAggregatorKafkaConsumer) {
	this.stateAggregatorKafkaConsumer = stateAggregatorKafkaConsumer;
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
}