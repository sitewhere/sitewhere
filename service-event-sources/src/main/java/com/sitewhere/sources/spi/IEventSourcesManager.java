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

import com.sitewhere.sources.kafka.DecodedEventsProducer;
import com.sitewhere.sources.kafka.DeviceRegistrationEventsProducer;
import com.sitewhere.sources.kafka.FailedDecodeEventsProducer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Manages the list of event sources for a tenant.
 */
public interface IEventSourcesManager extends ITenantEngineLifecycleComponent {

    /**
     * Get list of inbound event sources.
     * 
     * @return
     */
    public List<IInboundEventSource<?>> getEventSources();

    /**
     * Get Kafka producer for decoded events.
     * 
     * @return
     */
    public DecodedEventsProducer getDecodedEventsProducer();

    /**
     * Get Kafka producer for events that could not be decoded.
     * 
     * @return
     */
    public FailedDecodeEventsProducer getFailedDecodeEventsProducer();

    /**
     * Get Kafka producer for device registation events.
     * 
     * @return
     */
    public DeviceRegistrationEventsProducer getDeviceRegistrationEventsProducer();

    /**
     * Handle processing for a decoded event from an event source.
     * 
     * @param sourceId
     * @param encoded
     * @param metadata
     * @param decoded
     * @throws SiteWhereException
     */
    public void handleDecodedEvent(String sourceId, byte[] encoded, Map<String, Object> metadata,
	    IDecodedDeviceRequest<?> decoded) throws SiteWhereException;

    /**
     * Handle failed decode from an event source.
     * 
     * @param sourceId
     * @param encoded
     * @param metadata
     * @param t
     * @throws SiteWhereException
     */
    public void handleFailedDecode(String sourceId, byte[] encoded, Map<String, Object> metadata, Throwable t)
	    throws SiteWhereException;
}