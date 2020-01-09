/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.configuration.providers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sitewhere.event.configuration.EventManagementTenantConfiguration;
import com.sitewhere.warp10.Warp10Client;
import com.sitewhere.warp10.Warp10Configuration;

/**
 * Provides a client connected to Warp 10 based on tenant configuration.
 */
public class Warp10ClientProvider implements Provider<Warp10Client> {

    /** Injected configuration */
    private EventManagementTenantConfiguration configuration;

    @Inject
    public Warp10ClientProvider(EventManagementTenantConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see com.google.inject.Provider#get()
     */
    @Override
    public Warp10Client get() {
	Warp10Configuration warp10 = new Warp10Configuration();
	warp10.setApplication("sitewhere");
	warp10.setTokenSecret("sitewhere");
	warp10.setBaseUrl("http://sitewhere-warp10:8080/api/v0");
	return new Warp10Client(warp10);
    }

    protected EventManagementTenantConfiguration getConfiguration() {
	return configuration;
    }
}