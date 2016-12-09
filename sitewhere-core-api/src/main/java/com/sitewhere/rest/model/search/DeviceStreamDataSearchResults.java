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

import com.sitewhere.rest.model.device.event.DeviceStreamData;

/**
 * Search results that contain device stream data.
 * 
 * @author dadams
 */
public class DeviceStreamDataSearchResults extends SearchResults<DeviceStreamData> {

    public DeviceStreamDataSearchResults() {
	super(new ArrayList<DeviceStreamData>());
    }

    public DeviceStreamDataSearchResults(List<DeviceStreamData> results) {
	super(results);
    }
}
