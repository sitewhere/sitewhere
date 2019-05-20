/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.filter;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;

/**
 * Includes or excludes events for devices using a given device type.
 * 
 * @author Derek
 */
public class DeviceTypeFilter extends DeviceEventFilter {

    /** Device type token to match */
    private String deviceTypeToken;

    /** Operation filter performs */
    private FilterOperation operation = FilterOperation.Include;

    /*
     * @see
     * com.sitewhere.outbound.spi.IDeviceEventFilter#isFiltered(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceEvent)
     */
    @Override
    public boolean isFiltered(IDeviceEventContext context, IDeviceEvent event) throws SiteWhereException {
	IDeviceType deviceType = getDeviceManagementApiChannel().getDeviceType(context.getDeviceTypeId());
	if (deviceType == null) {
	    throw new SiteWhereException("Event filter unable to process event for unknown device type.");
	}
	if (getDeviceTypeToken().equals(deviceType.getToken())) {
	    return (getOperation() != FilterOperation.Include);
	}
	return (getOperation() == FilterOperation.Include);
    }

    public String getDeviceTypeToken() {
	return deviceTypeToken;
    }

    public void setDeviceTypeToken(String deviceTypeToken) {
	this.deviceTypeToken = deviceTypeToken;
    }

    public FilterOperation getOperation() {
	return operation;
    }

    public void setOperation(FilterOperation operation) {
	this.operation = operation;
    }
}