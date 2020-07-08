/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.kafka;

import java.util.UUID;

import org.apache.kafka.common.serialization.UUIDSerializer;

import com.sitewhere.event.spi.kafka.IOutboundEventsProducer;
import com.sitewhere.microservice.kafka.MicroserviceKafkaProducer;
import com.sitewhere.spi.SiteWhereException;

/**
 * Kafka producer that sends sends enriched events to a topic for further
 * processing.
 */
public class OutboundEventsProducer extends MicroserviceKafkaProducer<UUID, byte[]> implements IOutboundEventsProducer {

    /*
     * @see
     * com.sitewhere.microservice.kafka.MicroserviceKafkaProducer#getKeySerializer()
     */
    @Override
    public Class<?> getKeySerializer() {
	return UUIDSerializer.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#
     * getTargetTopicName()
     */
    @Override
    public String getTargetTopicName() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming().getOutboundEventsTopic(getTenantEngine().getTenantResource());
    }
}