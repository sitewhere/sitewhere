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
package com.sitewhere.sources.decoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sitewhere.microservice.api.device.DeviceManagementRequestBuilder;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.DeviceEventRequestBuilder;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.microservice.scripting.ScriptingComponent;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.sources.spi.microservice.IEventSourcesMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;

/**
 * Implementation of {@link IDeviceEventDecoder} that uses a script to decode a
 * binary payload.
 */
public class ScriptedEventDecoder extends ScriptingComponent<List<IDecodedDeviceRequest<?>>>
	implements IDeviceEventDecoder<byte[]> {

    public ScriptedEventDecoder() {
	super(LifecycleComponentType.DeviceEventDecoder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(java.
     * lang.Object, java.util.Map)
     */
    @Override
    public List<IDecodedDeviceRequest<?>> decode(byte[] payload, Map<String, Object> metadata)
	    throws EventDecodeException {
	try {
	    Binding binding = new Binding();
	    List<IDecodedDeviceRequest<?>> events = new ArrayList<IDecodedDeviceRequest<?>>();
	    binding.setVariable(IScriptVariables.VAR_DEVICE_MANAGEMENT_BUILDER,
		    new DeviceManagementRequestBuilder(getDeviceManagement()));
	    binding.setVariable(IScriptVariables.VAR_EVENT_MANAGEMENT_BUILDER,
		    new DeviceEventRequestBuilder(getDeviceManagement(), getDeviceEventManagement()));
	    binding.setVariable(IScriptVariables.VAR_DECODED_EVENTS, events);
	    binding.setVariable(IScriptVariables.VAR_PAYLOAD, payload);
	    binding.setVariable(IScriptVariables.VAR_PAYLOAD_METADATA, metadata);
	    binding.setVariable(IScriptVariables.VAR_LOGGER, getLogger());
	    return run(binding);
	} catch (SiteWhereException e) {
	    throw new EventDecodeException("Unable to execute event decoder script.", e);
	}
    }

    private IDeviceManagement getDeviceManagement() {
	return ((IEventSourcesMicroservice) getTenantEngine().getMicroservice()).getDeviceManagement();
    }

    private IDeviceEventManagement getDeviceEventManagement() {
	return ((IEventSourcesMicroservice) getTenantEngine().getMicroservice()).getDeviceEventManagementApiChannel();
    }
}