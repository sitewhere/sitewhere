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
		setDescription("Allows configuration of all aspects of this tenant including data management, "
				+ "device communication, inbound/outbound event processing, and asset management. After "
				+ "making changes, click <strong>Stage Updates</strong> to store the updates. The changes "
				+ "will be applied the next time the tenant is restarted.");
		setRole(ElementRole.Root.name());
		getElementsByRole().putAll(new GlobalsModel().getElementsByRole());
		getElementsByRole().putAll(new DataManagementModel().getElementsByRole());
		getElementsByRole().putAll(new DeviceCommunicationModel().getElementsByRole());
		getElementsByRole().putAll(new EventProcessingModel().getElementsByRole());
		getElementsByRole().putAll(new AssetManagementModel().getElementsByRole());
	}
}