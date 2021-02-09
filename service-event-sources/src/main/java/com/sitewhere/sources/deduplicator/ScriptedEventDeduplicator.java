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
package com.sitewhere.sources.deduplicator;

import com.sitewhere.microservice.api.device.DeviceManagementRequestBuilder;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.DeviceEventRequestBuilder;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.microservice.scripting.ScriptingComponent;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDeduplicator;
import com.sitewhere.sources.spi.microservice.IEventSourcesMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;

/**
 * Implementation of {@link IDeviceEventDeduplicator} that uses a script to
 * decide whether an event is a duplicate or not. The script should return a
 * boolean value.
 */
public class ScriptedEventDeduplicator extends ScriptingComponent<Boolean> implements IDeviceEventDeduplicator {

    public ScriptedEventDeduplicator() {
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
	    binding.setVariable(IScriptVariables.VAR_DEVICE_MANAGEMENT_BUILDER,
		    new DeviceManagementRequestBuilder(getDeviceManagement()));
	    binding.setVariable(IScriptVariables.VAR_EVENT_MANAGEMENT_BUILDER,
		    new DeviceEventRequestBuilder(getDeviceManagement(), getDeviceEventManagement()));
	    binding.setVariable(IScriptVariables.VAR_DECODED_DEVICE_REQUEST, request);
	    binding.setVariable(IScriptVariables.VAR_LOGGER, getLogger());
	    return run(binding);
	} catch (SiteWhereException e) {
	    throw new EventDecodeException("Unable to run deduplicator script.", e);
	}
    }

    private IDeviceManagement getDeviceManagement() {
	return ((IEventSourcesMicroservice) getTenantEngine().getMicroservice()).getDeviceManagement();
    }

    private IDeviceEventManagement getDeviceEventManagement() {
	return ((IEventSourcesMicroservice) getTenantEngine().getMicroservice()).getDeviceEventManagementApiChannel();
    }
}