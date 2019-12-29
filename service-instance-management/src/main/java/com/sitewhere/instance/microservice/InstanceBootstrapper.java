/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.microservice;

import com.sitewhere.instance.spi.microservice.IInstanceBootstrapper;
import com.sitewhere.instance.tenant.ScriptedTenantModelInitializer;
import com.sitewhere.instance.user.ScriptedUserModelInitializer;
import com.sitewhere.microservice.api.user.IUserManagement;
import com.sitewhere.microservice.instance.InstanceStatusUpdateOperation;
import com.sitewhere.microservice.lifecycle.AsyncStartLifecycleComponent;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.microservice.lifecycle.SimpleLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.IAsyncStartLifecycleComponent;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;

import io.sitewhere.k8s.crd.common.BootstrapState;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.instance.SiteWhereInstanceStatus;
import io.sitewhere.k8s.crd.instance.dataset.InstanceDatasetTemplate;

/**
 * Checks whether an instance is bootstrapped with initial data and, if not,
 * handles the bootstrap process.
 */
public class InstanceBootstrapper extends AsyncStartLifecycleComponent implements IInstanceBootstrapper {

    /** Functional area for tenant management */
    public static final String FA_TENANT_MANGEMENT = "tenantManagement";

    /** Functional area for user management */
    public static final String FA_USER_MANGEMENT = "userManagement";

    /** Tenant model initializer */
    private ScriptedTenantModelInitializer tenantModelInitializer = new ScriptedTenantModelInitializer();

    /** User model initializer */
    private ScriptedUserModelInitializer userModelInitializer = new ScriptedUserModelInitializer();

    public InstanceBootstrapper() {
	super(LifecycleComponentType.Other);
    }

