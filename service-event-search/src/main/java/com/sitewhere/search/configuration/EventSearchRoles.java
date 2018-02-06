/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.search.configuration;

import com.sitewhere.configuration.ConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Configuration roles available for event search microservice.
 * 
 * @author Derek
 */
public enum EventSearchRoles implements IConfigurationRoleProvider {

    /** Root event search role. */
    EventSearch(ConfigurationRole.build(EventSearchRoleKeys.EventSearch, "Event Search", false, false, false,
	    new IRoleKey[] { EventSearchRoleKeys.SearchProviders }, new IRoleKey[0], true)),

    /** Search providers. */
    SearchProviders(ConfigurationRole.build(EventSearchRoleKeys.SearchProviders, "Search Providers", true, false, false,
	    new IRoleKey[] { EventSearchRoleKeys.SearchProvider })),

    /** Search provider. */
    SearchProvider(ConfigurationRole.build(EventSearchRoleKeys.SearchProvider, "Search Provider", true, true, true));

    private ConfigurationRole role;

    private EventSearchRoles(ConfigurationRole role) {
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
