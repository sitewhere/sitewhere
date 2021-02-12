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

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.microservice.scripting.ScriptingComponent;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;

/**
 * Implementation of {@link IDeviceEventDecoder} that delegates parsing of a
 * JsonNode payload to a script.
 */
public class ScriptedJsonDecoder extends ScriptingComponent<List<IDecodedDeviceRequest<?>>>
	implements IDeviceEventDecoder<JsonNode> {

    public ScriptedJsonDecoder() {
	super(LifecycleComponentType.DeviceEventDecoder);
    }

    /*
     * @see com.sitewhere.sources.spi.IDeviceEventDecoder#decode(java.lang.Object,
     * java.util.Map)
     */
    @Override
    public List<IDecodedDeviceRequest<?>> decode(JsonNode payload, Map<String, Object> metadata)
	    throws EventDecodeException {
	try {
	    Binding binding = createBindingFor(this);
	    List<IDecodedDeviceRequest<?>> events = new ArrayList<IDecodedDeviceRequest<?>>();
	    binding.setVariable(IScriptVariables.VAR_DECODED_EVENTS, events);
	    binding.setVariable(IScriptVariables.VAR_PAYLOAD, payload);
	    return run(binding);
	} catch (SiteWhereException e) {
	    throw new EventDecodeException("Unable to run JSON decoder script.", e);
	}
    }
}