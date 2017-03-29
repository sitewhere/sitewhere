package com.sitewhere.spi.server.lifecycle;

import java.util.Deque;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.monitoring.IProgressReporter;

/**
 * Allows progress to be monitored on long-running lifecycle tasks.
 * 
 * @author Derek
 */
public interface ILifecycleProgressMonitor extends IProgressReporter {

    /**
     * Get current list of nested contexts.
     * 
     * @return
     */
    public Deque<ILifecycleProgressContext> getContextStack();

    /**
     * Push a new nested context onto the stack.
     * 
     * @param context
     * @throws SiteWhereException
     */
    public void pushContext(ILifecycleProgressContext context) throws SiteWhereException;

    /**
     * Start progress on a new operation within the current nesting context.
     * 
     * @param operation
     * @throws SiteWhereException
     */
    public void startProgress(String operation) throws SiteWhereException;

    /**
     * Finish progress for the current operation. This results in reporting of
     * progress message.
     * 
     * @throws SiteWhereException
     */
    public void finishProgress() throws SiteWhereException;

    /**
     * Pop last context from the stack.
     * 
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleProgressContext popContext() throws SiteWhereException;
}