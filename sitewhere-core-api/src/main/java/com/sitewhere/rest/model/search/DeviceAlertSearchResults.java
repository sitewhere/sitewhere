/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.rest.model.device.event.DeviceAlert;

/**
 * Search results that contain device alerts.
 * 
 * @author dadams
 */
public class DeviceAlertSearchResults extends SearchResults<DeviceAlert> {

    public DeviceAlertSearchResults() {
	super(new ArrayList<DeviceAlert>());
    }

    public DeviceAlertSearchResults(List<DeviceAlert> results) {
	super(results);
    }
}