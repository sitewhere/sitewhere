/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.batch;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Interface for interacting with a batch operation manager.
 * 
 * @author Derek
 */
public interface IBatchOperationManager extends ILifecycleComponent {

	/**
	 * Queues an {@link IBatchOperation} for processing. Note that this does not mean that
	 * the batch will be immediately processed since other batches may be queued first.
	 * 
	 * @param operation
	 * @throws SiteWhereException
	 */
	public void queueForProcessing(IBatchOperation operation) throws SiteWhereException;
}