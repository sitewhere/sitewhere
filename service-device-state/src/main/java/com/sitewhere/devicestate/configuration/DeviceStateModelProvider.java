/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.configuration;

import com.sitewhere.configuration.model.ConfigurationModelProvider;
import com.sitewhere.configuration.old.IDeviceServicesParser;
import com.sitewhere.configuration.parser.IPresenceManagementParser;
import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;

/**
 * Configuration model provider for presence management microservice.
 * 
 * @author Derek
 */
public class DeviceStateModelProvider extends ConfigurationModelProvider {

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/device-state";
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getRootRole()
     */
    @Override
    public IConfigurationRoleProvider getRootRole() {
	return DeviceStateRoles.DeviceState;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeElements()
     */
    @Override
    public void initializeElements() {
	addElement(createDeviceStateElement());

	addElement(createDefaultPresenceManagerElement());
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeRoles()
     */
    @Override
    public void initializeRoles() {
	for (DeviceStateRoles role : DeviceStateRoles.values()) {
	    getRolesById().put(role.getRole().getKey().getId(), role.getRole());
	}
    }

    /**
     * Create presence management element.
     * 
     * @return
     */
    protected ElementNode createDeviceStateElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Device State", IPresenceManagementParser.ROOT,
		"question-circle", DeviceStateRoleKeys.DeviceState, this);

	builder.description("Records device state persistence and presence management.");

	return builder.build();
    }

    /**
     * Create element configuration for default presence manager.
     * 
     * @return
     */
    protected ElementNode createDefaultPresenceManagerElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Default Presence Manager",
		IDeviceServicesParser.Elements.DefaultPresenceManager.getLocalName(), "bullseye",
		DeviceStateRoleKeys.PresenceManager, this);

	builder.description("Determines device presence information by monitoring the last interaction date"
		+ "for the device and firing an event if too much time has elapsed.");
	builder.attribute((new AttributeNode.Builder("Check interval", "checkInterval", AttributeType.String)
		.description("Time duration (ISO8601 or \"1h 10m 30s\" format) that indicates amount of time to "
			+ "to wait between performing presence checks.")
		.defaultValue("10m").build()));
	builder.attribute(
		(new AttributeNode.Builder("Presence missing interval", "presenceMissingInterval", AttributeType.String)
			.description("Time duration (ISO8601 or \"2d 5h 10m\" format) that indicates amount of time to "
				+ "since last interaction with a device to consider it non-present.")
			.defaultValue("8h").build()));
	return builder.build();
    }
}