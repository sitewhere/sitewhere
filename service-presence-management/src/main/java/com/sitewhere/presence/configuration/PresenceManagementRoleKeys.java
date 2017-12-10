/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.presence.configuration;

import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

public enum PresenceManagementRoleKeys implements IRoleKey {

    /** Presence management */
    PresenceManagement("presence_mgmt"),

    /** Presence manager */
    PresenceManager("presence_mgr");

    private String id;

    private PresenceManagementRoleKeys(String id) {
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