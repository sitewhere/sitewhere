/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.microservice;

import java.util.List;

import org.apache.zookeeper.data.Stat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.grpc.client.spi.client.ITenantManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IUserManagementApiChannel;
import com.sitewhere.grpc.client.tenant.CachedTenantManagementApiChannel;
import com.sitewhere.grpc.client.user.CachedUserManagementApiChannel;
import com.sitewhere.instance.configuration.InstanceManagementModelProvider;
import com.sitewhere.instance.initializer.GroovyTenantModelInitializer;
import com.sitewhere.instance.initializer.GroovyUserModelInitializer;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.instance.spi.templates.IInstanceTemplate;
import com.sitewhere.instance.spi.templates.IInstanceTemplateManager;
import com.sitewhere.instance.templates.InstanceTemplateManager;
import com.sitewhere.microservice.GlobalMicroservice;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.microservice.scripting.InstanceScriptSynchronizer;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.server.lifecycle.SimpleLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.scripting.IScriptSynchronizer;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;

/**
 * Microservice that provides instance management functionality.
 * 
 * @author Derek
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

    /** User management API channel */
    private IUserManagementApiChannel<?> userManagementApiChannel;

    /** Tenant management API channel */
    private ITenantManagementApiChannel<?> tenantManagementApiChannel;

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

	// Create GRPC components.
	createGrpcComponents();

	// Create step that will start components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize instance script synchronizer.
	init.addInitializeStep(this, getInstanceScriptSynchronizer(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /**
     * Create components that interact via GRPC.
     */
    protected void createGrpcComponents() {
	this.userManagementApiChannel = new CachedUserManagementApiChannel(getInstanceSettings(),
		new CachedUserManagementApiChannel.CacheSettings());
	this.tenantManagementApiChannel = new CachedTenantManagementApiChannel(getInstanceSettings(),
		new CachedTenantManagementApiChannel.CacheSettings());
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

	// Verify or create Zk node for instance information.
	start.addStep(verifyOrCreateInstanceNode());

	// Start instance template manager.
	start.addStartStep(this, getInstanceTemplateManager(), true);

	// Start instance script synchronizer.
	start.addStartStep(this, getInstanceScriptSynchronizer(), true);

	// Initialize user management API channel.
	start.addInitializeStep(this, getUserManagementApiChannel(), true);

	// Start user mangement API channel.
	start.addStartStep(this, getUserManagementApiChannel(), true);

	// Initialize tenant management API channel.
	start.addInitializeStep(this, getTenantManagementApiChannel(), true);

	// Start tenant mangement API channel.
	start.addStartStep(this, getTenantManagementApiChannel(), true);

	// Verify Zk node for instance configuration or bootstrap instance.
	start.addStep(verifyOrBootstrapConfiguration());

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

	// Stop tenant management API channel.
	stop.addStopStep(this, getTenantManagementApiChannel());

	// Stop user management API channel.
	stop.addStopStep(this, getUserManagementApiChannel());

	// Stop instance script synchronizer.
	stop.addStopStep(this, getInstanceScriptSynchronizer());

	// Stop instance template manager.
	stop.addStopStep(this, getInstanceTemplateManager());

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
    public ILifecycleStep verifyOrCreateInstanceNode() {
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
		    // Verify that instance state path exists.
		    Stat existing = getZookeeperManager().getCurator().checkExists().forPath(getInstanceStatePath());
		    if (existing == null) {
			getLogger().info("Instance state path '" + getInstanceStatePath() + "' not found. Creating...");
			getZookeeperManager().getCurator().create().forPath(getInstanceStatePath());
		    }

		    // Check for existing configuration bootstrap marker.
		    existing = getZookeeperManager().getCurator().checkExists()
			    .forPath(getInstanceConfigBootstrappedMarker());
		    if (existing == null) {
			getLogger().info("Configuration bootstrap marker node '" + getInstanceConfigBootstrappedMarker()
				+ "' not found. Bootstrapping configuration...");
			bootstrapInstanceConfiguration();
			getLogger().info("Bootstrapped instance configuration from template.");
		    } else {
			getLogger()
				.info("Found configuration bootstrap marker node. Skipping configuration bootstrap.");
		    }

		    // Check for existing data bootstrap marker.
		    existing = getZookeeperManager().getCurator().checkExists()
			    .forPath(getInstanceDataBootstrappedMarker());
		    if (existing == null) {
			getLogger().info("Data bootstrap marker node '" + getInstanceConfigBootstrappedMarker()
				+ "' not found. Bootstrapping instance data...");
			initializeModelFromInstanceTemplate();
			getLogger().info("Bootstrapped instance data from template.");
		    } else {
			getLogger().info("Found data bootstrap marker node. Skipping data bootstrap.");
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
	    getLogger().info("Copying instance template contents to Zookeeper...");
	    getInstanceTemplateManager().copyTemplateContentsToZk(getInstanceSettings().getInstanceTemplateId(),
		    getZookeeperManager().getCurator(), getInstanceZkPath());
	    getLogger().info("Marking instance configuration as bootstrapped.");
	    getZookeeperManager().getCurator().create().forPath(getInstanceConfigBootstrappedMarker());
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
	    getZookeeperManager().getCurator().create().forPath(getInstanceDataBootstrappedMarker());
	} catch (Exception e) {
	    throw new SiteWhereException(e);
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
	for (String script : scripts) {
	    getInstanceScriptSynchronizer().add(script);
	}

	GroovyConfiguration groovy = new GroovyConfiguration(getInstanceScriptSynchronizer());
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
	for (String script : scripts) {
	    getInstanceScriptSynchronizer().add(script);
	}

	GroovyConfiguration groovy = new GroovyConfiguration(getInstanceScriptSynchronizer());
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
     * @see com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice#
     * getTenantManagementApiChannel()
     */
    @Override
    public ITenantManagementApiChannel<?> getTenantManagementApiChannel() {
	return tenantManagementApiChannel;
    }

    public void setTenantManagementApiChannel(ITenantManagementApiChannel<?> tenantManagementApiChannel) {
	this.tenantManagementApiChannel = tenantManagementApiChannel;
    }
}