package com.sitewhere.server.lifecycle;

import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.monitoring.IProgressMessage;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleProgressUtils;

/**
 * Default implementation of {@link ILifecycleProgressMonitor}.
 * 
 * @author Derek
 */
public class LifecycleProgressMonitor implements ILifecycleProgressMonitor {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Stack for nested progress tracking */
    private Deque<ILifecycleProgressContext> contextStack = new ArrayDeque<ILifecycleProgressContext>();

    public LifecycleProgressMonitor(ILifecycleProgressContext initialContext) {
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
	LOGGER.info(
		"[" + message.getTaskName() + "]: (" + message.getProgressPercentage() + "%) " + message.getMessage());
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
    public Deque<ILifecycleProgressContext> getContextStack() {
	return contextStack;
    }

    public void setContextStack(Deque<ILifecycleProgressContext> contextStack) {
	this.contextStack = contextStack;
    }
}