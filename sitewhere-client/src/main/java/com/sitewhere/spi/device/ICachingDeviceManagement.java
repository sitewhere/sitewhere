/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

import com.sitewhere.spi.SiteWhereException;

/**
 * Interface implemented by {@link IDeviceManagement} implementations that can use caching
 * mechanisms.
 * 
 * @author Derek
 */
public interface ICachingDeviceManagement {

	/**
	 * Set the cache provider to be used by the {@link IDeviceManagement} implementation.
	 * 
	 * @param provider
	 * @throws SiteWhereException
	 */
	public void setCacheProvider(IDeviceManagementCacheProvider provider) throws SiteWhereException;
}