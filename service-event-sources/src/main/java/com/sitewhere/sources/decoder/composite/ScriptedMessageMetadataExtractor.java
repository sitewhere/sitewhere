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
package com.sitewhere.sources.decoder.composite;

import java.util.Map;

import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.microservice.scripting.ScriptingComponent;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.ICompositeDeviceEventDecoder.IMessageMetadata;
import com.sitewhere.sources.spi.ICompositeDeviceEventDecoder.IMessageMetadataExtractor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;

/**
 * Implements {@link IMessageMetadataExtractor} by using a script to extract
 * message metadata from a binary payload.
 */
public class ScriptedMessageMetadataExtractor extends ScriptingComponent<IMessageMetadata<byte[]>>
	implements IMessageMetadataExtractor<byte[]> {

    public ScriptedMessageMetadataExtractor() {
	super(LifecycleComponentType.Other);
    }

    /*
     * @see com.sitewhere.sources.spi.ICompositeDeviceEventDecoder.
     * IMessageMetadataExtractor#extractMetadata(java.lang.Object, java.util.Map)
     */
    @Override
    public IMessageMetadata<byte[]> extractMetadata(byte[] payload, Map<String, Object> eventSourceMetadata)
	    throws EventDecodeException {
	try {
	    Binding binding = createBindingFor(this);
	    // binding.setVariable(IScriptVariables.VAR_DEVICE_MANAGEMENT,
	    // getDeviceManagement());
	    binding.setVariable(IScriptVariables.VAR_PAYLOAD, payload);
	    binding.setVariable(IScriptVariables.VAR_PAYLOAD_METADATA, eventSourceMetadata);
	    return (IMessageMetadata<byte[]>) run(binding);
	} catch (SiteWhereException e) {
	    throw new EventDecodeException("Unable to run metadata extractor.", e);
	}
    }
}