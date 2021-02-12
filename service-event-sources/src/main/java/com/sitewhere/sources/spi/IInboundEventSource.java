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
 * Entity that receives events from one or more {@link IInboundEventReceiver},
 * decodes them, and forwards them for processing.
 */
public interface IInboundEventSource<T> extends ITenantEngineLifecycleComponent {

    /**
     * Get unique id for event source.
     * 
     * @return
     */
    public String getSourceId();

    /**
     * Get device event decoder implementation.
     * 
     * @return
     */
    public IDeviceEventDecoder<T> getDeviceEventDecoder();

    /**
     * Get device event deduplicator implementation.
     * 
     * @return
     */
    public IDeviceEventDeduplicator getDeviceEventDeduplicator();

    /**
     * Get list of inbound event receivers.
     * 
     * @return
     */
    public List<IInboundEventReceiver<T>> getInboundEventReceivers();

    /**
     * Get the raw payload as a byte array.
     * 
     * @param payload
     * @return
     */
    public byte[] getRawPayload(T payload);

    /**
     * Called by {@link IInboundEventReceiver} when an encoded event is received.
     * 
     * @param receiver
     * @param encodedEvent
     * @param metadata
     */
    public void onEncodedEventReceived(IInboundEventReceiver<T> receiver, T encodedEvent, Map<String, Object> metadata);
}