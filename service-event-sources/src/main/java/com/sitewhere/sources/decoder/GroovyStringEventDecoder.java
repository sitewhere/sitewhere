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

import com.sitewhere.microservice.groovy.GroovyComponent;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.groovy.IGroovyVariables;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;

/**
 * Implementation of {@link IDeviceEventDecoder} that delegates parsing of a
 * String payload to a Groovy script.
 */
public class GroovyStringEventDecoder extends GroovyComponent implements IDeviceEventDecoder<String> {

    public GroovyStringEventDecoder() {
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
    public List<IDecodedDeviceRequest<?>> decode(String payload, Map<String, Object> metadata)
	    throws EventDecodeException {
	try {
	    Binding binding = createBindingFor(this);
	    List<IDecodedDeviceRequest<?>> events = new ArrayList<IDecodedDeviceRequest<?>>();
	    binding.setVariable(IGroovyVariables.VAR_DECODED_EVENTS, events);
	    binding.setVariable(IGroovyVariables.VAR_PAYLOAD, payload);
	    getLogger().debug("About to execute '" + getScriptId() + "' with payload: " + payload);
	    run(binding);
	    return (List<IDecodedDeviceRequest<?>>) binding.getVariable(IGroovyVariables.VAR_DECODED_EVENTS);
	} catch (SiteWhereException e) {
	    throw new EventDecodeException("Unable to run decoder script.", e);
	}
    }
}