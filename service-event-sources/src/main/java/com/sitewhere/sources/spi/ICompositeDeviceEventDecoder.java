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

import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Device event decoder that delegates to one or more nested decoders.
 * 
 * @author Derek
 */
public interface ICompositeDeviceEventDecoder<T> extends IDeviceEventDecoder<T> {

    /**
     * Get metadata extractor implementation.
     * 
     * @return
     */
    public IMessageMetadataExtractor<T> getMetadataExtractor();

    /**
     * Get list of potential decoder choices.
     * 
     * @return
     */
    public List<IDecoderChoice<T>> getDecoderChoices();

    /**
     * Represents a potential choice for decoding a payload.
     * 
     * @author Derek
     */
    public static interface IDecoderChoice<T> extends ITenantEngineLifecycleComponent {

	/**
	 * Indicates if this choice applies for the given context.
	 * 
	 * @param criteria
	 * @return
	 */
	public boolean appliesTo(IDeviceContext<T> criteria);

	/**
	 * Get decoder for this choice.
	 * 
	 * @return
	 */
	public IDeviceEventDecoder<T> getDeviceEventDecoder();
    }

    /**
     * Provides metadata about a message, which is used to decide which decoder
     * to use.
     * 
     * @author Derek
     */
    public static interface IMessageMetadata<T> {

	/**
	 * Get parsed hardware id.
	 * 
	 * @return
	 */
	public String getHardwareId();

	/**
	 * Get parsed payload.
	 * 
	 * @return
	 */
	public T getPayload();
    }

    public static interface IDeviceContext<T> {

	/**
	 * Get referenced device.
	 * 
	 * @return
	 */
	public IDevice getDevice();

	/**
	 * Get referenced device specification.
	 * 
	 * @return
	 */
	public IDeviceSpecification getDeviceSpecification();

	/**
	 * Get payload to be parsed.
	 * 
	 * @return
	 */
	public T getPayload();
    }

    /**
     * Extracts device criteria from payload.
     * 
     * @author Derek
     *
     * @param <T>
     */
    public static interface IMessageMetadataExtractor<T> extends ITenantEngineLifecycleComponent {

	/**
	 * Extract metadata about payload. This metadata is used to determine
	 * context and choose which decoder should be used.
	 * 
	 * @param payload
	 * @param eventSourceMetadata
	 * @return
	 * @throws EventDecodeException
	 */
	public IMessageMetadata<T> extractMetadata(T payload, Map<String, Object> eventSourceMetadata)
		throws EventDecodeException;
    }
}