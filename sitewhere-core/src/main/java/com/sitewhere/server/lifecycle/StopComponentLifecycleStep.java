package com.sitewhere.server.lifecycle;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;

/**
 * Implementaton of {@link ILifecycleStep} that stops a single component.
 * 
 * @author Derek
 */
public class StopComponentLifecycleStep extends ComponentOperationLifecycleStep {

    public StopComponentLifecycleStep(ILifecycleComponent owner, ILifecycleComponent component, String name) {
	super(owner, component, name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleStep#execute(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getComponent() != null) {
	    getComponent().lifecycleStop(monitor);
	}
    }
}
