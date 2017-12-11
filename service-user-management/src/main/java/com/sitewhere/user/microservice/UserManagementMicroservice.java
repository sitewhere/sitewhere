/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.user.microservice;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.sitewhere.microservice.GlobalMicroservice;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroserviceIdentifiers;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.spring.UserManagementBeans;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.user.configuration.UserManagementModelProvider;
import com.sitewhere.user.grpc.UserManagementGrpcServer;
import com.sitewhere.user.spi.grpc.IUserManagementGrpcServer;
import com.sitewhere.user.spi.microservice.IUserManagementMicroservice;

/**
 * Microservice that provides user management functionality.
 * 
 * @author Derek
 */
public class UserManagementMicroservice extends GlobalMicroservice implements IUserManagementMicroservice {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice name */
    private static final String NAME = "User Management";

    /** Configuration model */
    private IConfigurationModel configurationModel = new UserManagementModelProvider().buildModel();

    /** User management configuration file name */
    private static final String USER_MANAGEMENT_CONFIGURATION = IMicroserviceIdentifiers.USER_MANAGEMENT + ".xml";

    /** List of configuration paths required by microservice */
    private static final String[] CONFIGURATION_PATHS = { USER_MANAGEMENT_CONFIGURATION };

    /** Responds to user management GRPC requests */
    private IUserManagementGrpcServer userManagementGrpcServer;

    /** User management persistence API */
    private IUserManagement userManagement;

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
	return IMicroserviceIdentifiers.USER_MANAGEMENT;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getConfigurationModel()
     */
    @Override
    public IConfigurationModel getConfigurationModel() {
	return configurationModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IGlobalMicroservice#getConfigurationPaths( )
     */
    @Override
    public String[] getConfigurationPaths() throws SiteWhereException {
	return CONFIGURATION_PATHS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IGlobalMicroservice#
     * initializeFromSpringContexts(org.springframework.context. ApplicationContext,
     * java.util.Map)
     */
    @Override
    public void initializeFromSpringContexts(ApplicationContext global, Map<String, ApplicationContext> contexts)
	    throws SiteWhereException {
	ApplicationContext context = contexts.get(USER_MANAGEMENT_CONFIGURATION);
	this.userManagement = (IUserManagement) context.getBean(UserManagementBeans.BEAN_USER_MANAGEMENT);
	this.userManagementGrpcServer = new UserManagementGrpcServer(this, getUserManagement());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IGlobalMicroservice#microserviceInitialize
     * (com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for initializing microservice.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize discoverable lifecycle components.
	init.addStep(initializeDiscoverableBeans(getUserManagementApplicationContext(), monitor));

	// Initialize user management implementation.
	init.addInitializeStep(this, getUserManagement(), true);

	// Initialize user management GRPC server.
	init.addInitializeStep(this, getUserManagementGrpcServer(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IGlobalMicroservice#microserviceStart(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for starting microservice.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getName());

	// Start discoverable lifecycle components.
	start.addStep(startDiscoverableBeans(getUserManagementApplicationContext(), monitor));

	// Start user mangement persistence.
	start.addStartStep(this, getUserManagement(), true);

	// Start GRPC server.
	start.addStartStep(this, getUserManagementGrpcServer(), true);

	// Execute initialization steps.
	start.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IGlobalMicroservice#microserviceStop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for stopping microservice.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop GRPC server.
	stop.addStopStep(this, getUserManagementGrpcServer());

	// Stop user management persistence.
	stop.addStopStep(this, getUserManagement());

	// Stop discoverable lifecycle components.
	stop.addStep(stopDiscoverableBeans(getUserManagementApplicationContext(), monitor));

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.user.spi.microservice.IUserManagementMicroservice#
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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.user.spi.microservice.IUserManagementMicroservice#
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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    protected ApplicationContext getUserManagementApplicationContext() {
	return getGlobalContexts().get(USER_MANAGEMENT_CONFIGURATION);
    }
}