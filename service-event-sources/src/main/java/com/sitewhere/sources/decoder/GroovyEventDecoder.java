/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.decoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sitewhere.groovy.IGroovyVariables;
import com.sitewhere.grpc.client.event.BlockingDeviceEventManagement;
import com.sitewhere.microservice.groovy.GroovyComponent;
import com.sitewhere.rest.model.device.event.request.scripting.DeviceEventRequestBuilder;
import com.sitewhere.rest.model.device.request.scripting.DeviceManagementRequestBuilder;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.sources.spi.microservice.IEventSourcesMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;

/**
 * Implementation of {@link IDeviceEventDecoder} that uses a Groovy script to
 * decode a binary payload.
 * 
 * @author Derek
 */
public class GroovyEventDecoder extends GroovyComponent implements IDeviceEventDecoder<byte[]> {

    public GroovyEventDecoder() {
	super(LifecycleComponentType.DeviceEventDecoder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(java.
     * lang.Object, java.util.Map)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<IDecodedDeviceRequest<?>> decode(byte[] payload, Map<String, Object> metadata)
	    throws EventDecodeException {
	try {
	    Binding binding = createBindingFor(this);
	    List<IDecodedDeviceRequest<?>> events = new ArrayList<IDecodedDeviceRequest<?>>();
	    binding.setVariable(IGroovyVariables.VAR_DEVICE_MANAGEMENT_BUILDER,
		    new DeviceManagementRequestBuilder(getDeviceManagement()));
	    binding.setVariable(IGroovyVariables.VAR_EVENT_MANAGEMENT_BUILDER,
		    new DeviceEventRequestBuilder(getDeviceManagement(), getDeviceEventManagement()));
	    binding.setVariable(IGroovyVariables.VAR_DECODED_EVENTS, events);
	    binding.setVariable(IGroovyVariables.VAR_PAYLOAD, payload);
	    binding.setVariable(IGroovyVariables.VAR_PAYLOAD_METADATA, metadata);
	    getLogger().debug("About to execute '" + getScriptId() + "' with payload: " + payload);
	    run(binding);
	    return (List<IDecodedDeviceRequest<?>>) binding.getVariable(IGroovyVariables.VAR_DECODED_EVENTS);
	} catch (SiteWhereException e) {
	    throw new EventDecodeException("Unable to execute event decoder script.", e);
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