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

    /** Syncope hostname */
    private String syncopeHost;

    /** Syncope port */
    private int syncopePort;

    public String getSyncopeHost() {
	return syncopeHost;
    }

    public void setSyncopeHost(String syncopeHost) {
	this.syncopeHost = syncopeHost;
    }

    public int getSyncopePort() {
	return syncopePort;
    }

    public void setSyncopePort(int syncopePort) {
	this.syncopePort = syncopePort;
    }
}
