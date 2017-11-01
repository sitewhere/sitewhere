/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;

/**
 * Implementaton of {@link ILifecycleStep} that starts a single component as a
 * nested component of an owning lifecycle component.
 * 
 * @author Derek
 */
public class StartComponentLifecycleStep extends ComponentOperationLifecycleStep {

    /** Indicates of required for parent component to function */
    private boolean require;

    protected StartComponentLifecycleStep(ILifecycleComponent owner, ILifecycleComponent component, boolean require) {
	super(owner, component);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.ComponentOperationLifecycleStep#getName()
     */
    @Override
    public String getName() {
	return "Start " + getComponent().getComponentName();
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
		getOwner().startNestedComponent(getComponent(), monitor, isRequire());
	    } catch (SiteWhereException t) {
		throw t;
	    } catch (Throwable t) {
		throw new SiteWhereException("Unable to start " + getComponent().getComponentName(), t);
	    }
	} else {
	    throw new SiteWhereException(
		    "Attempting to start component '" + getComponent().getComponentName() + "' but component is null.");
	}
    }

    public boolean isRequire() {
	return require;
    }

    public void setRequire(boolean require) {
	this.require = require;
    }
}