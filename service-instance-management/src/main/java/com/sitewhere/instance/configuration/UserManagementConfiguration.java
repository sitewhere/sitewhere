/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.configuration;

/**
 * Configuration settings related to user management.
 */
public class UserManagementConfiguration {

    /** Keycloak service name */
    private String serviceName = "sitewhere-keycloak-http";

    /** Keycloak API port */
    private int apiPort = 80;

    /** Master realm username */
    private String masterRealmUsername = "sitewhere";

    /** Master realm username */
    private String masterRealmPassword = "sitewhere";

    public String getServiceName() {
	return serviceName;
    }

    public void setServiceName(String serviceName) {
	this.serviceName = serviceName;
    }

    public int getApiPort() {
	return apiPort;
    }

    public void setApiPort(int apiPort) {
	this.apiPort = apiPort;
    }

    public String getMasterRealmUsername() {
	return masterRealmUsername;
    }

    public void setMasterRealmUsername(String masterRealmUsername) {
	this.masterRealmUsername = masterRealmUsername;
    }

    public String getMasterRealmPassword() {
	return masterRealmPassword;
    }

    public void setMasterRealmPassword(String masterRealmPassword) {
	this.masterRealmPassword = masterRealmPassword;
    }
}
