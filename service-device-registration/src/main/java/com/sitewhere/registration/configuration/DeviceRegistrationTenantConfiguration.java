/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration.configuration;

import com.sitewhere.spi.microservice.multitenant.ITenantEngineConfiguration;

/**
 * Maps device registration YAML configuration to objects.
 */
public class DeviceRegistrationTenantConfiguration implements ITenantEngineConfiguration {

    /** Default setting for whether new devices are allowed */
    private static final boolean DEFAULT_ALLOW_NEW_DEVICES = false;

    /** Indicates whether new devices are allowed */
    private boolean allowNewDevices = DEFAULT_ALLOW_NEW_DEVICES;

    /** Assignment defaults */
    private AssignmentDefaults assignmentDefaults = new AssignmentDefaults();

    public boolean isAllowNewDevices() {
	return allowNewDevices;
    }

    public void setAllowNewDevices(boolean allowNewDevices) {
	this.allowNewDevices = allowNewDevices;
    }

    public AssignmentDefaults getAssignmentDefaults() {
	return assignmentDefaults;
    }

    public void setAssignmentDefaults(AssignmentDefaults assignmentDefaults) {
	this.assignmentDefaults = assignmentDefaults;
    }
}
