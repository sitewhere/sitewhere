/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.instance.microservice;

import com.sitewhere.instance.spi.microservice.IInstanceBootstrapper;
import com.sitewhere.microservice.api.user.UserManagementRequestBuilder;
import com.sitewhere.microservice.instance.InstanceStatusUpdateOperation;
import com.sitewhere.microservice.lifecycle.AsyncStartLifecycleComponent;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.microservice.lifecycle.SimpleLifecycleStep;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.microservice.scripting.ScriptingUtils;
import com.sitewhere.microservice.security.SiteWhereAuthentication;
import com.sitewhere.microservice.security.UserContext;
import com.sitewhere.microservice.tenant.TenantManagementRequestBuilder;
import com.sitewhere.rest.model.user.request.UserCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.lifecycle.IAsyncStartLifecycleComponent;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;
import com.sitewhere.spi.microservice.user.IUserManagement;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.spi.user.request.IUserCreateRequest;

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

    public InstanceBootstrapper() {
	super(LifecycleComponentType.Other);
    }

    /*
     * @see com.sitewhere.spi.microservice.lifecycle.IAsyncStartLifecycleComponent#
     * asyncStart()
     */
    @Override
    public void asyncStart() throws SiteWhereException {
	ILifecycleProgressMonitor monitor = LifecycleProgressMonitor.createFor("Start", getMicroservice());
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start");

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
	SiteWhereInstance instance = getMicroservice().loadInstanceResource();

	// Load template and bootstrap datasets.
	InstanceDatasetTemplate template = getMicroservice().loadInstanceDatasetTemplate(instance);

	// Bootstrap users if not already bootstrapped.
	if (instance.getStatus().getUserManagementBootstrapState() == null) {
	    setUserBootstrapState(BootstrapState.NotBootstrapped);
	}
	if (instance.getStatus().getUserManagementBootstrapState() != BootstrapState.Bootstrapped) {
	    bootstrapUsers(instance, template);
	} else {
	    getLogger().info("Instance users already bootstrapped.");
	}

	// Bootstrap tenants if not already bootstrapped.
	if (instance.getStatus().getTenantManagementBootstrapState() == null) {
	    setTenantBootstrapState(BootstrapState.NotBootstrapped);
	}
	if (instance.getStatus().getTenantManagementBootstrapState() != BootstrapState.Bootstrapped) {
	    bootstrapTenants(instance, template);
	} else {
	    getLogger().info("Instance tenants already bootstrapped.");
	}
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
	SiteWhereAuthentication previous = UserContext.getCurrentUser();
	UserContext.setContext(getMicroservice().getSystemUser().getAuthentication());
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

		Binding binding = new Binding();
		binding.setVariable(IScriptVariables.VAR_LOGGER, getLogger());
		binding.setVariable(IScriptVariables.VAR_TENANT_MANAGEMENT_BUILDER,
			new TenantManagementRequestBuilder(getTenantManagement()));
		ScriptingUtils.run(tenantManagement, binding);

		getLogger().info(String.format("Completed execution of tenant management template '%s'.",
			template.getMetadata().getName()));
	    }
	    setTenantBootstrapState(BootstrapState.Bootstrapped);
	} catch (Throwable t) {
	    setTenantBootstrapState(BootstrapState.BootstrapFailed);
	    throw t;
	} finally {
	    UserContext.setContext(previous);
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
     * Bootstrap system user used for authentication between microservices for
     * internal tasks.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IUser bootstrapSystemUser() throws SiteWhereException {
	IInstanceSettings settings = getMicroservice().getInstanceSettings();
	IUser system = getUserManagement().getUserByUsername(settings.getKeycloakSystemUsername());
	if (system == null) {
	    IUserCreateRequest request = new UserCreateRequest.Builder(settings.getKeycloakSystemUsername(),
		    settings.getKeycloakSystemPassword(), "System", "User").withRole(SiteWhereRoles.SystemAdministrator)
			    .build();
	    system = getUserManagement().createUser(request);
	}
	return system;
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

		Binding binding = new Binding();
		binding.setVariable(IScriptVariables.VAR_LOGGER, getLogger());
		binding.setVariable("userBuilder", new UserManagementRequestBuilder(getUserManagement()));
		ScriptingUtils.run(userManagement, binding);

		getLogger().info(String.format("Completed execution of user management template '%s'.",
			template.getMetadata().getName()));
	    }

	    // Bootstrap system user.
	    bootstrapSystemUser();

	    // Indicate user management is bootstrapped.
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
	    public void update(SiteWhereInstanceStatus current) throws SiteWhereException {
		getLogger().info(String.format("Set tenant management bootstrap status to `%s`.", state.name()));
		current.setTenantManagementBootstrapState(state);
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
	    public void update(SiteWhereInstanceStatus current) throws SiteWhereException {
		getLogger().info(String.format("Set user management bootstrap status to `%s`.", state.name()));
		current.setUserManagementBootstrapState(state);
	    }
	});
    }

    protected ITenantManagement getTenantManagement() {
	return ((InstanceManagementMicroservice) getMicroservice()).getTenantManagement();
    }

    protected IUserManagement getUserManagement() {
	return ((InstanceManagementMicroservice) getMicroservice()).getUserManagement();
    }
}
