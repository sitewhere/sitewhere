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

import com.sitewhere.rest.model.device.group.DeviceGroup;

/**
 * Search results that contain device groups.
 * 
 * @author Derek
 */
public class DeviceGroupSearchResults extends SearchResults<DeviceGroup> {

    public DeviceGroupSearchResults() {
	super(new ArrayList<DeviceGroup>());
    }

    public DeviceGroupSearchResults(List<DeviceGroup> results) {
	super(results);
    }
}