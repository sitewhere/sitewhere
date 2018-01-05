/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rules.processors.geospatial;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.rules.spi.IRuleProcessor;
import com.sitewhere.rules.spi.microservice.IRuleProcessingTenantEngine;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;

/**
 * Default implementation of {@link IRuleProcessor} that acts as a common base
 * class for other rule processors.
 * 
 * @author Derek
 */
public class RuleProcessor extends TenantEngineLifecycleComponent implements IRuleProcessor {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Owning tenant engine */
    private IRuleProcessingTenantEngine tenantEngine;

    /*
     * @see com.sitewhere.rules.spi.IRuleProcessor#onMeasurements(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurements)
     */
    @Override
    public void onMeasurements(IDeviceEventContext context, IDeviceMeasurements measurements)
	    throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.rules.spi.IRuleProcessor#onLocation(com.sitewhere.spi.device.
     * event.IDeviceEventContext, com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocation(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.rules.spi.IRuleProcessor#onAlert(com.sitewhere.spi.device.event
     * .IDeviceEventContext, com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlert(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.rules.spi.IRuleProcessor#onCommandInvocation(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void onCommandInvocation(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.rules.spi.IRuleProcessor#onCommandResponse(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public void onCommandResponse(IDeviceEventContext context, IDeviceCommandResponse response)
	    throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.rules.spi.IRuleProcessor#onStateChange(com.sitewhere.spi.device
     * .event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceStateChange)
     */
    @Override
    public void onStateChange(IDeviceEventContext context, IDeviceStateChange state) throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent#getTenantEngine
     * ()
     */
    @Override
    public IRuleProcessingTenantEngine getTenantEngine() {
	return tenantEngine;
    }

    /*
     * @see
     * com.sitewhere.rules.spi.IRuleProcessor#setTenantEngine(com.sitewhere.rules.
     * spi.microservice.IRuleProcessingTenantEngine)
     */
    @Override
    public void setTenantEngine(IRuleProcessingTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    /**
     * Get reference to device management implementation.
     * 
     * @return
     */
    public IDeviceManagement getDeviceManagement() {
	return null;
    }

    /**
     * Get reference to event management implementation.
     * 
     * @return
     */
    public IDeviceEventManagement getDeviceEventManagement() {
	return null;
    }
}