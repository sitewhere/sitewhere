/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
