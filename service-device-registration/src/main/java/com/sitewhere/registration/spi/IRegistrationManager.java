/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration.spi;

import java.util.UUID;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceMappingCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.microservice.kafka.payload.IInboundEventPayload;
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
     * Get id of device type that will be used if no device type is provided in
     * request.
     * 
     * @return
     */
    public UUID getAutoAssignDeviceTypeId();

    /**
     * Get id of area that will be used if no area is provided in request.
     * 
     * @return
     */
    public UUID getAutoAssignAreaId();

    /**
     * Handle registration of a new device.
     * 
     * @param request
     * @throws SiteWhereException
     */
    public void handleDeviceRegistration(IDeviceRegistrationRequest request) throws SiteWhereException;

    /**
     * Handle event addressed to unknown device.
     * 
     * @param payload
     * @throws SiteWhereException
     */
    public void handleUnregisteredDeviceEvent(IInboundEventPayload payload) throws SiteWhereException;

    /**
     * Handle mapping of a device to a path on a composite device.
     * 
     * @param hardwareId
     * @param request
     * @throws SiteWhereException
     */
    public void handleDeviceMapping(String hardwareId, IDeviceMappingCreateRequest request) throws SiteWhereException;
}