/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration.configuration;

/**
 * Default settings for device assignments in auto-created devices.
 */
public class AssignmentDefaults {

    /** Indicates if a default device type is used if not provided in request */
    private boolean useDefaultDeviceType = false;

    /** Id of device type that will be used if none provided */
    private String defaultDeviceTypeToken = null;

    /** Indicates if a default customer used if not provided in request */
    private boolean useDefaultCustomer = false;

    /** Token of customer that will be used by default in assignment */
    private String defaultCustomerToken = null;

    /** Indicates if a default area used if not provided in request */
    private boolean useDefaultArea = false;

    /** Token of area that will be used by default in assignment */
    private String defaultAreaToken = null;

    public boolean isUseDefaultDeviceType() {
	return useDefaultDeviceType;
    }

    public void setUseDefaultDeviceType(boolean useDefaultDeviceType) {
	this.useDefaultDeviceType = useDefaultDeviceType;
    }

    public String getDefaultDeviceTypeToken() {
	return defaultDeviceTypeToken;
    }

    public void setDefaultDeviceTypeToken(String defaultDeviceTypeToken) {
	this.defaultDeviceTypeToken = defaultDeviceTypeToken;
    }

    public boolean isUseDefaultCustomer() {
	return useDefaultCustomer;
    }

    public void setUseDefaultCustomer(boolean useDefaultCustomer) {
	this.useDefaultCustomer = useDefaultCustomer;
    }

    public String getDefaultCustomerToken() {
	return defaultCustomerToken;
    }

    public void setDefaultCustomerToken(String defaultCustomerToken) {
	this.defaultCustomerToken = defaultCustomerToken;
    }

    public boolean isUseDefaultArea() {
	return useDefaultArea;
    }

    public void setUseDefaultArea(boolean useDefaultArea) {
	this.useDefaultArea = useDefaultArea;
    }

    public String getDefaultAreaToken() {
	return defaultAreaToken;
    }

    public void setDefaultAreaToken(String defaultAreaToken) {
	this.defaultAreaToken = defaultAreaToken;
    }
}
