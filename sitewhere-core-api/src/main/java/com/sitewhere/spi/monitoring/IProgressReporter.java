package com.sitewhere.spi.monitoring;

import com.sitewhere.spi.SiteWhereException;

/**
 * Allows long-running tasks to report their progress.
 * 
 * @author Derek
 */
public interface IProgressReporter {

    /**
     * Report progress for an operation.
     * 
     * @param message
     * @throws SiteWhereException
     */
    public void reportProgress(IProgressMessage message) throws SiteWhereException;
}