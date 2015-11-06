/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.event.processor.filter;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Filters events for devices associated with a given site.
 * 
 * @author Derek
 */
public class SiteFilter extends DeviceEventFilter {

	/** Site token to allow */
	private String siteToken;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IDeviceEventFilter#isFiltered(com.sitewhere
	 * .spi.device.event.IDeviceEvent)
	 */
	@Override
	public boolean isFiltered(IDeviceEvent event) throws SiteWhereException {
		if (event.getSiteToken().equals(getSiteToken())) {
			return true;
		}
		return false;
	}

	public String getSiteToken() {
		return siteToken;
	}

	public void setSiteToken(String siteToken) {
		this.siteToken = siteToken;
	}
}