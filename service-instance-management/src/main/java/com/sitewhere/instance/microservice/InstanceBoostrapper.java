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
import com.sitewhere.microservice.exception.ConcurrentK8sUpdateException;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.lifecycle.LifecycleComponent;
import com.sitewhere.microservice.lifecycle.SimpleLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

import io.sitewhere.k8s.crd.common.BootstrapState;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.instance.SiteWhereInstanceStatus;
import io.sitewhere.k8s.crd.instance.dataset.InstanceDatasetTemplate;

/**
 * Checks whether an instance is bootstrapped with initial data and, if not,
 * handles the bootstrap process.
 */
public class InstanceBoostrapper extends LifecycleComponent implements IInstanceBootstrapper {

    /** Functional area for tenant management */
    public static final String FA_TENANT_MANGEMENT = "tenantManagement";

    /** Functional area for user management */
    public static final String FA_USER_MANGEMENT = "userManagement";

    /** Tenant model initializer */
    private ScriptedTenantModelInitializer tenantModelInitializer = new ScriptedTenantModelInitializer();

    /** User model initializer */
    private ScriptedUserModelInitializer userModelInitializer = new ScriptedUserModelInitializer();

    public InstanceBoostrapper() {
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
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#start(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
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
     * Bootstrap instance.
     * 
     * @throws SiteWhereException
     */
    protected void bootstrap() throws SiteWhereException {
	// Load latest instance configuration from k8s.
	SiteWhereInstance instance = getMicroservice().loadInstanceConfiguration();
	instance = createStatusIfMissing(instance);

	switch (instance.getStatus().getBootstrapState()) {
	case BootstrapFailed: {
	    getLogger().warn("Skipping instance bootstrap due to previous failure.");
	    return;
	}
	case Bootstrapped: {
	    getLogger().info("Instance already bootstrapped.");
	    return;
	}
	case Bootstrapping: {
	    getLogger().info("Instance already in bootstrapping state.");
	    return;
	}
	case NotBootstrapped: {
	    break;
	}
	}

	// Mark instance as bootstrapping.
	instance = setBootstrappingStatus(BootstrapState.Bootstrapping);

	// Load template and bootstrap datasets.
	InstanceDatasetTemplate template = getMicroservice().loadInstanceDatasetTemplate(instance);

	try {
	    bootstrapTenantManagement(template);
	    bootstrapUserManagement(template);

	    // Mark instance as bootstrapped.
	    instance = setBootstrappingStatus(BootstrapState.Bootstrapped);
	} catch (SiteWhereException e) {
	    // Indicate that bootstrap failed.
	    instance = setBootstrappingStatus(BootstrapState.BootstrapFailed);
	    throw e;
	}
    }

    /**
     * Create default status if missing from resource.
     * 
     * @param instance
     * @return
     */
    protected SiteWhereInstance createStatusIfMissing(SiteWhereInstance instance) {
	if (instance.getStatus() == null) {
	    instance.setStatus(new SiteWhereInstanceStatus());
	}
	instance.getStatus().setBootstrapState(BootstrapState.NotBootstrapped);
	return instance;
    }

    /**
     * Bootstrap tenant management using Groovy script.
     * 
     * @param template
     * @throws SiteWhereException
     */
    protected void bootstrapTenantManagement(InstanceDatasetTemplate template) throws SiteWhereException {
	String tenantManagement = template.getSpec().getDatasets().get(FA_TENANT_MANGEMENT);
	if (tenantManagement != null) {
	    getLogger().info(String.format("Initializing tenant management from template '%s'.",
		    template.getMetadata().getName()));
	    getMicroservice().getScriptManager().addBootstrapScript(FA_TENANT_MANGEMENT, tenantManagement);
	    getTenantModelInitializer().setScriptId(FA_TENANT_MANGEMENT);
	    getTenantModelInitializer().initialize(getMicroservice().getTenantManagement());
	    getLogger().info(String.format("Completed execution of tenant management template '%s'.",
		    template.getMetadata().getName()));
	}
    }

    /**
     * Bootstrap user management using Groovy script.
     * 
     * @param template
     * @throws SiteWhereException
     */
    protected void bootstrapUserManagement(InstanceDatasetTemplate template) throws SiteWhereException {
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
    }

    /**
     * Attempts to set the boostrapping indicator. Handles cases where there is
     * contention for the instance configuration update.
     * 
     * @param status
     * @return
     * @throws SiteWhereException
     */
    protected SiteWhereInstance setBootstrappingStatus(BootstrapState status) throws SiteWhereException {
	while (true) {
	    try {
		SiteWhereInstance instance = getMicroservice().loadInstanceConfiguration();
		instance = createStatusIfMissing(instance);
		instance.getStatus().setBootstrapState(status);
		instance = getMicroservice().updateInstanceConfiguration(instance);
		getLogger().info(String.format("Set instance bootstrap status to `%s`.", status.name()));
		return instance;
	    } catch (ConcurrentK8sUpdateException e) {
		getLogger().info("Instance configuration updated concurrently. Will retry.");
	    }
	    try {
		Thread.sleep(500);
	    } catch (InterruptedException e) {
		throw new SiteWhereException("Failed to lock instance for bootstrap due to interrupt.");
	    }
	}
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
