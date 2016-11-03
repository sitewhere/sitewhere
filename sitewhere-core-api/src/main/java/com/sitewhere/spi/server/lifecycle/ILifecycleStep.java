package com.sitewhere.spi.server.lifecycle;

import com.sitewhere.spi.SiteWhereException;

/**
 * Single step executed as part of a lifecycle operation.
 * 
 * @author Derek
 */
public interface ILifecycleStep {

    /**
     * Get the step name.
     * 
     * @return
     */
    public String getName();

    /**
     * Counts the number of operations that make up this step.
     * 
     * @return
     */
    public int getOperationCount();

    /**
     * Execute the step.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException;
}