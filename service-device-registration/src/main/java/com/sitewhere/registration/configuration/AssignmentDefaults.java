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

    /** Id of device type that will be used if none provided */
    private String defaultDeviceTypeToken = null;

    /** Token of customer that will be used by default in assignment */
    private String defaultCustomerToken = null;

    /** Token of area that will be used by default in assignment */
    private String defaultAreaToken = null;

    /** Token of asset that will be used by default in assignment */
    private String defaultAssetToken = null;

    public String getDefaultDeviceTypeToken() {
	return defaultDeviceTypeToken;
    }

    public void setDefaultDeviceTypeToken(String defaultDeviceTypeToken) {
	this.defaultDeviceTypeToken = defaultDeviceTypeToken;
    }

    public String getDefaultCustomerToken() {
	return defaultCustomerToken;
    }

    public void setDefaultCustomerToken(String defaultCustomerToken) {
	this.defaultCustomerToken = defaultCustomerToken;
    }

    public String getDefaultAreaToken() {
	return defaultAreaToken;
    }

    public void setDefaultAreaToken(String defaultAreaToken) {
	this.defaultAreaToken = defaultAreaToken;
    }

    public String getDefaultAssetToken() {
	return defaultAssetToken;
    }

    public void setDefaultAssetToken(String defaultAssetToken) {
	this.defaultAssetToken = defaultAssetToken;
    }
}
