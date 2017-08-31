/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.registration.spi.IRegistrationManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceMappingCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;

/**
 * Inbound event processor that hands off registration requests to the
 * {@link IRegistrationManager} implementation.
 * 
 * @author Derek
 */
public class RegistrationProcessor {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Cached registration manager */
    private IRegistrationManager registration;

    public void onRegistrationRequest(String hardwareId, String originator, IDeviceRegistrationRequest request)
	    throws SiteWhereException {
	getRegistrationManager().handleDeviceRegistration(request);
    }

    public void onDeviceMappingCreateRequest(String hardwareId, String originator, IDeviceMappingCreateRequest request)
	    throws SiteWhereException {
	getRegistrationManager().handleDeviceMapping(hardwareId, request);
    }

    public Logger getLogger() {
	return LOGGER;
    }

    /**
     * Cache the registration manager implementation rather than looking it up
     * each time.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IRegistrationManager getRegistrationManager() throws SiteWhereException {
	return registration;
    }
}