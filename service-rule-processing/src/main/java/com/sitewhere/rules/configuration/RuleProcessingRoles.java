/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rules.configuration;

import com.sitewhere.configuration.ConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Configuration roles available for event management microservice.
 * 
 * @author Derek
 */
public enum RuleProcessingRoles implements IConfigurationRoleProvider {

    /** Root presence management role. */
    RuleProcessing(ConfigurationRole.build(RuleProcessingRoleKeys.RuleProcessing, "Rule Processing", false, false,
	    false, new IRoleKey[] { RuleProcessingRoleKeys.ZoneTestProcessor }, new IRoleKey[0], true)),

    /** Zone test event processor. */
    ZoneTestProcessor(ConfigurationRole.build(RuleProcessingRoleKeys.ZoneTestProcessor, "Zone Test Processor", true,
	    true, true, new IRoleKey[] { RuleProcessingRoleKeys.ZoneTestElement })),

    /** Zone test. */
    ZoneTest(ConfigurationRole.build(RuleProcessingRoleKeys.ZoneTestElement, "Zone Test", true, true, true));

    private ConfigurationRole role;

    private RuleProcessingRoles(ConfigurationRole role) {
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