/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.decoder.composite;

import java.util.Map;

import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.microservice.scripting.ScriptingComponent;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.ICompositeDeviceEventDecoder.IMessageMetadata;
import com.sitewhere.sources.spi.ICompositeDeviceEventDecoder.IMessageMetadataExtractor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;
import com.sitewhere.spi.microservice.scripting.ScriptScope;
import com.sitewhere.spi.microservice.scripting.ScriptType;
import com.sitewhere.spi.tenant.ITenant;

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
	    binding.setVariable(IScriptVariables.VAR_DEVICE_MANAGEMENT,
		    getDeviceManagement(getTenantEngine().getTenant()));
	    binding.setVariable(IScriptVariables.VAR_PAYLOAD, payload);
	    binding.setVariable(IScriptVariables.VAR_PAYLOAD_METADATA, eventSourceMetadata);
	    return (IMessageMetadata<byte[]>) run(ScriptScope.TenantEngine, ScriptType.Managed, binding);
	} catch (SiteWhereException e) {
	    throw new EventDecodeException("Unable to run metadata extractor.", e);
	}
    }

    private IDeviceManagement getDeviceManagement(ITenant tenant) {
	return null;
    }
}