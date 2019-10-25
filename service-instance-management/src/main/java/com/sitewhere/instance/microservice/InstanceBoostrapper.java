/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.microservice;

import com.sitewhere.instance.spi.microservice.IInstanceBootstrapper;
import com.sitewhere.microservice.exception.ConcurrentK8sUpdateException;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import io.sitewhere.k8s.crd.common.BootstrapState;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.instance.SiteWhereInstanceStatus;
import io.sitewhere.k8s.crd.instance.dataset.InstanceDatasetTemplate;

/**
 * Checks whether an instance is bootstrapped with initial data and, if not,
 * handles the bootstrap process.
 */
public class InstanceBoostrapper extends LifecycleComponent implements IInstanceBootstrapper {

    public InstanceBoostrapper() {
	super(LifecycleComponentType.Other);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Load latest instance configuration from k8s.
	SiteWhereInstance instance = getMicroservice().loadInstanceConfiguration();
	instance = createStatusIfMissing(instance);

	// Skip already bootstrapped/bootstrapping instance.
	if (instance.getStatus().getBootstrapState() != BootstrapState.NotBootstrapped) {
	    return;
	}

	// Mark instance as bootstrapping.
	instance = setBootstrappingStatus(BootstrapState.Bootstrapping);

	// Load template and bootstrap datasets.
	InstanceDatasetTemplate template = getMicroservice().loadInstanceDatasetTemplate(instance);
	bootstrapTenantManagement(template);
	bootstrapUserManagement(template);

	// Mark instance as bootstrapped.
	instance = setBootstrappingStatus(BootstrapState.Bootstrapped);
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
	String tenantManagement = template.getSpec().getDatasets().get("tenantManagement");
	if (tenantManagement != null) {
	    getLogger().info(String.format("Bootstrapping tenant management with:\n\n%s\n", tenantManagement));
	}
    }

    /**
     * Bootstrap user management using Groovy script.
     * 
     * @param template
     * @throws SiteWhereException
     */
    protected void bootstrapUserManagement(InstanceDatasetTemplate template) throws SiteWhereException {
	String userManagement = template.getSpec().getDatasets().get("userManagement");
	if (userManagement != null) {
	    getLogger().info(String.format("Bootstrapping user management with:\n\n%s\n", userManagement));
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
}
