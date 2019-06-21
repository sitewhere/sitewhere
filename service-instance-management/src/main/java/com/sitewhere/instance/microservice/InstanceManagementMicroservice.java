/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.microservice;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.instance.configuration.InstanceManagementModelProvider;
import com.sitewhere.instance.initializer.GroovyTenantModelInitializer;
import com.sitewhere.instance.initializer.GroovyUserModelInitializer;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.instance.spi.templates.IInstanceTemplate;
import com.sitewhere.instance.spi.templates.IInstanceTemplateManager;
import com.sitewhere.instance.spi.tenant.grpc.ITenantManagementGrpcServer;
import com.sitewhere.instance.spi.tenant.kafka.ITenantBootstrapModelConsumer;
import com.sitewhere.instance.spi.tenant.kafka.ITenantModelProducer;
import com.sitewhere.instance.spi.tenant.templates.IDatasetTemplateManager;
import com.sitewhere.instance.spi.tenant.templates.ITenantTemplateManager;
import com.sitewhere.instance.spi.user.grpc.IUserManagementGrpcServer;
import com.sitewhere.instance.templates.InstanceTemplateManager;
import com.sitewhere.instance.tenant.persistence.ZookeeperTenantManagement;
import com.sitewhere.instance.tenant.templates.DatasetTemplateManager;
import com.sitewhere.instance.tenant.templates.TenantTemplateManager;
import com.sitewhere.instance.user.persistence.SyncopeUserManagement;
import com.sitewhere.microservice.GlobalMicroservice;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.microservice.grpc.tenant.TenantManagementGrpcServer;
import com.sitewhere.microservice.grpc.user.UserManagementGrpcServer;
import com.sitewhere.microservice.kafka.tenant.TenantBootstrapModelConsumer;
import com.sitewhere.microservice.kafka.tenant.TenantManagementKafkaTriggers;
import com.sitewhere.microservice.kafka.tenant.TenantModelProducer;
import com.sitewhere.microservice.kafka.user.UserManagementKafkaTriggers;
import com.sitewhere.microservice.scripting.InstanceScriptSynchronizer;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.server.lifecycle.SimpleLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
import com.sitewhere.spi.microservice.scripting.IScriptSynchronizer;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.user.IUserManagement;

/**
 * Microservice that provides instance management functionality.
 */
