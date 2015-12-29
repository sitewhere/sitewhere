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
 * Configuration model for asset management elements.
 * 
 * @author Derek
 */
public class AssetManagementModel extends ConfigurationModel {

	public AssetManagementModel() {
		addElement(createAssetManagement());
	}

	/**
	 * Create the container for asset management configuration.
	 * 
	 * @return
	 */
	protected ElementNode createAssetManagement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Asset Management",
						TenantConfigurationParser.Elements.AssetManagement.getLocalName(), "tag",
						ElementRole.AssetManagment);
		builder.setDescription("Configure asset management features.");
		return builder.build();
	}
}