/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search.device;

import com.sitewhere.spi.search.device.IDeviceBySpecificationParameters;

/**
 * Model object that provides parameters for a search for devices that use a given
 * specification.
 * 
 * @author Derek
 */
public class DeviceBySpecificationParameters implements IDeviceBySpecificationParameters {

	/** Token for specification to filter by */
	private String specificationToken;

	public String getSpecificationToken() {
		return specificationToken;
	}

	public void setSpecificationToken(String specificationToken) {
		this.specificationToken = specificationToken;
	}
}