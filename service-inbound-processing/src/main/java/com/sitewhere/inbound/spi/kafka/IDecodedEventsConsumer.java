/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.spi.kafka;

import com.sitewhere.inbound.spi.processing.IInboundPayloadProcessingLogic;
import com.sitewhere.inbound.spi.processing.IInboundProcessingConfiguration;
import com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer;

/**
 * Kafka consumer for inbound decoded events that need to be processed.
 */
public interface IDecodedEventsConsumer extends IMicroserviceKafkaConsumer {

    /**
     * Get configuration options for inbound processing.
     * 
     * @return
     */
    public IInboundProcessingConfiguration getInboundProcessingConfiguration();

    /**
     * Get processing logic component used for inbound event payloads.
     * 
     * @return
     */
    public IInboundPayloadProcessingLogic getInboundPayloadProcessingLogic();
}