/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound.spi;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Allows intereseted entities to interact with SiteWhere outbound event
 * processing.
 * 
 * @author Derek
 */
public interface IOutboundEventProcessor extends ITenantLifecycleComponent {

    /**
     * Executes code after device measurements have been successfully saved.
     * 
     * @param measurements
     *            event information
     * @throws SiteWhereException
     *             if an error occurs in processing
     */
    public void onMeasurements(IDeviceMeasurements measurements) throws SiteWhereException;

    /**
     * Executes code after device location has been successfully saved.
     * 
     * @param location
     *            event information
     * @throws SiteWhereException
     *             if an error occurs in processing
     */
    public void onLocation(IDeviceLocation location) throws SiteWhereException;

    /**
     * Executes code after device alert has been successfully saved.
     * 
     * @param alert
     *            event information
     * @throws SiteWhereException
     *             if an error occurs in processing
     */
    public void onAlert(IDeviceAlert alert) throws SiteWhereException;

    /**
     * Executes code after device command invocation has been successfully
     * saved.
     * 
     * @param invocation
     *            event information
     * @throws SiteWhereException
     *             if an error occurs in processing
     */
    public void onCommandInvocation(IDeviceCommandInvocation invocation) throws SiteWhereException;

    /**
     * Executes code after device command response has been successfully saved.
     * 
     * @param response
     *            event information
     * @throws SiteWhereException
     *             if an error occurs in processing
     */
    public void onCommandResponse(IDeviceCommandResponse response) throws SiteWhereException;

    /**
     * Executes code after device state change has been successfully saved.
     * 
     * @param state
     *            event information
     * @throws SiteWhereException
     *             if an error occurs in processing
     */
    public void onStateChange(IDeviceStateChange state) throws SiteWhereException;
}