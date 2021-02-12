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