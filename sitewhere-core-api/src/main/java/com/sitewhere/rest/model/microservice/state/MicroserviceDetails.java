/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.state;

import com.sitewhere.spi.microservice.state.IMicroserviceDetails;

/**
 * Information that identifies and describes a microservice.
 */
public class MicroserviceDetails implements IMicroserviceDetails {

    /** Microservice indentifier */
    private String identifier;

    /** Unique hostname */
    private String hostname;

    /** Display name */
    private String name;

    /** Icon */
    private String icon;

    /** Description */
    private String description;

    /** Global flag */
    private boolean global;

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IMicroserviceDetails#getIdentifier()
     */
    @Override
    public String getIdentifier() {
	return identifier;
    }

    public void setIdentifier(String identifier) {
	this.identifier = identifier;
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IMicroserviceDetails#getHostname()
     */
    @Override
    public String getHostname() {
	return hostname;
    }

    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IMicroserviceDetails#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IMicroserviceDetails#getIcon()
     */
    @Override
    public String getIcon() {
	return icon;
    }

    public void setIcon(String icon) {
	this.icon = icon;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IMicroserviceDetails#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IMicroserviceDetails#isGlobal()
     */
    @Override
    public boolean isGlobal() {
	return global;
    }

    public void setGlobal(boolean global) {
	this.global = global;
    }
}