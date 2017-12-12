/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.model;

import com.sitewhere.configuration.ConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Common roles used by other microservices.
 * 
 * @author Derek
 */
public enum CommonModelRoles implements IConfigurationRoleProvider {

    /** Top-level common role. */
    SiteWhereCommon(ConfigurationRole.build(CommonModelRoleKeys.SiteWhereCommon, "SiteWhere Common", false, false,
	    false, new IRoleKey[] { CommonModelRoleKeys.Datastore }, new IRoleKey[0])),

    /** Datastore. */
    Datastore(ConfigurationRole.build(CommonModelRoleKeys.Datastore, "Datastore", false, false, false, new IRoleKey[0],
	    new IRoleKey[0]));

    private ConfigurationRole role;

    private CommonModelRoles(ConfigurationRole role) {
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