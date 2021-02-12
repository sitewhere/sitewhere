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
package com.sitewhere.sources.spi;

import java.util.List;
import java.util.Map;

import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Device event decoder that delegates to one or more nested decoders.
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
     * Provides metadata about a message, which is used to decide which decoder to
     * use.
     */
    public static interface IMessageMetadata<T> {

	/**
	 * Get parsed device token.
	 * 
	 * @return
	 */
	public String getDeviceToken();

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
	 * Get referenced device type.
	 * 
	 * @return
	 */
	public IDeviceType getDeviceType();

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
     * @param <T>
     */
    public static interface IMessageMetadataExtractor<T> extends ITenantEngineLifecycleComponent {

	/**
	 * Extract metadata about payload. This metadata is used to determine context
	 * and choose which decoder should be used.
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