/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.logging;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer;

/**
 * Kafka producer that forwards log messages to a well-known topic to support
 * centralized logging.
 * 
 * @author Derek
 */
public interface IMicroserviceLogProducer extends IMicroserviceKafkaProducer {

    /**
     * Process a log message by encoding it and adding it to the topic.
     * 
     * @param message
     * @throws SiteWhereException
     */
    public void send(IMicroserviceLogMessage message) throws SiteWhereException;
}