/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration.spi;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.kafka.IDeviceRegistrationPayload;
import com.sitewhere.spi.device.event.kafka.IInboundEventPayload;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Manages how devices are dynamically registered into the system.
 * 
 * @author Derek
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
    public void handleUnregisteredDeviceEvent(IInboundEventPayload payload) throws SiteWhereException;
}