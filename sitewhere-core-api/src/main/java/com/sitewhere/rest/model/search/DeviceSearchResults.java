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

import com.sitewhere.rest.model.device.Device;

/**
 * Search results that contain devices. Needed so that JSON marshaling has a
 * concrete class to inflate.
 * 
 * @author dadams
 */
public class DeviceSearchResults extends SearchResults<Device> {

    public DeviceSearchResults() {
	super(new ArrayList<Device>());
    }

    public DeviceSearchResults(List<Device> results) {
	super(results);
    }
}