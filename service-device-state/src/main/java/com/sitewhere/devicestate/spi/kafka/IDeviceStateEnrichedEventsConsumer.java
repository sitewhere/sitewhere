/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.spi.kafka;

import com.sitewhere.devicestate.spi.processing.IDeviceStateProcessingLogic;
import com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer;

/**
 * Consumer that pulls enriched device events from a Kafka topic and processes
 * them to store device state.
 * 
 * @author Derek
 */
public interface IDeviceStateEnrichedEventsConsumer extends IMicroserviceKafkaConsumer {

    /**
     * Get logic used for processing enriched inbound events for device state.
     * 
     * @return
     */
    public IDeviceStateProcessingLogic getDeviceStateProcessingLogic();
}
