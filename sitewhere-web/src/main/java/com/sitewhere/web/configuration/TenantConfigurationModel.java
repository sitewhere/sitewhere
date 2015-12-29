/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration;

import com.sitewhere.web.configuration.model.ConfigurationModel;
import com.sitewhere.web.configuration.model.ElementRole;

/**
 * Specifies model used for tenant configuration user interface.
 * 
 * @author Derek
 */
public class TenantConfigurationModel extends ConfigurationModel {

	public TenantConfigurationModel() {
		setLocalName("tenant-configuration");
		setName("Tenant Configuration");
		setDescription("Provides a model for all aspects of tenant configuration.");
		setRole(ElementRole.Root.name());
		getElementsByRole().putAll(new GlobalsModel().getElementsByRole());
		getElementsByRole().putAll(new DataManagementModel().getElementsByRole());
		getElementsByRole().putAll(new DeviceCommunicationModel().getElementsByRole());
		getElementsByRole().putAll(new InboundProcessingChainModel().getElementsByRole());
		getElementsByRole().putAll(new OutboundProcessingChainModel().getElementsByRole());
		getElementsByRole().putAll(new AssetManagementModel().getElementsByRole());
	}
}