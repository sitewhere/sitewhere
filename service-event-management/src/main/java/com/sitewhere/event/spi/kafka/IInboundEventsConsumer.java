/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.spi.kafka;

import com.sitewhere.event.spi.processing.IEventManagementConfiguration;
import com.sitewhere.event.spi.processing.IEventManagementStoreLogic;
import com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer;

/**
 * Kafka consumer for events which have already completed inbound processing.
 */
public interface IInboundEventsConsumer extends IMicroserviceKafkaConsumer {
    /**
     * Get configuration options for inbound processing.
     *
     * @return
     */
    public IEventManagementConfiguration getEventManagementConfiguration();

    /**
     * Get processing logic component used for inbound event payloads.
     *
     * @return
     */
    public IEventManagementStoreLogic getEventManagementStoreLogic();
}
