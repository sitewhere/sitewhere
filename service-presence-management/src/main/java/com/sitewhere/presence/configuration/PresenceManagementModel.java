/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.presence.configuration;

import com.sitewhere.configuration.model.DependencyResolvingConfigurationModel;
import com.sitewhere.configuration.old.IDeviceServicesParser;
import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;

/**
 * Configuration model for presence management microservice.
 * 
 * @author Derek
 */
public class PresenceManagementModel extends DependencyResolvingConfigurationModel {

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IConfigurationModel#
     * getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/presence-management";
    }

    /*
     * @see com.sitewhere.configuration.model.DependencyResolvingConfigurationModel#
     * getRootRole()
     */
    @Override
    public IConfigurationRoleProvider getRootRole() {
	return PresenceManagementRoles.PresenceManagement;
    }

    /*
     * @see
     * com.sitewhere.configuration.model.MicroserviceConfigurationModel#addElements(
     * )
     */
    @Override
    public void addElements() {
	addElement(createDefaultPresenceManagerElement());
    }

    /**
     * Create element configuration for default presence manager.
     * 
     * @return
     */
    protected ElementNode createDefaultPresenceManagerElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Default Presence Manager",
		IDeviceServicesParser.Elements.DefaultPresenceManager.getLocalName(), "bullseye",
		PresenceManagementRoleKeys.PresenceManager);

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