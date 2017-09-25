package com.sitewhere.user.microservice;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.sitewhere.microservice.GlobalMicroservice;
import com.sitewhere.microservice.spi.spring.UserManagementBeans;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.InitializeComponentLifecycleStep;
import com.sitewhere.server.lifecycle.StartComponentLifecycleStep;
import com.sitewhere.server.lifecycle.StopComponentLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.user.IUserManagement;
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

    /** User management configuration file name */
    private static final String USER_MANAGEMENT_CONFIGURATION = "user-management.xml";

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
     * @see
     * com.sitewhere.microservice.spi.IGlobalMicroservice#getConfigurationPaths(
     * )
     */
    @Override
    public String[] getConfigurationPaths() throws SiteWhereException {
	return CONFIGURATION_PATHS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IGlobalMicroservice#
     * initializeFromSpringContexts(org.springframework.context.
     * ApplicationContext, java.util.Map)
     */
    @Override
    public void initializeFromSpringContexts(ApplicationContext global, Map<String, ApplicationContext> contexts)
	    throws SiteWhereException {
	ApplicationContext context = contexts.get(USER_MANAGEMENT_CONFIGURATION);
	this.userManagement = (IUserManagement) context.getBean(UserManagementBeans.BEAN_USER_MANAGEMENT);
	this.userManagementGrpcServer = new UserManagementGrpcServer(getUserManagement());
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
	init.addStep(new InitializeComponentLifecycleStep(this, getUserManagement(), "User management persistence",
		"Unable to initialize user management persistence", true));

	// Initialize user management GRPC server.
	init.addStep(new InitializeComponentLifecycleStep(this, getUserManagementGrpcServer(),
		"User management GRPC server", "Unable to initialize user management GRPC server", true));

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
	start.addStep(new StartComponentLifecycleStep(this, getUserManagement(), "User management persistence",
		"Unable to start user management persistence.", true));

	// Start GRPC server.
	start.addStep(new StartComponentLifecycleStep(this, getUserManagementGrpcServer(),
		"User management GRPC server", "Unable to start user management GRPC server.", true));

	// Execute initialization steps.
	start.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IGlobalMicroservice#microserviceStop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for stopping microservice.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop GRPC server.
	stop.addStep(
		new StopComponentLifecycleStep(this, getUserManagementGrpcServer(), "User management GRPC server"));

	// Stop user management persistence.
	stop.addStep(new StopComponentLifecycleStep(this, getUserManagement(), "User management persistence"));

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