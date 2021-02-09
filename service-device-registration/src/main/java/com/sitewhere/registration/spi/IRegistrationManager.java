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
package com.sitewhere.registration.spi;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.kafka.IDecodedEventPayload;
import com.sitewhere.spi.device.event.kafka.IDeviceRegistrationPayload;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Manages how devices are dynamically registered into the system.
 */
public interface IRegistrationManager extends ITenantEngineLifecycleComponent {

    /**
     * Indicates whether the registration manager allows new devices to be created
     * as a byproduct of the registration process.
     * 
     * @return
     */
    public boolean isAllowNewDevices();

    /**
     * Indicates whether a default device type is used if information is not passed
     * in the registration request.
     * 
     * @return
     */
    public boolean isUseDefaultDeviceType();

    /**
     * Get token of device type that will be used if no device type is provided in
     * request.
     * 
     * @return
     */
    public String getDefaultDeviceTypeToken();

    /**
     * Indicates whether a default customer is used when creating a device
     * assignment for an unassigned device.
     * 
     * @return
     */
    public boolean isUseDefaultCustomer();

    /**
     * Get token of customer that will be used if no customer is provided in
     * request.
     * 
     * @return
     */
    public String getDefaultCustomerToken();

    /**
     * Indicates whether a default area is used when creating a device assignment
     * for an unassigned device.
     * 
     * @return
     */
    public boolean isUseDefaultArea();

    /**
     * Get token of area that will be used if no area is provided in request.
     * 
     * @return
     */
    public String getDefaultAreaToken();

    /**
     * Indicates whether a default asset is used when creating a device assignment
     * for an unassigned device.
     * 
     * @return
     */
    public boolean isUseDefaultAsset();

    /**
     * Get token of asset that will be used if no area is provided in request.
     * 
     * @return
     */
    public String getDefaultAssetToken();

    /**
     * Handle registration of a new device.
     * 
     * @param request
     * @throws SiteWhereException
     */
    public void handleDeviceRegistration(IDeviceRegistrationPayload request) throws SiteWhereException;

    /**
     * Handle event addressed to unknown device.
     * 
     * @param payload
     * @throws SiteWhereException
     */
    public void handleUnregisteredDeviceEvent(IDecodedEventPayload payload) throws SiteWhereException;
}