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
package com.sitewhere.event.spi.microservice;

import com.sitewhere.event.configuration.EventManagementTenantConfiguration;
import com.sitewhere.event.spi.kafka.IEventPersistencePipeline;
import com.sitewhere.event.spi.kafka.IOutboundCommandInvocationsProducer;
import com.sitewhere.event.spi.kafka.IOutboundEventsProducer;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Extends {@link IMicroserviceTenantEngine} with features specific to device
 * event management.
 */
public interface IEventManagementTenantEngine extends IMicroserviceTenantEngine<EventManagementTenantConfiguration> {

    /**
     * Get associated event management implementation.
     * 
     * @return
     */
    public IDeviceEventManagement getEventManagement();

    /**
     * Get implementation class that wraps event management with GRPC conversions.
     * 
     * @return
     */
    public DeviceEventManagementGrpc.DeviceEventManagementImplBase getEventManagementImpl();

    /**
     * Get Kafka Streams pipeline for events prepared by inbound processing logic.
     * 
     * @return
     */
    public IEventPersistencePipeline getPreprocessedEventsPipeline();

    /**
     * Get Kafka producer that sends enriched, persisted events to a topic.
     * 
     * @return
     */
    public IOutboundEventsProducer getOutboundEventsProducer();

    /**
     * Get Kafka producer that sends enriched, persisted command invocations to a
     * topic.
     * 
     * @return
     */
    public IOutboundCommandInvocationsProducer getOutboundCommandInvocationsProducer();
}