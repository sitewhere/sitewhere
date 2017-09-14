package com.sitewhere.server.lifecycle;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Composite step used for initializing a component.
 * 
 * @author Derek
 */
public class InitializeComponentLifecycleStep extends ComponentOperationLifecycleStep {

    /** Error message if failed */
    private String errorMessage;

    /** Indicates of required for parent component to function */
    private boolean require;

    public InitializeComponentLifecycleStep(ILifecycleComponent owner, ILifecycleComponent component, String name,
	    String errorMessage, boolean require) {
	super(owner, component, name);
	this.errorMessage = errorMessage;
	this.require = require;
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
	    try {
		getOwner().initializeNestedComponent(getComponent(), monitor, getErrorMessage(), isRequire());
	    } catch (SiteWhereException t) {
		throw t;
	    } catch (Throwable t) {
		throw new SiteWhereException(getErrorMessage(), t);
	    }
	} else {
	    throw new SiteWhereException(
		    "Attempting to initialize component '" + getName() + "' but component is null.");
	}
    }

    public String getErrorMessage() {
	return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
    }

    public boolean isRequire() {
	return require;
    }

    public void setRequire(boolean require) {
	this.require = require;
    }
}