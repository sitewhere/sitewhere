/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.decoder.composite;

import java.util.Map;

import com.sitewhere.groovy.IGroovyVariables;
import com.sitewhere.microservice.groovy.GroovyComponent;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.ICompositeDeviceEventDecoder.IMessageMetadata;
import com.sitewhere.sources.spi.ICompositeDeviceEventDecoder.IMessageMetadataExtractor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.tenant.ITenant;

import groovy.lang.Binding;

/**
 * Implements {@link IMessageMetadataExtractor} by using a Groovy script to
 * extract message metadata from a binary payload.
 * 
 * @author Derek
 */
public class GroovyMessageMetadataExtractor extends GroovyComponent implements IMessageMetadataExtractor<byte[]> {

    public GroovyMessageMetadataExtractor() {
	super(LifecycleComponentType.Other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder.
     * IMessageMetadataExtractor#extractMetadata(java.lang.Object, java.util.Map)
     */
    @Override
    @SuppressWarnings("unchecked")
    public IMessageMetadata<byte[]> extractMetadata(byte[] payload, Map<String, Object> eventSourceMetadata)
	    throws EventDecodeException {
	try {
	    Binding binding = new Binding();
	    binding.setVariable(IGroovyVariables.VAR_DEVICE_MANAGEMENT,
		    getDeviceManagement(getTenantEngine().getTenant()));
	    binding.setVariable(IGroovyVariables.VAR_PAYLOAD, payload);
	    binding.setVariable(IGroovyVariables.VAR_PAYLOAD_METADATA, eventSourceMetadata);
	    binding.setVariable(IGroovyVariables.VAR_LOGGER, getLogger());
	    getLogger().debug("About to execute '" + getScriptId() + "' with payload: " + payload);
	    return (IMessageMetadata<byte[]>) run(binding);
	} catch (SiteWhereException e) {
	    throw new EventDecodeException("Unable to run metadata extractor.", e);
	}
    }

    private IDeviceManagement getDeviceManagement(ITenant tenant) {
	return null;
    }
}