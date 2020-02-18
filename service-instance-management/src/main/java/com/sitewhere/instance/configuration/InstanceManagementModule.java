/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.configuration;

import com.sitewhere.instance.user.persistence.SyncopeUserManagement;
import com.sitewhere.microservice.api.user.IUserManagement;
import com.sitewhere.microservice.configuration.MicroserviceModule;

/**
 * Guice module used for configuring components associated with the instance
 * management microservice.
 */
public class InstanceManagementModule extends MicroserviceModule<InstanceManagementConfiguration> {

    public InstanceManagementModule(InstanceManagementConfiguration configuration) {
	super(configuration);
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(InstanceManagementConfiguration.class).toInstance(getConfiguration());
	bind(IUserManagement.class).to(SyncopeUserManagement.class);
    }
}
