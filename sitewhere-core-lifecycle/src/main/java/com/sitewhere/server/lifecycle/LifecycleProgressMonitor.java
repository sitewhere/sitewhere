/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle;

import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.monitoring.IProgressErrorMessage;
import com.sitewhere.spi.monitoring.IProgressMessage;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleProgressUtils;

/**
 * Default implementation of {@link ILifecycleProgressMonitor}.
 */
public class LifecycleProgressMonitor implements ILifecycleProgressMonitor {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(LifecycleProgressMonitor.class);

    /** Stack for nested progress tracking */
    private Deque<ILifecycleProgressContext> contextStack = new ArrayDeque<ILifecycleProgressContext>();

    /** Microservice associated with component */
    private IMicroservice<?> microservice;

    public LifecycleProgressMonitor(ILifecycleProgressContext initialContext, IMicroservice<?> microservice) {
	this.microservice = microservice;
	contextStack.push(initialContext);
	try {
	    LifecycleProgressUtils.startProgressOperation(this, initialContext.getTaskName());
	} catch (SiteWhereException e) {
	    throw new RuntimeException("Unable to create progress monitor.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.monitoring.IProgressReporter#reportProgress(com.
     * sitewhere.spi.monitoring.IProgressMessage)
     */
    @Override
    public void reportProgress(IProgressMessage message) throws SiteWhereException {
	LOGGER.debug(
		"[" + message.getTaskName() + "]: (" + message.getProgressPercentage() + "%) " + message.getMessage());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.monitoring.IProgressReporter#reportError(com.sitewhere.
     * spi.monitoring.IProgressErrorMessage)
     */
    @Override
    public void reportError(IProgressErrorMessage error) throws SiteWhereException {
	LOGGER.info("[ERROR][" + error.getTaskName() + "]: (" + error.getProgressPercentage() + "%) "
		+ error.getMessage() + "[" + error.getLevel().name() + "]");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor#pushContext(
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext)
     */
    @Override
    public void pushContext(ILifecycleProgressContext context) throws SiteWhereException {
	getContextStack().push(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor#
     * startProgress(java.lang.String)
     */
    @Override
    public void startProgress(String operation) throws SiteWhereException {
	LifecycleProgressUtils.startProgressOperation(this, operation);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor#
     * finishProgress()
     */
    @Override
    public void finishProgress() throws SiteWhereException {
	LifecycleProgressUtils.finishProgressOperation(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor#popContext()
     */
    @Override
    public ILifecycleProgressContext popContext() throws SiteWhereException {
	return getContextStack().pop();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor#
     * getContextStack()
     */
    @Override
    public Deque<ILifecycleProgressContext> getContextStack() {
	return contextStack;
    }

    public void setContextStack(Deque<ILifecycleProgressContext> contextStack) {
	this.contextStack = contextStack;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor#
     * getMicroservice()
     */
    @Override
    public IMicroservice<?> getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroservice<?> microservice) {
	this.microservice = microservice;
    }
}