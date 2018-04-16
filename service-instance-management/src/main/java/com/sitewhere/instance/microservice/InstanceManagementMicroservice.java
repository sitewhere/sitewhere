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

import com.sitewhere.grpc.client.spi.client.ITenantManagementApiDemux;
import com.sitewhere.grpc.client.spi.client.IUserManagementApiDemux;
import com.sitewhere.grpc.client.tenant.TenantManagementApiDemux;
import com.sitewhere.grpc.client.user.UserManagementApiDemux;
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
public class InstanceManagementMicroservice extends GlobalMicroservice implements IInstanceManagementMicroservice {

    /** Microservice name */
    private static final String NAME = "Instance Management";

    /** User management configuration file name */
    private static final String CONFIGURATION_PATH = MicroserviceIdentifier.InstanceManagement.getPath() + ".xml";

    /** Instance template manager */
    private IInstanceTemplateManager instanceTemplateManager = new InstanceTemplateManager();

    /** Instance script synchronizer */
    private IScriptSynchronizer instanceScriptSynchronizer;

    /** User management API demux */
    private IUserManagementApiDemux userManagementApiDemux;

    /** Tenant management API demux */
    private ITenantManagementApiDemux tenantManagementApiDemux;

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
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
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
	this.userManagementApiDemux = new UserManagementApiDemux();
	this.tenantManagementApiDemux = new TenantManagementApiDemux();
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
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
	start.addInitializeStep(this, getUserManagementApiDemux(), true);

	// Start user mangement API channel.
	start.addStartStep(this, getUserManagementApiDemux(), true);

	// Initialize tenant management API channel.
	start.addInitializeStep(this, getTenantManagementApiDemux(), true);

	// Start tenant mangement API channel.
	start.addStartStep(this, getTenantManagementApiDemux(), true);

	// Verify Zk node for instance configuration or bootstrap instance.
	start.addStep(verifyOrBootstrapConfiguration());

	// Execute initialization steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * microserviceStop(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop tenant management API demux.
	stop.addStopStep(this, getTenantManagementApiDemux());

	// Stop user management API demux.
	stop.addStopStep(this, getUserManagementApiDemux());

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

		    // Check for existing instance bootstrap marker.
		    existing = getZookeeperManager().getCurator().checkExists()
			    .forPath(getInstanceBootstrappedMarker());
		    if (existing == null) {
			getLogger().info("Bootstrap marker node '" + getInstanceBootstrappedMarker()
				+ "' not found. Bootstrapping...");
			bootstrapInstanceConfiguration();
			getLogger().info("Bootstrapped instance configuration from template.");
		    } else {
			getLogger().info("Found bootstrap marker node. Skipping instance bootstrap.");
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
	for (String script : scripts) {
	    getInstanceScriptSynchronizer().add(script);
	}

	// Wait for user management APIs to become available.
	getUserManagementApiDemux().waitForApiChannel().waitForApiAvailable();
	getLogger().info("User management API detected as available.");

	GroovyConfiguration groovy = new GroovyConfiguration(getInstanceScriptSynchronizer());
	groovy.start(new LifecycleProgressMonitor(new LifecycleProgressContext(1, "Initialize user model."), this));
	for (String script : scripts) {
	    GroovyUserModelInitializer initializer = new GroovyUserModelInitializer(groovy, script);
	    initializer.initialize(getUserManagementApiDemux().getApiChannel());
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

	// Wait for tenant management APIs to become available.
	getTenantManagementApiDemux().waitForApiChannel().waitForApiAvailable();
	getLogger().info("Tenant management API detected as available.");

	GroovyConfiguration groovy = new GroovyConfiguration(getInstanceScriptSynchronizer());
	groovy.start(new LifecycleProgressMonitor(new LifecycleProgressContext(1, "Initialize tenant model."), this));
	for (String script : scripts) {
	    GroovyTenantModelInitializer initializer = new GroovyTenantModelInitializer(groovy, script);
	    initializer.initialize(getTenantManagementApiDemux().getApiChannel());
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
     * getUserManagementApiDemux()
     */
    @Override
    public IUserManagementApiDemux getUserManagementApiDemux() {
	return userManagementApiDemux;
    }

    public void setUserManagementApiDemux(IUserManagementApiDemux userManagementApiDemux) {
	this.userManagementApiDemux = userManagementApiDemux;
    }

    /*
     * @see com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice#
     * getTenantManagementApiDemux()
     */
    @Override
    public ITenantManagementApiDemux getTenantManagementApiDemux() {
	return tenantManagementApiDemux;
    }

    public void setTenantManagementApiDemux(ITenantManagementApiDemux tenantManagementApiDemux) {
	this.tenantManagementApiDemux = tenantManagementApiDemux;
    }
}