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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;

/**
 * Implementation of {@link ILifecycleStep} that is composed of multiple
 * lifecycle steps that are executed in order.
 * 
 * @author Derek
 */
public class CompositeLifecycleStep implements ICompositeLifecycleStep {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

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
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleStep#getOperationCount()
     */
    @Override
    public int getOperationCount() {
	return steps.size();
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
	monitor.pushContext(new LifecycleProgressContext(steps.size(), getName()));
	StringBuffer buffer = new StringBuffer();
	buffer.append("About to process composite lifecycle with " + steps.size() + "steps:\n");
	for (ILifecycleStep step : steps) {
	    buffer.append("  " + step.getName() + "\n");
	}
	LOGGER.debug(buffer.toString());
	try {
	    for (ILifecycleStep step : steps) {
		LOGGER.debug("Starting " + step.getName());
		monitor.startProgress(step.getName());
		step.execute(monitor);
		monitor.finishProgress();
	    }
	} catch (SiteWhereException e) {
	    throw e;
	} catch (Throwable e) {
	    throw new SiteWhereException("Unhandled exception in composite lifeycle step.", e);
	} finally {
	    monitor.popContext();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep#addStep(com.
     * sitewhere.spi.server.lifecycle.ILifecycleStep)
     */
    public void addStep(ILifecycleStep step) {
	getSteps().add(step);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep#getSteps()
     */
    public List<ILifecycleStep> getSteps() {
	return steps;
    }

    public void setSteps(List<ILifecycleStep> steps) {
	this.steps = steps;
    }
}