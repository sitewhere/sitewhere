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

import com.sitewhere.spi.microservice.multitenant.ITenantEngineConfiguration;

/**
 * Maps device registration YAML configuration to objects.
 */
public class DeviceRegistrationTenantConfiguration implements ITenantEngineConfiguration {

    /** Default setting for whether new devices are allowed */
    private static final boolean DEFAULT_ALLOW_NEW_DEVICES = false;

    /** Indicates whether new devices are allowed */
    private boolean allowNewDevices = DEFAULT_ALLOW_NEW_DEVICES;

    /** Assignment defaults */
    private AssignmentDefaults assignmentDefaults = new AssignmentDefaults();

    public boolean isAllowNewDevices() {
	return allowNewDevices;
    }

    public void setAllowNewDevices(boolean allowNewDevices) {
	this.allowNewDevices = allowNewDevices;
    }

    public AssignmentDefaults getAssignmentDefaults() {
	return assignmentDefaults;
    }

    public void setAssignmentDefaults(AssignmentDefaults assignmentDefaults) {
	this.assignmentDefaults = assignmentDefaults;
    }
}
