/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration.configuration;

import com.sitewhere.configuration.model.ConfigurationModelProvider;
import com.sitewhere.configuration.old.IDeviceServicesParser;
import com.sitewhere.configuration.parser.IDeviceRegistrationParser;
import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;

/**
 * Configuration model provider for device registration microservice.
 * 
 * @author Derek
 */
public class DeviceRegistrationModelProvider extends ConfigurationModelProvider {

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/device-registration";
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getRootRole()
     */
    @Override
    public IConfigurationRoleProvider getRootRole() {
	return DeviceRegistrationRoles.DeviceRegistration;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeElements()
     */
    @Override
    public void initializeElements() {
	addElement(createDeviceRegistrationElement());
	addElement(createDefaultRegistrationManagerElement());
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeRoles()
     */
    @Override
    public void initializeRoles() {
	for (DeviceRegistrationRoles role : DeviceRegistrationRoles.values()) {
	    getRolesById().put(role.getRole().getKey().getId(), role.getRole());
	}
    }

    /**
     * Create element configuration for device registration.
     * 
     * @return
     */
    protected ElementNode createDeviceRegistrationElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Device Registration", IDeviceRegistrationParser.ROOT,
		"user-plus", DeviceRegistrationRoleKeys.DeviceRegistration, this);

	builder.description("Determines how new devices are registered with the system.");

	return builder.build();
    }

    /**
     * Create element configuration for default registration manager.
     * 
     * @return
     */
    protected ElementNode createDefaultRegistrationManagerElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Default Registration Manager",
		IDeviceServicesParser.Elements.DefaultRegistrationManager.getLocalName(), "key",
		DeviceRegistrationRoleKeys.DeviceRegistrationManager, this);

	builder.description("Provides device registration management functionality.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_GENERAL);

	builder.attribute((new AttributeNode.Builder("Allow registration of new devices", "allowNewDevices",
		AttributeType.Boolean, ConfigurationModelProvider.ATTR_GROUP_GENERAL)
			.description("Indicates whether new devices should be allowed to register with the system")
			.defaultValue("false").build()));
	builder.attribute((new AttributeNode.Builder("Use default device type", "useDefaultDeviceType",
		AttributeType.Boolean, ConfigurationModelProvider.ATTR_GROUP_GENERAL).description(
			"Indicates if a default device type should be used if no device type token is passed in registration request.")
			.build()));
	builder.attribute((new AttributeNode.Builder("Default device type", "defaultDeviceTypeToken",
		AttributeType.DeviceTypeReference, ConfigurationModelProvider.ATTR_GROUP_GENERAL)
			.description(
				"Default device type used if no device type token is passed in registration request.")
			.build()));
	builder.attribute((new AttributeNode.Builder("Use default customer", "useDefaultCustomer",
		AttributeType.Boolean, ConfigurationModelProvider.ATTR_GROUP_GENERAL).description(
			"Indicates if a default customer should be used if no customer token is passed in registration request.")
			.build()));
	builder.attribute((new AttributeNode.Builder("Default customer", "defaultCustomerToken",
		AttributeType.CustomerReference, ConfigurationModelProvider.ATTR_GROUP_GENERAL)
			.description("Default customer used if no customer token is passed in registration request.")
			.build()));
	builder.attribute((new AttributeNode.Builder("Use default area", "useDefaultArea", AttributeType.Boolean,
		ConfigurationModelProvider.ATTR_GROUP_GENERAL).description(
			"Indicates if a default area should be used if no area token is passed in registration request.")
			.build()));
	builder.attribute((new AttributeNode.Builder("Default area", "defaultAreaToken", AttributeType.AreaReference,
		ConfigurationModelProvider.ATTR_GROUP_GENERAL)
			.description("Default area used if no area token is passed in registration request.").build()));
	return builder.build();
    }
}