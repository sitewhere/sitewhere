package com.sitewhere.microservice;

import com.sitewhere.microservice.spi.IGlobalMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Microservice that serves a global function in a SiteWhere instance.
 * 
 * @author Derek
 */
public abstract class GlobalMicroservice extends Microservice implements IGlobalMicroservice {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Wait for validation from Zk that instance is initialized.
	waitOnInstanceInitialization();

	// Call logic for starting microservice subclass.
	microserviceStart(monitor);
    }
}