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

import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.microservice.scripting.ScriptingComponent;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;
import com.sitewhere.spi.microservice.scripting.ScriptScope;
import com.sitewhere.spi.microservice.scripting.ScriptType;

/**
 * Implementation of {@link IDeviceEventDecoder} that delegates parsing of a
 * String payload to a script.
 */
public class ScriptedStringEventDecoder extends ScriptingComponent<List<IDecodedDeviceRequest<?>>>
	implements IDeviceEventDecoder<String> {

    public ScriptedStringEventDecoder() {
	super(LifecycleComponentType.DeviceEventDecoder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(java.
     * lang.Object, java.util.Map)
     */
    @Override
    public List<IDecodedDeviceRequest<?>> decode(String payload, Map<String, Object> metadata)
	    throws EventDecodeException {
	try {
	    Binding binding = createBindingFor(this);
	    List<IDecodedDeviceRequest<?>> events = new ArrayList<IDecodedDeviceRequest<?>>();
	    binding.setVariable(IScriptVariables.VAR_DECODED_EVENTS, events);
	    binding.setVariable(IScriptVariables.VAR_PAYLOAD, payload);
	    return run(ScriptScope.TenantEngine, ScriptType.Managed, binding);
	} catch (SiteWhereException e) {
	    throw new EventDecodeException("Unable to run decoder script.", e);
	}
    }
}