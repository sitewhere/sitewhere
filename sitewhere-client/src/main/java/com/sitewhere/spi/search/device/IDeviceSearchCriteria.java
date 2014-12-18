/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.search.device;

import com.sitewhere.spi.search.IDateRangeSearchCriteria;

/**
 * Search criteria particular to device searches.
 * 
 * @author Derek
 */
public interface IDeviceSearchCriteria extends IDateRangeSearchCriteria {

	/**
	 * Indicates whether assigned devices should be returned.
	 * 
	 * @return
	 */
	public boolean isIncludeAssigned();

	/**
	 * Gets the type of search being performed.
	 * 
	 * @return
	 */
	public DeviceSearchType getSearchType();

	/**
	 * If searching for devices using a given specification, this will contain the
	 * parameters for the search.
	 * 
	 * @return
	 */
	public IDeviceBySpecificationParameters getDeviceBySpecificationParameters();
}