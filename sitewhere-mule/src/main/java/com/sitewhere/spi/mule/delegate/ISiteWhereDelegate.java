/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
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
 * Delegate that executes processing logic with access to SiteWhere and Mule internals.
 * 
 * @author dadams
 */
public interface ISiteWhereDelegate {

	/**
	 * Process information using SiteWhere and Mule information. If response is non-null, it will be returned
	 * as the new payload. Otherwise, the current SiteWhere context will be returned.
	 * 
	 * @param context
	 * @param client
	 * @param event
	 * @return
	 * @throws SiteWhereException
	 */
	public Object process(ISiteWhereContext context, ISiteWhereClient client, MuleEvent event)
			throws SiteWhereException;
}