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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.monitoring.IProgressErrorMessage;
import com.sitewhere.spi.monitoring.IProgressMessage;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleProgressUtils;
import com.sitewhere.spi.tracing.ITracerProvider;

import io.opentracing.ActiveSpan;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;

/**
 * Default implementation of {@link ILifecycleProgressMonitor}.
 * 
 * @author Derek
 */
public class LifecycleProgressMonitor implements ILifecycleProgressMonitor {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Tracer provider */
    private ITracerProvider tracerProvider;

    /** Stack for nested progress tracking */
    private Deque<ILifecycleProgressContext> contextStack = new ArrayDeque<ILifecycleProgressContext>();

    public LifecycleProgressMonitor(ILifecycleProgressContext initialContext, ITracerProvider tracerProvider) {
	this.tracerProvider = tracerProvider;
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
	LOGGER.info("[PROGRESS][" + message.getTaskName() + "]: (" + message.getProgressPercentage() + "%) "
		+ message.getMessage());
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
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor#
     * createTracerSpan()
     */
    @Override
    public ActiveSpan createTracerSpan() throws SiteWhereException {
	ILifecycleProgressContext current = getContextStack().peek();
	if (current != null) {
	    return getTracer().buildSpan(current.getCurrentOperationMessage()).startActive();
	}
	throw new SiteWhereException("Unable to create span. No context found.");
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor#
     * handleErrorInTracerSpan(io.opentracing.ActiveSpan, java.lang.Throwable)
     */
    @Override
    public void handleErrorInTracerSpan(ActiveSpan span, Throwable t) {
	if (span != null) {
	    span.setTag(Tags.ERROR.getKey(), true);
	    span.log(TracerLogUtils.mapOf("error.object", t));
	    span.log(TracerLogUtils.mapOf("message", t.getMessage()));
	}
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor#
     * finishTracerSpan(io.opentracing.ActiveSpan)
     */
    @Override
    public void finishTracerSpan(ActiveSpan span) {
	if (span != null) {
	    span.deactivate();
	}
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
    public Deque<ILifecycleProgressContext> getContextStack() {
	return contextStack;
    }

    public void setContextStack(Deque<ILifecycleProgressContext> contextStack) {
	this.contextStack = contextStack;
    }

    /*
     * @see com.sitewhere.spi.tracing.ITracerProvider#getTracer()
     */
    @Override
    public Tracer getTracer() {
	return getTracerProvider().getTracer();
    }

    public ITracerProvider getTracerProvider() {
	return tracerProvider;
    }

    public void setTracerProvider(ITracerProvider tracerProvider) {
	this.tracerProvider = tracerProvider;
    }
}