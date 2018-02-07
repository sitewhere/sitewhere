/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.decoder.composite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.ICompositeDeviceEventDecoder;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Basic logic for a composite decoder. Extracts metadata from initial payload,
 * looks up device management data to build a context, then loops through a list
 * of potential decoders, returning the first one that applies to the context.
 * 
 * @author Derek
 *
 * @param <T>
 */
public abstract class CompositeDeviceEventDecoder<T> extends TenantEngineLifecycleComponent
	implements ICompositeDeviceEventDecoder<T> {

    public CompositeDeviceEventDecoder() {
	super(LifecycleComponentType.DeviceEventDecoder);
    }

    /**
     * Build an {@link IDeviceContext} based on metadata parsed from the initial
     * payload.
     * 
     * @param metadata
     * @return
     * @throws SiteWhereException
     */
    public abstract IDeviceContext<T> buildContext(IMessageMetadata<T> metadata) throws SiteWhereException;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(java.
     * lang.Object, java.util.Map)
     */
    @Override
    public List<IDecodedDeviceRequest<?>> decode(T payload, Map<String, Object> eventSourceMetadata)
	    throws EventDecodeException {

	// Parse metadata from payload.
	IMessageMetadata<T> metadata = getMetadataExtractor().extractMetadata(payload, eventSourceMetadata);
	getLogger().info("Extracted payload metadata: HardwareId: " + metadata.getHardwareId() + " Payload: "
		+ metadata.getPayload().toString());

	try {
	    IDeviceContext<T> context = buildContext(metadata);
	    getLogger().debug("Built context: Device: " + context.getDevice().getHardwareId() + " Device type: "
		    + context.getDeviceType().getToken());

	    // Add context metadata to event source metadata.
	    Map<String, Object> combined = new HashMap<String, Object>();
	    if (eventSourceMetadata != null) {
		combined.putAll(eventSourceMetadata);
	    }
	    combined.put(IDeviceEventDecoder.META_DEVICE, context.getDevice());
	    combined.put(IDeviceEventDecoder.META_DEVICE_TYPE, context.getDeviceType());

	    // Loop through choices and use first one that applies.
	    for (ICompositeDeviceEventDecoder.IDecoderChoice<T> choice : getDecoderChoices()) {

		if (choice.appliesTo(context)) {
		    return choice.getDeviceEventDecoder().decode(context.getPayload(), combined);
		}
	    }
	} catch (SiteWhereException e) {
	    throw new EventDecodeException("Exception in composite decoder.", e);
	} catch (Throwable t) {
	    throw new EventDecodeException("Unhandled exception in composite decoder.", t);
	}

	// Handle case where no choices apply.
	return new ArrayList<IDecodedDeviceRequest<?>>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getMetadataExtractor() == null) {
	    throw new SiteWhereException("Composite decoder has no metadata extractor configured.");
	}
	if (getDecoderChoices().size() < 1) {
	    throw new SiteWhereException("Composite decoder has no chocies configured.");
	}

	startNestedComponent(getMetadataExtractor(), monitor, true);
	for (ICompositeDeviceEventDecoder.IDecoderChoice<T> choice : getDecoderChoices()) {
	    startNestedComponent(choice, monitor, true);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getMetadataExtractor() != null) {
	    getMetadataExtractor().lifecycleStop(monitor);
	}
	for (ICompositeDeviceEventDecoder.IDecoderChoice<T> choice : getDecoderChoices()) {
	    choice.lifecycleStop(monitor);
	}
    }
}