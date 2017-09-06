package com.sitewhere.microservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;

import com.sitewhere.microservice.configuration.ZookeeperConfigurationManager;
import com.sitewhere.microservice.spi.IMicroservice;
import com.sitewhere.microservice.spi.IZookeeperConfigurationManager;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.InitializeComponentLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Common base class for all SiteWhere microservices.
 * 
 * @author Derek
 */
public abstract class Microservice extends LifecycleComponent implements IMicroservice, DisposableBean {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Zookeeper configuration manager */
    private IZookeeperConfigurationManager zookeeperConfigurationManager = new ZookeeperConfigurationManager();

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Organizes steps for starting microservice.
	ICompositeLifecycleStep initialize = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize Zookeeper configuration management.
	initialize.addStep(new InitializeComponentLifecycleStep(this, getZookeeperConfigurationManager(),
		"Zookeeper Configuration Manager"));

	// Execute initialization steps.
	initialize.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
	LifecycleProgressMonitor termMonitor = new LifecycleProgressMonitor(
		new LifecycleProgressContext(1, "Terminate " + getName()));
	terminate(termMonitor);
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
	getZookeeperConfigurationManager().lifecycleTerminate(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#
     * getZookeeperConfigurationManager()
     */
    @Override
    public IZookeeperConfigurationManager getZookeeperConfigurationManager() {
	return zookeeperConfigurationManager;
    }

    public void setZookeeperConfigurationManager(IZookeeperConfigurationManager zookeeperConfigurationManager) {
	this.zookeeperConfigurationManager = zookeeperConfigurationManager;
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