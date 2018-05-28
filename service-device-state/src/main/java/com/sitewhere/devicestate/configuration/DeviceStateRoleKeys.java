/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.configuration;

import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

public enum DeviceStateRoleKeys implements IRoleKey {

    /** Device state */
    DeviceState("device_state"),

    /** Presence manager */
    PresenceManager("presence_mgr");

    private String id;

    private DeviceStateRoleKeys(String id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IRoleKey#getId()
     */
    @Override
    public String getId() {
	return id;
    }
}