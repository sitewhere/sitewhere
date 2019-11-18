/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.spi;

import java.util.List;
import java.util.Map;

import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Decodes inbound device event messages.
 */
public interface IDeviceEventDecoder<T> extends ITenantEngineLifecycleComponent {

    /** Metadata field used to pass device into nested decoders */
    public static final String META_DEVICE = "com.sitewhere.spi.device.communication.IDeviceEventDecoder:device";

    /** Metadata field used to pass device type into nested decoders */
    public static final String META_DEVICE_TYPE = "com.sitewhere.spi.device.communication.IDeviceEventDecoder:deviceType";

    /**
     * Decodes a payload into one or more {@link IDecodedDeviceRequest} objects.
     * 
     * @param payload
     *                     the payload that will be decoded
     * @param metadata
     *                     extra information associated with the payload
     * @return a list of decoded device requests to be processed
     * @throws EventDecodeException
     *                                  if the payload can not be decoded
     */
    public List<IDecodedDeviceRequest<?>> decode(T payload, Map<String, Object> metadata) throws EventDecodeException;
}