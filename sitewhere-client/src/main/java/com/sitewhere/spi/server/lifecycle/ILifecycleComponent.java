/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.lifecycle;

import com.sitewhere.spi.SiteWhereException;

/**
 * Lifecycle methods used in SiteWhere components.
 * 
 * @author Derek
 */
public interface ILifecycleComponent {

	/**
	 * Get human-readable name shown for component.
	 * 
	 * @return
	 */
	public String getComponentName();

	/**
	 * Start the component.
	 * 
	 * @throws SiteWhereException
	 */
	public void start() throws SiteWhereException;

	/**
	 * Stop the component.
	 * 
	 * @throws SiteWhereException
	 */
	public void stop() throws SiteWhereException;
}