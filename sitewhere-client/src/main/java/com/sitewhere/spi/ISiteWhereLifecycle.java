/*
 * ISiteWhereLifecycle.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi;

/**
 * Lifecycle methods used in service provider implementations.
 * 
 * @author Derek
 */
public interface ISiteWhereLifecycle {

	/**
	 * Start the device management implementation.
	 * 
	 * @throws SiteWhereException
	 */
	public void start() throws SiteWhereException;

	/**
	 * Stop the device management implementation.
	 * 
	 * @throws SiteWhereException
	 */
	public void stop() throws SiteWhereException;
}