package com.sitewhere.microservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.microservice.configuration.ZookeeperManager;
import com.sitewhere.microservice.security.TokenManagement;
import com.sitewhere.microservice.spi.IMicroservice;
import com.sitewhere.microservice.spi.configuration.IZookeeperManager;
import com.sitewhere.microservice.spi.security.ITokenManagement;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.InitializeComponentLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Common base class for all SiteWhere microservices.
 * 
 * @author Derek
 */
public abstract class Microservice extends LifecycleComponent implements IMicroservice {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Default instance id value */
    private static final String DEFAULT_INSTANCE_ID = "default";

    /** Instance configuration folder name */
    private static final String INSTANCE_CONFIGURATION_FOLDER = "/conf";

    /** Relative path to instance bootstrap marker */
    private static final String INSTANCE_BOOTSTRAP_MARKER = "/bootstrapped";

    /** Zookeeper manager */
    private IZookeeperManager zookeeperManager = new ZookeeperManager();

    /** JWT token management */
    private ITokenManagement tokenManagement = new TokenManagement();

    /** Instance id service belongs to */
    private String instanceId = DEFAULT_INSTANCE_ID;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Load instance id from environment if available.
	checkEnvForInstanceId();

	// Organizes steps for initializing microservice.
	ICompositeLifecycleStep initialize = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize Zookeeper configuration management.
	initialize.addStep(new InitializeComponentLifecycleStep(this, getZookeeperManager(), "Zookeeper Manager",
		"Unable to initialize Zookeeper Manager", true));

	// Execute initialization steps.
	initialize.execute(monitor);
    }

    /**
     * Check environment variable for SiteWhere instance id.
     */
    protected void checkEnvForInstanceId() {
	String envInstanceId = System.getenv().get(MicroserviceEnvironment.ENV_INSTANCE_ID);
	if (envInstanceId != null) {
	    setInstanceId(envInstanceId);
	    LOGGER.info("SiteWhere instance id loaded from " + MicroserviceEnvironment.ENV_INSTANCE_ID + ": "
		    + envInstanceId);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroservice#afterMicroserviceStarted()
     */
    @Override
    public void afterMicroserviceStarted() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#terminate(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void terminate(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Terminate Zk manager.
	getZookeeperManager().lifecycleTerminate(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#
     * waitForInstanceInitialization()
     */
    @Override
    public void waitForInstanceInitialization() throws SiteWhereException {
	try {
	    LOGGER.info("Verifying that instance has been bootstrapped...");
	    while (true) {
		if (getZookeeperManager().getCurator().checkExists().forPath(getInstanceBootstrappedMarker()) != null) {
		    break;
		}
		Thread.sleep(1000);
	    }
	    LOGGER.info("Confirmed that instance was bootstrapped.");
	} catch (Exception e) {
	    throw new SiteWhereException("Error waiting on instance to be bootstrapped.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#getInstanceZkPath()
     */
    @Override
    public String getInstanceZkPath() {
	return "/" + getInstanceId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroservice#getInstanceConfigurationPath
     * ()
     */
    @Override
    public String getInstanceConfigurationPath() {
	return getInstanceZkPath() + INSTANCE_CONFIGURATION_FOLDER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#
     * getInstanceBootstrappedMarker()
     */
    @Override
    public String getInstanceBootstrappedMarker() throws SiteWhereException {
	return getInstanceConfigurationPath() + INSTANCE_BOOTSTRAP_MARKER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#getZookeeperManager()
     */
    @Override
    public IZookeeperManager getZookeeperManager() {
	return zookeeperManager;
    }

    public void setZookeeperManager(IZookeeperManager zookeeperManager) {
	this.zookeeperManager = zookeeperManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#getTokenManagement()
     */
    @Override
    public ITokenManagement getTokenManagement() {
	return tokenManagement;
    }

    public void setTokenManagement(ITokenManagement tokenManagement) {
	this.tokenManagement = tokenManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#getInstanceId()
     */
    @Override
    public String getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(String instanceId) {
	this.instanceId = instanceId;
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
}