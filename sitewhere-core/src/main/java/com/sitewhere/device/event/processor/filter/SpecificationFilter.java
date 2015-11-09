/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.event.processor.filter;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Includes or excludes events for devices using a given specification.
 * 
 * @author Derek
 */
public class SpecificationFilter extends DeviceEventFilter {

	/** Specification token to match */
	private String specificationToken;

	/** Operation filter performs */
	private FilterOperation operation = FilterOperation.Include;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IDeviceEventFilter#isFiltered(com.sitewhere
	 * .spi.device.event.IDeviceEvent, com.sitewhere.spi.device.IDevice,
	 * com.sitewhere.spi.device.IDeviceAssignment)
	 */
	@Override
	public boolean isFiltered(IDeviceEvent event, IDevice device, IDeviceAssignment assignment)
			throws SiteWhereException {
		if (getSpecificationToken().equals(device.getSpecificationToken())) {
			return (getOperation() != FilterOperation.Include);
		}
		return (getOperation() == FilterOperation.Include);
	}

	public String getSpecificationToken() {
		return specificationToken;
	}

	public void setSpecificationToken(String specificationToken) {
		this.specificationToken = specificationToken;
	}

	public FilterOperation getOperation() {
		return operation;
	}

	public void setOperation(FilterOperation operation) {
		this.operation = operation;
	}
}