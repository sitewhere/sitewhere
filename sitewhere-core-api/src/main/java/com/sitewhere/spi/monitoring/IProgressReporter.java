/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.monitoring;

import com.sitewhere.spi.SiteWhereException;

/**
 * Allows long-running tasks to report their progress.
 */
public interface IProgressReporter {

    /**
     * Report progress for an operation.
     * 
     * @param message
     * @throws SiteWhereException
     */
    public void reportProgress(IProgressMessage message) throws SiteWhereException;

    /**
     * Report that an error occurred in a monitored operation.
     * 
     * @param error
     * @throws SiteWhereException
     */
    public void reportError(IProgressErrorMessage error) throws SiteWhereException;
}