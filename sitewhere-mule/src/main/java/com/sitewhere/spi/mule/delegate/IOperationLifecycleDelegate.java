/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.mule.delegate;

import org.mule.api.MuleEvent;

import com.sitewhere.spi.ISiteWhereClient;
import com.sitewhere.spi.ISiteWhereContext;
import com.sitewhere.spi.SiteWhereException;

/**
 * Delegate that executes before and after an operation is executed.
 * 
 * @author Derek Adams
 */
public interface IOperationLifecycleDelegate {

	/**
	 * Called before an ESB operation is executed.
	 * 
	 * @param context
	 * @param client
	 * @throws SiteWhereException
	 */
	public void beforeOperation(ISiteWhereContext context, ISiteWhereClient client, MuleEvent event)
			throws SiteWhereException;

	/**
	 * Called before an ESB operation is executed.
	 * 
	 * @param context
	 * @param client
	 * @throws SiteWhereException
	 */
	public void afterOperation(ISiteWhereContext context, ISiteWhereClient client, MuleEvent event)
			throws SiteWhereException;
}