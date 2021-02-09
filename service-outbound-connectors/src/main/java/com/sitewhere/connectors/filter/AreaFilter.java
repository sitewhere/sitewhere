/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
	    IArea area = getDeviceManagement().getArea(event.getAreaId());
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