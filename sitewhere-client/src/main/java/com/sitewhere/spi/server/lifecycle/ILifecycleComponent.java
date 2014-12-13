/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.lifecycle;

import org.apache.log4j.Logger;

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
	 * Get current lifecycle status.
	 * 
	 * @return
	 */
	public LifecycleStatus getLifecycleStatus();

	/**
	 * Gets the last lifecycle error that occurred.
	 * 
	 * @return
	 */
	public SiteWhereException getLifecycleError();

	/**
	 * Starts the component while keeping up with lifecycle information.
	 */
	public void lifecycleStart();

	/**
	 * Start the component.
	 * 
	 * @throws SiteWhereException
	 */
	public void start() throws SiteWhereException;

	/**
	 * Stops the component while keeping up with lifecycle information.
	 */
	public void lifecycleStop();

	/**
	 * Stop the component.
	 * 
	 * @throws SiteWhereException
	 */
	public void stop() throws SiteWhereException;

	/**
	 * Get component logger.
	 * 
	 * @return
	 */
	public Logger getLogger();
}