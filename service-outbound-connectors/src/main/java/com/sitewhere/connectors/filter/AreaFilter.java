/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.filter;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;

/**
 * Includes or excludes events for devices associated with a given area.
 */
public class AreaFilter extends DeviceEventFilter {

    /** Area token to check */
    private String areaToken;

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
	if (event.getAreaId() != null) {
	    IArea area = getDeviceManagementApiChannel().getArea(event.getAreaId());
	    if (area == null) {
		throw new SiteWhereException("Unable to process event filter for non-existent area.");
	    }
	    if (getAreaToken().equals(area.getToken())) {
		return (getOperation() != FilterOperation.Include);
	    }
	    return (getOperation() == FilterOperation.Include);
	}
	return (getOperation() == FilterOperation.Include);
    }

    public String getAreaToken() {
	return areaToken;
    }

    public void setAreaToken(String areaToken) {
	this.areaToken = areaToken;
    }

    public FilterOperation getOperation() {
	return operation;
    }

    public void setOperation(FilterOperation operation) {
	this.operation = operation;
    }
}