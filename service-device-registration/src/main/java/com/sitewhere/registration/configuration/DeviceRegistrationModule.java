/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration.configuration;

import com.sitewhere.microservice.configuration.MicroserviceModule;

/**
 * Guice module used for configuring components associated with the device
 * registration microservice.
 */
public class DeviceRegistrationModule extends MicroserviceModule<DeviceRegistrationConfiguration> {

    public DeviceRegistrationModule(DeviceRegistrationConfiguration configuration) {
	super(configuration);
    }
}
