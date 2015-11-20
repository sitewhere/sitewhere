/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server;

import com.sitewhere.spi.SiteWhereException;

/**
 * Common interface for model initializers.
 * 
 * @author Derek
 */
public interface IModelInitializer {

	/**
	 * Indicates whether model should be initialized if no console is available for user
	 * input.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public boolean isInitializeIfNoConsole() throws SiteWhereException;
}