/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.search.device;

/**
 * Parameters needed for a search of devices by specification.
 * 
 * @author Derek
 */
public interface IDeviceBySpecificationParameters {

	/**
	 * Get the specification token being searched for.
	 * 
	 * @return
	 */
	public String getSpecificationToken();
}