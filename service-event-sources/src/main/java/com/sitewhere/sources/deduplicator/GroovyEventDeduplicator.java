/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.deduplicator;

import com.sitewhere.groovy.IGroovyVariables;
import com.sitewhere.grpc.client.event.BlockingDeviceEventManagement;
import com.sitewhere.microservice.groovy.GroovyComponent;
import com.sitewhere.rest.model.device.event.request.scripting.DeviceEventRequestBuilder;
import com.sitewhere.rest.model.device.request.scripting.DeviceManagementRequestBuilder;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDeduplicator;
import com.sitewhere.sources.spi.microservice.IEventSourcesMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;

/**
 * Implementation of {@link IDeviceEventDeduplicator} that uses a Groovy script
 * to decide whether an event is a duplicate or not. The Groovy script should
 * return a boolean value.
 * 
 * @author Derek
 */
public class GroovyEventDeduplicator extends GroovyComponent implements IDeviceEventDeduplicator {

    public GroovyEventDeduplicator() {
	super(LifecycleComponentType.DeviceEventDeduplicator);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceEventDeduplicator#
     * isDuplicate(com.sitewhere.spi.device.communication.IDecodedDeviceRequest)
     */
    @Override
    public boolean isDuplicate(IDecodedDeviceRequest<?> request) throws SiteWhereException {
	try {
	    Binding binding = new Binding();
	    binding.setVariable(IGroovyVariables.VAR_DEVICE_MANAGEMENT_BUILDER,
		    new DeviceManagementRequestBuilder(getDeviceManagement()));
	    binding.setVariable(IGroovyVariables.VAR_EVENT_MANAGEMENT_BUILDER,
		    new DeviceEventRequestBuilder(getDeviceManagement(), getDeviceEventManagement()));
	    binding.setVariable(IGroovyVariables.VAR_DECODED_DEVICE_REQUEST, request);
	    binding.setVariable(IGroovyVariables.VAR_LOGGER, getLogger());
	    getLogger().debug("About to execute '" + getScriptId() + "' for event request: " + request);
	    Boolean isDuplicate = (Boolean) run(binding);
	    return isDuplicate;
	} catch (SiteWhereException e) {
	    throw new EventDecodeException("Unable to run deduplicator script.", e);
	}
    }

    private IDeviceManagement getDeviceManagement() {
	return ((IEventSourcesMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiDemux()
		.getApiChannel();
    }

    private IDeviceEventManagement getDeviceEventManagement() {
	return new BlockingDeviceEventManagement(((IEventSourcesMicroservice) getTenantEngine().getMicroservice())
		.getDeviceEventManagementApiDemux().getApiChannel());
    }
}