public class InstanceManagementMicroservice extends GlobalMicroservice<MicroserviceIdentifier>
	implements IInstanceManagementMicroservice<MicroserviceIdentifier> {

    /** Microservice name */
    private static final String NAME = "Instance Management";

    /** User management configuration file name */
    private static final String CONFIGURATION_PATH = MicroserviceIdentifier.InstanceManagement.getPath() + ".xml";

    /** Instance template manager */
    private IInstanceTemplateManager instanceTemplateManager = new InstanceTemplateManager();

    /** Instance script synchronizer */
    private IScriptSynchronizer instanceScriptSynchronizer;

    /** Responds to user management GRPC requests */
    private IUserManagementGrpcServer userManagementGrpcServer;

    /** User management implementation */
    private IUserManagement userManagement;

    /** Responds to tenant management GRPC requests */
    private ITenantManagementGrpcServer tenantManagementGrpcServer;

    /** Tenant management implementation */
    private ITenantManagement tenantManagement;

    /** Tenant template manager */
    private ITenantTemplateManager tenantConfigurationTemplateManager = new TenantTemplateManager();

    /** Dataset template manager */
    private IDatasetTemplateManager tenantDatasetTemplateManager = new DatasetTemplateManager();

    /** Reflects tenant model updates to Kafka topic */
    private ITenantModelProducer tenantModelProducer;

    /** Watches tenant model updates and bootstraps new tenants */
    private ITenantBootstrapModelConsumer tenantBootstrapModelConsumer;

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
	return MicroserviceIdentifier.InstanceManagement;
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
	return new InstanceManagementModelProvider().buildModel();
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
     * @see com.sitewhere.microservice.Microservice#waitForInstanceInitialization()
     */
    @Override
    public void waitForInstanceInitialization() throws SiteWhereException {
	// Prevent deadlock waiting for self.
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationListener#
     * onConfigurationCacheInitialized()
     */
    @Override
    public void onConfigurationCacheInitialized() {
	// Override default configuration loading behavior because instance bootstrap
	// must be done first.
	setConfigurationCacheReady(true);
    }

    /*
     * @see com.sitewhere.microservice.configuration.ConfigurableMicroservice#
     * waitForConfigurationReady()
     */
    @Override
    public void waitForConfigurationReady() throws SiteWhereException {
	// Configuration will be loaded manually after instance bootstrap.
    }

    /*
     * @see com.sitewhere.microservice.configuration.ConfigurableMicroservice#
     * microserviceInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create script synchronizer.
	this.instanceScriptSynchronizer = new InstanceScriptSynchronizer();

	// Create Kafka components.
	createKafkaComponents();

	// Create management implementations.
	createManagementImplementations();

	// Create GRPC components.
	createGrpcComponents();

	// Create step that will start components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize tenant management implementation.
	init.addInitializeStep(this, getTenantManagement(), true);

	// Initialize tenant configuration template manager.
	init.addInitializeStep(this, getTenantConfigurationTemplateManager(), true);

	// Initialize tenant dataset template manager.
	init.addInitializeStep(this, getTenantDatasetTemplateManager(), true);

	// Initialize tenant management GRPC server.
	init.addInitializeStep(this, getTenantManagementGrpcServer(), true);

	// Initialize tenant bootstrap model consumer.
	init.addInitializeStep(this, getTenantBootstrapModelConsumer(), true);

	// Initialize tenant model producer.
	init.addInitializeStep(this, getTenantModelProducer(), true);

	// Initialize user management implementation.
	init.addInitializeStep(this, getUserManagement(), true);

	// Initialize user management GRPC server.
	init.addInitializeStep(this, getUserManagementGrpcServer(), true);

	// Initialize instance script synchronizer.
	init.addInitializeStep(this, getInstanceScriptSynchronizer(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /**
     * Create management implementations.
     */
    protected void createManagementImplementations() {
	this.userManagement = new UserManagementKafkaTriggers(new SyncopeUserManagement());
	this.tenantManagement = new TenantManagementKafkaTriggers(new ZookeeperTenantManagement());
    }

    /**
     * Create components that interact via GRPC.
     */
    protected void createGrpcComponents() {
	this.userManagementGrpcServer = new UserManagementGrpcServer(this, getUserManagement());
	this.tenantManagementGrpcServer = new TenantManagementGrpcServer(this, getTenantManagement(), this);
    }

    /**
     * Create Apache Kafka components.
     * 
     * @throws SiteWhereException
     */
    protected void createKafkaComponents() throws SiteWhereException {
	this.tenantModelProducer = new TenantModelProducer();
	this.tenantBootstrapModelConsumer = new TenantBootstrapModelConsumer();
    }

    /*
     * @see com.sitewhere.microservice.configuration.ConfigurableMicroservice#
     * microserviceStart(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getName());

	// Start instance template manager.
	start.addStartStep(this, getInstanceTemplateManager(), true);

	// Start instance script synchronizer.
	start.addStartStep(this, getInstanceScriptSynchronizer(), true);

	// Verify or create Zk configuration based on instance template.
	start.addStep(verifyOrBootstrapInstanceConfigurationModel());

	// Start tenant management implementation.
	start.addStartStep(this, getTenantManagement(), true);

	// Start tenant configuration template manager.
	start.addStartStep(this, getTenantConfigurationTemplateManager(), true);

	// Start tenant dataset template manager.
	start.addStartStep(this, getTenantDatasetTemplateManager(), true);

	// Start GRPC server.
	start.addStartStep(this, getTenantManagementGrpcServer(), true);

	// Start tenant bootstrap model consumer.
	start.addStartStep(this, getTenantBootstrapModelConsumer(), true);

	// Start tenant model producer.
	start.addStartStep(this, getTenantModelProducer(), true);

	// Boostrap tenants from instance template configuration.
	start.addStep(initializeTenantModelFromInstanceTemplate());

	// Start user management implementation.
	start.addStartStep(this, getUserManagement(), true);

	// Start user management GRPC server.
	start.addStartStep(this, getUserManagementGrpcServer(), true);

	// Boostrap users from instance template configuration.
	start.addStep(initializeUserModelFromInstanceTemplate());

	// Execute initialization steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.microservice.configuration.ConfigurableMicroservice#
     * microserviceStop(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop tenant model producer.
	stop.addStopStep(this, getTenantModelProducer());

	// Stop tenant bootstrap model consumer.
	stop.addStopStep(this, getTenantBootstrapModelConsumer());

	// Stop GRPC manager.
	stop.addStopStep(this, getTenantManagementGrpcServer());

	// Stop user management GRPC server.
	stop.addStopStep(this, getUserManagementGrpcServer());

	// Stop tenant dataset template manager.
	stop.addStopStep(this, getTenantDatasetTemplateManager());

	// Stop tenant configuration template manager.
	stop.addStopStep(this, getTenantConfigurationTemplateManager());

	// Stop instance script synchronizer.
	stop.addStopStep(this, getInstanceScriptSynchronizer());

	// Stop instance template manager.
	stop.addStopStep(this, getInstanceTemplateManager());

	// Stop tenant management implementation.
	stop.addStopStep(this, getTenantManagement());

	// Stop user management implementation.
	stop.addStopStep(this, getUserManagement());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /**
     * Verify that a Zk node exists to hold instance information. Create the folder
     * if it does not exist. Other microservices block while waiting on this node to
     * be created.
     * 
     * @return
     */
    public ILifecycleStep verifyOrBootstrapInstanceConfigurationModel() {
	return new SimpleLifecycleStep("Verify instance bootstrapped") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		try {
		    Stat existing = getZookeeperManager().getCurator().checkExists().forPath(getInstanceZkPath());
		    if (existing == null) {
			getLogger().info("Zk node for instance not found. Creating...");
			getZookeeperManager().getCurator().create().forPath(getInstanceZkPath());
			getLogger().info("Created instance Zk node.");
		    } else {
			getLogger().info("Found Zk node for instance.");
		    }
		    verifyOrBootstrapInstanceConfiguration();
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
    public void verifyOrBootstrapInstanceConfiguration() throws SiteWhereException {
	try {
	    // Verify that instance state path exists.
	    Stat existing = getZookeeperManager().getCurator().checkExists().forPath(getInstanceStatePath());
	    if (existing == null) {
		getLogger().info("Instance state path '" + getInstanceStatePath() + "' not found. Creating...");
		getZookeeperManager().getCurator().create().forPath(getInstanceStatePath());
	    }

	    // Check for existing configuration bootstrap marker.
	    existing = getZookeeperManager().getCurator().checkExists().forPath(getInstanceConfigBootstrappedMarker());
	    if (existing == null) {
		getLogger().info("Configuration bootstrap marker node '" + getInstanceConfigBootstrappedMarker()
			+ "' not found. Bootstrapping configuration...");
		bootstrapInstanceConfiguration();
		getLogger().info("Bootstrapped instance configuration from template.");
	    } else {
		getLogger().info("Found configuration bootstrap marker node. Skipping configuration bootstrap.");
	    }
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    /**
     * Bootstrap instance configuration data from chosen instance template and
     * create root paths for configuring users and tenants.
     * 
     * @throws SiteWhereException
     */
    protected void bootstrapInstanceConfiguration() throws SiteWhereException {
	try {
	    getLogger().info("Copying instance template contents to Zookeeper...");
	    getInstanceTemplateManager().copyTemplateContentsToZk(getInstanceSettings().getInstanceTemplateId(),
		    getZookeeperManager().getCurator(), getInstanceZkPath());

	    // Create root path for configuring users.
	    createUsersConfigurationRootIfNotFound(getZookeeperManager().getCurator());

	    // Create root path for configuring tenants.
	    createTenantsConfigurationRootIfNotFound(getZookeeperManager().getCurator());

	    getLogger().info("Marking instance configuration as bootstrapped.");
	    getZookeeperManager().getCurator().create().forPath(getInstanceConfigBootstrappedMarker());
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    /**
     * Verify that instance users configuration node has been created.
     * 
     * @param curator
     * @throws Exception
     */
    protected void createUsersConfigurationRootIfNotFound(CuratorFramework curator) throws Exception {
	Stat existing = curator.checkExists().forPath(getInstanceUsersConfigurationPath());
	if (existing == null) {
	    getLogger().info("Zk node for user configurations not found. Creating...");
	    curator.create().forPath(getInstanceUsersConfigurationPath());
	    getLogger().info("Created user configurations Zk node.");
	} else {
	    getLogger().info("Found Zk node for user configurations.");
	}
    }

    /**
     * Verify that instance tenants configuration node has been created.
     * 
     * @param curator
     * @throws Exception
     */
    protected void createTenantsConfigurationRootIfNotFound(CuratorFramework curator) throws Exception {
	Stat existing = curator.checkExists().forPath(getInstanceTenantsConfigurationPath());
	if (existing == null) {
	    getLogger().info("Zk node for tenant configurations not found. Creating...");
	    curator.create().forPath(getInstanceTenantsConfigurationPath());
	    getLogger().info("Created tenant configurations Zk node.");
	} else {
	    getLogger().info("Found Zk node for tenant configurations.");
	}
    }

    /**
     * Initialize user model based on scripts in instance template.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected ILifecycleStep initializeUserModelFromInstanceTemplate() throws SiteWhereException {
	return new SimpleLifecycleStep("Verify instance users configured") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		Authentication previous = SecurityContextHolder.getContext().getAuthentication();
		try {
		    Stat existing = getZookeeperManager().getCurator().checkExists()
			    .forPath(getInstanceUsersBootstrappedMarker());
		    if (existing != null) {
			getLogger().info("Found users bootstrap marker node. Skipping users bootstrap.");
			return;
		    }
		    getLogger().info("Users bootstrap marker node '" + getInstanceConfigBootstrappedMarker()
			    + "' not found. Bootstrapping user data...");

		    SecurityContextHolder.getContext().setAuthentication(getSystemUser().getAuthentication());
		    IInstanceTemplate template = getChosenInstanceTemplate();
		    getLogger().info("Initializing instance users from template '" + template.getName() + "'.");
		    String templatePath = getInstanceZkPath() + "/" + template.getId();
		    if (template.getInitializers() != null) {
			List<String> userScripts = template.getInitializers().getUserManagement();
			initializeUserModelFromInstanceTemplateScripts(templatePath, userScripts);
		    }
		    getZookeeperManager().getCurator().create().forPath(getInstanceUsersBootstrappedMarker());
		} catch (Exception e) {
		    throw new SiteWhereException(e);
		} finally {
		    SecurityContextHolder.getContext().setAuthentication(previous);
		}
	    }
	};
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
	for (String script : scripts) {
	    getInstanceScriptSynchronizer().add(script);
	}

	GroovyConfiguration groovy = new GroovyConfiguration(getInstanceScriptSynchronizer());
	groovy.start(new LifecycleProgressMonitor(new LifecycleProgressContext(1, "Initialize user model."), this));
	for (String script : scripts) {
	    GroovyUserModelInitializer initializer = new GroovyUserModelInitializer(groovy, script);
	    initializer.initialize(getUserManagement());
	}
    }

    /**
     * Initialize tenant model based on scripts in instance template.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected ILifecycleStep initializeTenantModelFromInstanceTemplate() throws SiteWhereException {
	return new SimpleLifecycleStep("Verify instance tenants configured") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		Authentication previous = SecurityContextHolder.getContext().getAuthentication();
		try {
		    Stat existing = getZookeeperManager().getCurator().checkExists()
			    .forPath(getInstanceTenantsBootstrappedMarker());
		    if (existing != null) {
			getLogger().info("Found tenants bootstrap marker node. Skipping tenants bootstrap.");
			return;
		    }
		    getLogger().info("Tenants bootstrap marker node '" + getInstanceConfigBootstrappedMarker()
			    + "' not found. Bootstrapping tenant data...");

		    SecurityContextHolder.getContext().setAuthentication(getSystemUser().getAuthentication());
		    IInstanceTemplate template = getChosenInstanceTemplate();
		    getLogger().info("Initializing instance tenants from template '" + template.getName() + "'.");
		    String templatePath = getInstanceZkPath() + "/" + template.getId();
		    if (template.getInitializers() != null) {
			List<String> tenantScripts = template.getInitializers().getTenantManagement();
			initializeTenantModelFromInstanceTemplateScripts(templatePath, tenantScripts);
		    }
		    getZookeeperManager().getCurator().create().forPath(getInstanceTenantsBootstrappedMarker());
		} catch (Exception e) {
		    throw new SiteWhereException(e);
		} finally {
		    SecurityContextHolder.getContext().setAuthentication(previous);
		}
	    }
	};
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
	for (String script : scripts) {
	    getInstanceScriptSynchronizer().add(script);
	}

	GroovyConfiguration groovy = new GroovyConfiguration(getInstanceScriptSynchronizer());
	groovy.start(new LifecycleProgressMonitor(new LifecycleProgressContext(1, "Initialize tenant model."), this));
	for (String script : scripts) {
	    GroovyTenantModelInitializer initializer = new GroovyTenantModelInitializer(groovy, script);
	    initializer.initialize(getTenantManagement());
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
     * @see com.sitewhere.spi.tenant.ITenantAdministration#getTenantTemplates()
     */
    @Override
    public List<ITenantTemplate> getTenantTemplates() throws SiteWhereException {
	return getTenantConfigurationTemplateManager().getTenantTemplates();
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenantAdministration#getDatasetTemplates()
     */
    @Override
    public List<IDatasetTemplate> getDatasetTemplates() throws SiteWhereException {
	return getTenantDatasetTemplateManager().getDatasetTemplates();
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
     * @see com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice#
     * getInstanceScriptSynchronizer()
     */
    @Override
    public IScriptSynchronizer getInstanceScriptSynchronizer() {
	return instanceScriptSynchronizer;
    }

    public void setInstanceScriptSynchronizer(IScriptSynchronizer instanceScriptSynchronizer) {
	this.instanceScriptSynchronizer = instanceScriptSynchronizer;
    }

    /*
     * @see com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice#
     * getUserManagementGrpcServer()
     */
    @Override
    public IUserManagementGrpcServer getUserManagementGrpcServer() {
	return userManagementGrpcServer;
    }

    public void setUserManagementGrpcServer(IUserManagementGrpcServer userManagementGrpcServer) {
	this.userManagementGrpcServer = userManagementGrpcServer;
    }

    /*
     * @see com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice#
     * getUserManagement()
     */
    @Override
    public IUserManagement getUserManagement() {
	return userManagement;
    }

    public void setUserManagement(IUserManagement userManagement) {
	this.userManagement = userManagement;
    }

    /*
     * @see com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice#
     * getTenantManagementGrpcServer()
     */
    @Override
    public ITenantManagementGrpcServer getTenantManagementGrpcServer() {
	return tenantManagementGrpcServer;
    }

    public void setTenantManagementGrpcServer(ITenantManagementGrpcServer tenantManagementGrpcServer) {
	this.tenantManagementGrpcServer = tenantManagementGrpcServer;
    }

    /*
     * @see com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice#
     * getTenantManagement()
     */
    @Override
    public ITenantManagement getTenantManagement() {
	return tenantManagement;
    }

    public void setTenantManagement(ITenantManagement tenantManagement) {
	this.tenantManagement = tenantManagement;
    }

    /*
     * @see com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice#
     * getTenantConfigurationTemplateManager()
     */
    @Override
    public ITenantTemplateManager getTenantConfigurationTemplateManager() {
	return tenantConfigurationTemplateManager;
    }

    public void setTenantConfigurationTemplateManager(ITenantTemplateManager tenantConfigurationTemplateManager) {
	this.tenantConfigurationTemplateManager = tenantConfigurationTemplateManager;
    }

    /*
     * @see com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice#
     * getTenantDatasetTemplateManager()
     */
    @Override
    public IDatasetTemplateManager getTenantDatasetTemplateManager() {
	return tenantDatasetTemplateManager;
    }

    public void setTenantDatasetTemplateManager(IDatasetTemplateManager tenantDatasetTemplateManager) {
	this.tenantDatasetTemplateManager = tenantDatasetTemplateManager;
    }

    /*
     * @see com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice#
     * getTenantModelProducer()
     */
    @Override
    public ITenantModelProducer getTenantModelProducer() {
	return tenantModelProducer;
    }

    public void setTenantModelProducer(ITenantModelProducer tenantModelProducer) {
	this.tenantModelProducer = tenantModelProducer;
    }

    /*
     * @see com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice#
     * getTenantBootstrapModelConsumer()
     */
    @Override
    public ITenantBootstrapModelConsumer getTenantBootstrapModelConsumer() {
	return tenantBootstrapModelConsumer;
    }

    public void setTenantBootstrapModelConsumer(ITenantBootstrapModelConsumer tenantBootstrapModelConsumer) {
	this.tenantBootstrapModelConsumer = tenantBootstrapModelConsumer;
    }
}