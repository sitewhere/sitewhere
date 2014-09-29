/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

import com.sitewhere.spi.ISiteWhereLifecycle;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.cache.ICache;

/**
 * Interface for entity that provides caching for device management objects.
 * 
 * @author Derek
 */
public interface IDeviceManagementCacheProvider extends ISiteWhereLifecycle {

	/**
	 * Gets cache mapping specification tokens for {@link IDeviceSpecification} objects.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public ICache<String, IDeviceSpecification> getDeviceSpecificationCache() throws SiteWhereException;

	/**
	 * Gets cache mapping hardware ids to {@link IDevice} objects.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public ICache<String, IDevice> getDeviceCache() throws SiteWhereException;

	/**
	 * Get cache mapping assignment tokens to {@link IDeviceAssignment} objects.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public ICache<String, IDeviceAssignment> getDeviceAssignmentCache() throws SiteWhereException;
}