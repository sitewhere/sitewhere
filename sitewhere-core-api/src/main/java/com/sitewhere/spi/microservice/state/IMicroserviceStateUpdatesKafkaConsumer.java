/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

import com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer;

/**
 * Kafka consumer that processes state updates for microservices and their
 * managed tenant engines.
 * 
 * @author Derek
 */
public interface IMicroserviceStateUpdatesKafkaConsumer extends IMicroserviceKafkaConsumer {

    /**
     * Called when a microservice state update is received.
     * 
     * @param state
     */
    public void onMicroserviceStateUpdate(IMicroserviceState state);

    /**
     * Called when a tenant engine state update is received.
     * 
     * @param state
     */
    public void onTenantEngineStateUpdate(ITenantEngineState state);
}