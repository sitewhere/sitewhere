/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.inbound.spi.kafka.IEnrichedCommandInvocationsProducer;
import com.sitewhere.microservice.kafka.MicroserviceKafkaProducer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;

/**
 * Kafka producer that sends sends enriched device command invocations to a
 * topic for further processing.
 * 
 * @author Derek
 */
public class EnrichedCommandInvocationsProducer extends MicroserviceKafkaProducer
	implements IEnrichedCommandInvocationsProducer {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public EnrichedCommandInvocationsProducer(IMicroservice microservice) {
	super(microservice);
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#
     * getTargetTopicName()
     */
    @Override
    public String getTargetTopicName() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming()
		.getInboundEnrichedCommandInvocationsTopic(getTenantEngine().getTenant());
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}