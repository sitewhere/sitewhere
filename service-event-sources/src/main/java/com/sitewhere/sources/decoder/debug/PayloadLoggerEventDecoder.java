/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.decoder.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sitewhere.core.DataUtils;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.spi.EventDecodeException;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IDeviceEventDecoder} that logs the event payload but
 * does not actually produce any events. This is useful for debugging when
 * implementing decoders for hardware sending human-readable commands across the
 * wire.
 * 
 * @author Derek
 */
public class PayloadLoggerEventDecoder extends TenantEngineLifecycleComponent implements IDeviceEventDecoder<byte[]> {

    public PayloadLoggerEventDecoder() {
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
	getLogger().info("=== EVENT DATA BEGIN ===");
	getLogger().info(new String(payload));
	getLogger().info("(hex) " + DataUtils.bytesToHex(payload));
	getLogger().info("=== EVENT DATA END ===");
	return new ArrayList<IDecodedDeviceRequest<?>>();
    }
}