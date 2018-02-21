/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.kafka;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.inbound.spi.kafka.IEnrichedEventsProducer;
import com.sitewhere.microservice.kafka.MicroserviceKafkaProducer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;

/**
 * Kafka producer that sends sends enriched events to a topic for further
 * processing.
 * 
 * @author Derek
 */
public class EnrichedEventsProducer extends MicroserviceKafkaProducer implements IEnrichedEventsProducer {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(EnrichedEventsProducer.class);

    public EnrichedEventsProducer(IMicroservice microservice) {
	super(microservice);
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#
     * getTargetTopicName()
     */
    @Override
    public String getTargetTopicName() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming().getInboundEnrichedEventsTopic(getTenantEngine().getTenant());
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }
}