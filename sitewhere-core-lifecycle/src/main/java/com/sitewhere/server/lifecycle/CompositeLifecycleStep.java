/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;

/**
 * Implementation of {@link ILifecycleStep} that is composed of multiple
 * lifecycle steps that are executed in order.
 */
public class CompositeLifecycleStep implements ICompositeLifecycleStep {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(CompositeLifecycleStep.class);

    /** Step name */
    private String name;

    /** List of lifecycle steps to be executed */
    private List<ILifecycleStep> steps = new ArrayList<ILifecycleStep>();

    public CompositeLifecycleStep(String name) {
	this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleStep#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleStep#getOperationCount()
     */
    @Override
    public int getOperationCount() {
	return steps.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleStep#execute(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	monitor.pushContext(new LifecycleProgressContext(steps.size(), getName()));
	try {
	    for (ILifecycleStep step : steps) {
		LOGGER.trace(String.format("About to start step '%s'...", step.getName()));
		try {
		    monitor.startProgress(step.getName());
		    step.execute(monitor);
		    monitor.finishProgress();
		} catch (SiteWhereException t) {
		    throw t;
		} catch (Throwable t) {
		    throw new SiteWhereException("Unhandled exception in composite lifecycle step.", t);
		}
	    }
	} finally {
	    monitor.popContext();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep#addStep(com.
     * sitewhere.spi.server.lifecycle.ILifecycleStep)
     */
    public void addStep(ILifecycleStep step) {
	LOGGER.trace("In addStep() for " + step.getName());
	getSteps().add(step);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep#
     * addInitializeStep(com.sitewhere.spi.server.lifecycle.ILifecycleComponent,
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent, boolean)
     */
    @Override
    public void addInitializeStep(ILifecycleComponent owner, ILifecycleComponent component, boolean require) {
	LOGGER.trace("In addInitializeStep() for " + component);
	if (component != null) {
	    addStep(new InitializeComponentLifecycleStep(owner, component, require));
	} else {
	    LOGGER.warn("Skipping 'initialize' step for null component.");
	}
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep#addStartStep(
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent,
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent, boolean)
     */
    @Override
    public void addStartStep(ILifecycleComponent owner, ILifecycleComponent component, boolean require) {
	LOGGER.trace("In addStartStep() for " + component);
	if (component != null) {
	    addStep(new StartComponentLifecycleStep(owner, component, require));
	} else {
	    LOGGER.warn("Skipping 'start' step for null component.");
	}
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep#addStopStep(
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent,
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent)
     */
    @Override
    public void addStopStep(ILifecycleComponent owner, ILifecycleComponent component) {
	if (component != null) {
	    addStep(new StopComponentLifecycleStep(owner, component));
	} else {
	    LOGGER.debug("Skipping 'stop' step for null component.");
	}
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep#addTerminateStep(
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent,
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent)
     */
    @Override
    public void addTerminateStep(ILifecycleComponent owner, ILifecycleComponent component) {
	addStep(new TerminateComponentLifecycleStep(owner, component));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep#getSteps()
     */
    public List<ILifecycleStep> getSteps() {
	return steps;
    }

    public void setSteps(List<ILifecycleStep> steps) {
	this.steps = steps;
    }
}