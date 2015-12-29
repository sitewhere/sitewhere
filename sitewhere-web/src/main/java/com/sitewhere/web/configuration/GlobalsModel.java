/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration;

import com.sitewhere.spring.handler.TenantConfigurationParser;
import com.sitewhere.web.configuration.model.ConfigurationModel;
import com.sitewhere.web.configuration.model.ElementNode;
import com.sitewhere.web.configuration.model.ElementRole;

/**
 * Configuration model for global elements.
 * 
 * @author Derek
 */
public class GlobalsModel extends ConfigurationModel {

	public GlobalsModel() {
		addElement(createGlobals());
	}

	/**
	 * Create the container for global overrides information.
	 * 
	 * @return
	 */
	protected ElementNode createGlobals() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Global Overrides",
						TenantConfigurationParser.Elements.Globals.getLocalName(), "cogs",
						ElementRole.Globals);
		builder.setDescription("Allow tenant-specific changes to global configuration elements.");
		return builder.build();
	}
}