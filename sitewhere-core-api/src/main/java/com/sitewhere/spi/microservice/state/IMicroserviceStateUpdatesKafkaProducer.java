/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer;

/**
 * Kafka producer that reports state updates for microservices and their managed
 * tenant engines.
 * 
 * @author Derek
 */
public interface IMicroserviceStateUpdatesKafkaProducer extends IMicroserviceKafkaProducer {

    /**
     * Send microservice state.
     * 
     * @param state
     * @throws SiteWhereException
     */
    public void send(IMicroserviceState state) throws SiteWhereException;

    /**
     * Send tenant engine state.
     * 
     * @param state
     * @throws SiteWhereException
     */
    public void send(ITenantEngineState state) throws SiteWhereException;
}