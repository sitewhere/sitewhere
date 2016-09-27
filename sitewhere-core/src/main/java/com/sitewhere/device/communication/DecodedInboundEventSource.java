/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.rest.model.device.communication.DecodedDeviceRequest;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.EventDecodeException;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.communication.IDeviceEventDecoder;
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

    /**
     * Decoder that just returns the decoded events.
     * 
     * @author Derek
     */
    public static class NoOpDecoder extends TenantLifecycleComponent
	    implements IDeviceEventDecoder<DecodedDeviceRequest<?>> {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	public NoOpDecoder() {
	    super(LifecycleComponentType.DeviceEventDecoder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(
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
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
	    return LOGGER;
	}
    }
}