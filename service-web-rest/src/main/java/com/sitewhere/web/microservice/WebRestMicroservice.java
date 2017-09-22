package com.sitewhere.web.microservice;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.sitewhere.grpc.model.client.UserManagementApiChannel;
import com.sitewhere.grpc.model.client.UserManagementGrpcChannel;
import com.sitewhere.microservice.GlobalMicroservice;
import com.sitewhere.microservice.MicroserviceEnvironment;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.InitializeComponentLifecycleStep;
import com.sitewhere.server.lifecycle.StartComponentLifecycleStep;
import com.sitewhere.server.lifecycle.StopComponentLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.web.spi.microservice.IWebRestMicroservice;

/**
 * Microservice that provides web/REST functionality.
 * 
 * @author Derek
 */
public class WebRestMicroservice extends GlobalMicroservice implements IWebRestMicroservice {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice name */
    private static final String NAME = "Web/REST";

    /** Web/REST configuration file name */
    private static final String WEB_REST_CONFIGURATION = "web-rest.xml";

    /** List of configuration paths required by microservice */
    private static final String[] CONFIGURATION_PATHS = { WEB_REST_CONFIGURATION };

    /** User management GRPC channel */
    private UserManagementGrpcChannel userManagementGrpcChannel;

    /** User management implementation */
    private IUserManagement userManagement;

    public WebRestMicroservice() {
	createGrpcComponents();
    }

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
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.Microservice#afterMicroserviceStarted()
     */
    @Override
    public void afterMicroserviceStarted() {
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
	initializeDiscoverableBeans(getWebRestApplicationContext(), monitor);

	// Initialize user management GRPC channel.
	init.addStep(new InitializeComponentLifecycleStep(this, getUserManagementGrpcChannel(),
		"User management GRPC channel", "Unable to initialize user management GRPC channel", true));

	// Execute initialization steps.
	init.execute(monitor);
    }

    /**
     * Create components that interact via GRPC.
     */
    protected void createGrpcComponents() {
	this.userManagementGrpcChannel = new UserManagementGrpcChannel(MicroserviceEnvironment.HOST_USER_MANAGEMENT,
		MicroserviceEnvironment.DEFAULT_GRPC_PORT);
	this.userManagement = new UserManagementApiChannel(getUserManagementGrpcChannel());
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
	startDiscoverableBeans(getWebRestApplicationContext(), monitor);

	// Start user mangement GRPC channel.
	start.addStep(new StartComponentLifecycleStep(this, getUserManagementGrpcChannel(),
		"User management GRPC channel", "Unable to start user management GRPC channel.", true));

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

	// Stop user mangement GRPC channel.
	stop.addStep(
		new StopComponentLifecycleStep(this, getUserManagementGrpcChannel(), "User management GRPC channel"));

	// Stop discoverable lifecycle components.
	stopDiscoverableBeans(getWebRestApplicationContext(), monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.web.spi.microservice.IWebRestMicroservice#
     * getUserManagementGrpcChannel()
     */
    @Override
    public UserManagementGrpcChannel getUserManagementGrpcChannel() {
	return userManagementGrpcChannel;
    }

    public void setUserManagementGrpcChannel(UserManagementGrpcChannel userManagementGrpcChannel) {
	this.userManagementGrpcChannel = userManagementGrpcChannel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.web.spi.microservice.IWebRestMicroservice#getUserManagement
     * ()
     */
    @Override
    public IUserManagement getUserManagement() {
	return userManagement;
    }

    public void setUserManagement(IUserManagement userManagement) {
	this.userManagement = userManagement;
    }

    protected ApplicationContext getWebRestApplicationContext() {
	return getGlobalContexts().get(WEB_REST_CONFIGURATION);
    }
}