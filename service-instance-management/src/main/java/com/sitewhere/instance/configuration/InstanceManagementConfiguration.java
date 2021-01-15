/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.spi.microservice.IMicroserviceConfiguration;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Maps instance management microservice YAML configuration to objects.
 */
@RegisterForReflection
public class InstanceManagementConfiguration implements IMicroserviceConfiguration {

    /** User management configuration */
    private JsonNode userManagement;

    public JsonNode getUserManagement() {
	return userManagement;
    }

    public void setUserManagement(JsonNode userManagement) {
	this.userManagement = userManagement;
    }
}