    /*
     * @see com.sitewhere.microservice.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);
	ICompositeLifecycleStep initialize = new CompositeLifecycleStep("Initialize");

	// Initialize tenant model initializer.
	initialize.addInitializeStep(this, getTenantModelInitializer(), true);

	// Initialize user model initializer.
	initialize.addInitializeStep(this, getUserModelInitializer(), true);

	// Execute initialization steps.
	initialize.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.lifecycle.IAsyncStartLifecycleComponent#
     * asyncStart()
     */
    @Override
    public void asyncStart() throws SiteWhereException {
	ILifecycleProgressMonitor monitor = LifecycleProgressMonitor.createFor("Start", getMicroservice());
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start");

	// Start tenant model initializer.
	start.addStartStep(this, getTenantModelInitializer(), true);

	// Start user model initializer.
	start.addStartStep(this, getUserModelInitializer(), true);

	// Execute bootstrap logic.
	start.addStep(new SimpleLifecycleStep("Bootstrap") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		bootstrap();
	    }
	});

	// Execute start steps.
	start.execute(monitor);
    }

    /**
     * Create default status if missing from resource.
     * 
     * @param instance
     * @throws SiteWhereException
     */
    protected void createStatusIfMissing(SiteWhereInstance instance) throws SiteWhereException {
	if (instance.getStatus() == null) {
	    getMicroservice().executeInstanceStatusUpdate(new InstanceStatusUpdateOperation() {

		@Override
		public void update(SiteWhereInstance current) throws SiteWhereException {
		    getLogger().info("Creating default instance status since none was found.");
		    current.setStatus(new SiteWhereInstanceStatus());
		    current.getStatus().setUserManagementBootstrapState(BootstrapState.NotBootstrapped);
		    current.getStatus().setTenantManagementBootstrapState(BootstrapState.NotBootstrapped);
		}
	    });
	}
    }

    /**
     * Bootstrap instance.
     * 
     * @throws SiteWhereException
     */
    protected void bootstrap() throws SiteWhereException {
	// Load latest instance configuration from k8s.
	SiteWhereInstance instance = getMicroservice().loadInstanceResource();
	createStatusIfMissing(instance);

	// Load template and bootstrap datasets.
	InstanceDatasetTemplate template = getMicroservice().loadInstanceDatasetTemplate(instance);

	// Bootstrap tenants and users.
	bootstrapTenants(instance, template);
	bootstrapUsers(instance, template);
    }

    /**
     * Bootstrap tenants using initializer.
     * 
     * @param instance
     * @param template
     * @throws SiteWhereException
     */
    protected void bootstrapTenants(SiteWhereInstance instance, InstanceDatasetTemplate template)
	    throws SiteWhereException {
	switch (instance.getStatus().getTenantManagementBootstrapState()) {
	case BootstrapFailed: {
	    getLogger().warn("Skipping tenants bootstrap due to previous failure.");
	    break;
	}
	case Bootstrapped: {
	    getLogger().info("Tenants already bootstrapped.");
	    break;
	}
	case Bootstrapping: {
	    getLogger().info("Tenants already in bootstrapping state.");
	    break;
	}
	case NotBootstrapped: {
	    getLogger().info("Tenants not bootstrapped. Running initializer.");
	    runTenantManagementInitializer(template);
	    break;
	}
	}
    }

    /**
     * Bootstrap tenant management using script.
     * 
     * @param template
     * @throws SiteWhereException
     */
    protected void runTenantManagementInitializer(InstanceDatasetTemplate template) throws SiteWhereException {
	try {
	    setTenantBootstrapState(BootstrapState.Bootstrapping);
	    ITenantManagement tenants = getMicroservice().getTenantManagement();
	    if (tenants instanceof IAsyncStartLifecycleComponent) {
		getLogger().info("Waiting for tenant management to start before bootstrapping...");
		((IAsyncStartLifecycleComponent) tenants).waitForComponentStarted();
	    }

	    String tenantManagement = template.getSpec().getDatasets().get(FA_TENANT_MANGEMENT);
	    if (tenantManagement != null) {
		getLogger().info(String.format("Initializing tenant management from template '%s'.",
			template.getMetadata().getName()));
		getMicroservice().getScriptManager().addBootstrapScript(FA_TENANT_MANGEMENT, tenantManagement);
		getTenantModelInitializer().setScriptId(FA_TENANT_MANGEMENT);
		getTenantModelInitializer().initialize(tenants);
		getLogger().info(String.format("Completed execution of tenant management template '%s'.",
			template.getMetadata().getName()));
	    }
	    setTenantBootstrapState(BootstrapState.Bootstrapped);
	} catch (Throwable t) {
	    setTenantBootstrapState(BootstrapState.BootstrapFailed);
	    throw t;
	}
    }

    /**
     * Bootstrap users using initializer.
     * 
     * @param instance
     * @param template
     * @throws SiteWhereException
     */
    protected void bootstrapUsers(SiteWhereInstance instance, InstanceDatasetTemplate template)
	    throws SiteWhereException {
	switch (instance.getStatus().getUserManagementBootstrapState()) {
	case BootstrapFailed: {
	    getLogger().warn("Skipping users bootstrap due to previous failure.");
	    break;
	}
	case Bootstrapped: {
	    getLogger().info("Users already bootstrapped.");
	    break;
	}
	case Bootstrapping: {
	    getLogger().info("Users already in bootstrapping state.");
	    break;
	}
	case NotBootstrapped: {
	    getLogger().info("Users not bootstrapped. Running initializer.");
	    runUserManagementInitializer(template);
	    break;
	}
	}
    }

    /**
     * Bootstrap user management using script.
     * 
     * @param template
     * @throws SiteWhereException
     */
    protected void runUserManagementInitializer(InstanceDatasetTemplate template) throws SiteWhereException {
	try {
	    setUserBootstrapState(BootstrapState.Bootstrapping);
	    IUserManagement users = getUserManagement();
	    if (users instanceof IAsyncStartLifecycleComponent) {
		getLogger().info("Waiting for user management to start before bootstrapping...");
		((IAsyncStartLifecycleComponent) users).waitForComponentStarted();
	    }

	    String userManagement = template.getSpec().getDatasets().get(FA_USER_MANGEMENT);
	    if (userManagement != null) {
		getLogger().info(String.format("Initializing user management from template '%s'.",
			template.getMetadata().getName()));
		getMicroservice().getScriptManager().addBootstrapScript(FA_USER_MANGEMENT, userManagement);
		getUserModelInitializer().setScriptId(FA_USER_MANGEMENT);
		getUserModelInitializer().initialize(getUserManagement());
		getLogger().info(String.format("Completed execution of user management template '%s'.",
			template.getMetadata().getName()));
	    }
	    setUserBootstrapState(BootstrapState.Bootstrapped);
	} catch (Throwable t) {
	    setUserBootstrapState(BootstrapState.BootstrapFailed);
	    throw t;
	}
    }

    /**
     * Attempts to set the tenant bootstrap indicator. Handles cases where there is
     * contention for the instance configuration update.
     * 
     * @param state
     * @throws SiteWhereException
     */
    protected void setTenantBootstrapState(BootstrapState state) throws SiteWhereException {
	getMicroservice().executeInstanceStatusUpdate(new InstanceStatusUpdateOperation() {

	    @Override
	    public void update(SiteWhereInstance current) throws SiteWhereException {
		getLogger().info(String.format("Set tenant management bootstrap status to `%s`.", state.name()));
		current.getStatus().setTenantManagementBootstrapState(state);
	    }
	});
    }

    /**
     * Attempts to set the user bootstrap indicator. Handles cases where there is
     * contention for the instance configuration update.
     * 
     * @param state
     * @throws SiteWhereException
     */
    protected void setUserBootstrapState(BootstrapState state) throws SiteWhereException {
	getMicroservice().executeInstanceStatusUpdate(new InstanceStatusUpdateOperation() {

	    @Override
	    public void update(SiteWhereInstance current) throws SiteWhereException {
		getLogger().info(String.format("Set user management bootstrap status to `%s`.", state.name()));
		current.getStatus().setUserManagementBootstrapState(state);
	    }
	});
    }

    protected ScriptedTenantModelInitializer getTenantModelInitializer() {
	return tenantModelInitializer;
    }

    protected ScriptedUserModelInitializer getUserModelInitializer() {
	return userModelInitializer;
    }

    protected IUserManagement getUserManagement() {
	return ((InstanceManagementMicroservice) getMicroservice()).getUserManagement();
    }
}
