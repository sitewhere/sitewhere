/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.configuration;

import com.sitewhere.configuration.ConfigurationRole;
import com.sitewhere.configuration.model.CommonDatastoreRoleKeys;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Configuration roles available for batch operations microservice.
 */
public enum BatchOperationsRoles implements IConfigurationRoleProvider {

    /** Root event sources role. */
    BatchOperations(ConfigurationRole.build(
	    BatchOperationsRoleKeys.BatchOperations, "Batch Operations", false, false, false, new IRoleKey[] {
		    CommonDatastoreRoleKeys.DeviceManagementDatastore, BatchOperationsRoleKeys.BatchOperationManager },
	    new IRoleKey[0], true)),

    /** Batch operation manager. */
    BatchOperationManager(ConfigurationRole.build(BatchOperationsRoleKeys.BatchOperationManager,
	    "Batch Operation Manager", false, false, false));

    private ConfigurationRole role;

    private BatchOperationsRoles(ConfigurationRole role) {
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