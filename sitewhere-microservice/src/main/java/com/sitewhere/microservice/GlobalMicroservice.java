package com.sitewhere.microservice;

import com.sitewhere.microservice.configuration.ConfigurableMicroservice;
import com.sitewhere.microservice.spi.IGlobalMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Microservice that serves a global function in a SiteWhere instance.
 * 
 * @author Derek
 */
public abstract class GlobalMicroservice extends ConfigurableMicroservice implements IGlobalMicroservice {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.Microservice#initialize(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Wait for validation from Zk that instance is initialized.
	waitOnInstanceInitialization();

	// Call logic for initializing microservice subclass.
	microserviceInitialize(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	// Call logic for starting microservice subclass.
	microserviceStart(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);

	// Call logic for stopping microservice subclass.
	microserviceStop(monitor);
    }
}