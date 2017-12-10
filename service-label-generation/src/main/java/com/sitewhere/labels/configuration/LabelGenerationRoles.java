/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.configuration;

import com.sitewhere.configuration.ConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Configuration roles available for label generation microservice.
 * 
 * @author Derek
 */
public enum LabelGenerationRoles implements IConfigurationRoleProvider {

    /** Root event sources role. */
    LabelGeneration(ConfigurationRole.build(LabelGenerationRoleKeys.LabelGeneration, "Label Generation", false, false,
	    false, new IRoleKey[] { LabelGenerationRoleKeys.SymbolGeneratorManager }, new IRoleKey[0], true)),

    /** Device services container. Symbol generator manager. */
    SymbolGeneratorManager(
	    ConfigurationRole.build(LabelGenerationRoleKeys.SymbolGeneratorManager, "Symbol Generator Manager", true,
		    false, false, new IRoleKey[] { LabelGenerationRoleKeys.SymbolGenerator })),

    /** Symbol generator manager. Symbol generator. */
    SymbolGenerator(
	    ConfigurationRole.build(LabelGenerationRoleKeys.SymbolGenerator, "Symbol Generator", true, true, true));

    private ConfigurationRole role;

    private LabelGenerationRoles(ConfigurationRole role) {
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