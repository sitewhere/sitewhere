/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.configuration;

import com.sitewhere.configuration.model.ConfigurationModelProvider;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;

/**
 * Configuration model provider for device management microservice.
 * 
 * @author Derek
 */
public class DeviceManagementModelProvider extends ConfigurationModelProvider {

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IConfigurationModel#
     * getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/device-management";
    }

    /*
     * @see com.sitewhere.configuration.model.DependencyResolvingConfigurationModel#
     * getRootRole()
     */
    @Override
    public IConfigurationRoleProvider getRootRole() {
	return DeviceManagementRoles.DeviceManagement;
    }

    /*
     * @see
     * com.sitewhere.configuration.model.MicroserviceConfigurationModel#addElements(
     * )
     */
    @Override
    public void addElements() {
    }
}