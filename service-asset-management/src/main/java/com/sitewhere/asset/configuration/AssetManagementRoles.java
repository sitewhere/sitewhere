/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.configuration;

import com.sitewhere.configuration.ConfigurationRole;
import com.sitewhere.configuration.model.CommonDatastoreRoleKeys;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Configuration roles available for asset management microservice.
 * 
 * @author Derek
 */
public enum AssetManagementRoles implements IConfigurationRoleProvider {

    /** Asset management. */
    AssetManagement(ConfigurationRole.build(AssetManagementRoleKeys.AssetManagement, "Asset Management", false, false,
	    false,
	    new IRoleKey[] { CommonDatastoreRoleKeys.DeviceManagementDatastore, AssetManagementRoleKeys.AssetModules },
	    new IRoleKey[0], true)),

    /** Asset modules. */
    AssetModules(ConfigurationRole.build(AssetManagementRoleKeys.AssetModules, "Asset Modules", false, false, false,
	    new IRoleKey[] { AssetManagementRoleKeys.AssetModule }, new IRoleKey[0], true)),

    /** Asset Management. Asset module */
    AssetModule(ConfigurationRole.build(AssetManagementRoleKeys.AssetModule, "Asset Module", true, true, true));

    private ConfigurationRole role;

    private AssetManagementRoles(ConfigurationRole role) {
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