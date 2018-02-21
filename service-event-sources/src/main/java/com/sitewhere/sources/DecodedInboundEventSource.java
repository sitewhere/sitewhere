/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link InboundEventSource} where event receivers return
 * decoded events and the decoder just passes the events through without
 * changing them.
 * 
 * @author Derek
 */
public class DecodedInboundEventSource extends InboundEventSource<DecodedDeviceRequest<?>> {

    public DecodedInboundEventSource() {
	setDeviceEventDecoder(new NoOpDecoder());
    }

    /*
     * @see
     * com.sitewhere.spi.device.communication.IInboundEventSource#getRawPayload(
     * java.lang.Object)
     */
    @Override
    public byte[] getRawPayload(DecodedDeviceRequest<?> payload) {
	return new byte[0];
    }

    /**
     * Decoder that just returns the decoded events.
     * 
     * @author Derek
     */
    public static class NoOpDecoder extends TenantEngineLifecycleComponent
	    implements IDeviceEventDecoder<DecodedDeviceRequest<?>> {

	/** Static logger instance */
	private static Log LOGGER = LogFactory.getLog(NoOpDecoder.class);

	public NoOpDecoder() {
	    super(LifecycleComponentType.DeviceEventDecoder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(
	 * java.lang. Object, java.util.Map)
	 */
	@Override
	public List<IDecodedDeviceRequest<?>> decode(DecodedDeviceRequest<?> payload, Map<String, Object> metadata)
		throws EventDecodeException {
	    List<IDecodedDeviceRequest<?>> results = new ArrayList<IDecodedDeviceRequest<?>>();
	    results.add(payload);
	    return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start(com.
	 * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
	 */
	@Override
	public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop(com.
	 * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
	 */
	@Override
	public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Log getLogger() {
	    return LOGGER;
	}
    }
}