/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration.configuration;

import com.sitewhere.configuration.ConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Configuration roles available for device registration microservice.
 * 
 * @author Derek
 */
public enum DeviceRegistrationRoles implements IConfigurationRoleProvider {

    /** Device registration. */
    DeviceRegistration(ConfigurationRole.build(DeviceRegistrationRoleKeys.DeviceRegistration, "Device Registration",
	    false, false, false, new IRoleKey[] { DeviceRegistrationRoleKeys.DeviceRegistrationManager },
	    new IRoleKey[0], true)),

    /** Device registration manager. */
    DeviceRegistrationManager(ConfigurationRole.build(DeviceRegistrationRoleKeys.DeviceRegistrationManager,
	    "Device Registration Manager", false, false, false));

    private ConfigurationRole role;

    private DeviceRegistrationRoles(ConfigurationRole role) {
	this.role = role;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider
     * #getRole()
     */
    @Override
    public IConfigurationRole getRole() {
	return role;
    }
}