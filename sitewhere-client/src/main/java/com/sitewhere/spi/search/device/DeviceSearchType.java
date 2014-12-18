/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.search.device;

/**
 * Indicates the type of device search to be performed.
 * 
 * @author Derek
 */
public enum DeviceSearchType {

	/** Returns all devices */
	All,

	/** Returns devices that use a specification */
	UsesSpecification,
}