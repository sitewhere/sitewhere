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
 * Kafka producer that reports instance topology updates.
 * 
 * @author Derek
 */
public interface IInstanceTopologyUpdatesKafkaProducer extends IMicroserviceKafkaProducer {

    /**
     * Send an instance topology update.
     * 
     * @param update
     * @throws SiteWhereException
     */
    public void send(IInstanceTopologyUpdate update) throws SiteWhereException;
}