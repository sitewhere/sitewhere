/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.kafka;

import com.sitewhere.event.spi.kafka.IInboundPersistedEventsProducer;
import com.sitewhere.microservice.kafka.AckPolicy;
import com.sitewhere.microservice.kafka.MicroserviceKafkaProducer;
import com.sitewhere.spi.SiteWhereException;

/**
 * Kafka producer for events that have been persisted to the event datastore and
 * are ready for further processing.
 * 
 * @author Derek
 */
public class InboundPersistedEventsProducer extends MicroserviceKafkaProducer
	implements IInboundPersistedEventsProducer {

    public InboundPersistedEventsProducer() {
	super(AckPolicy.FireAndForget);
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#
     * getTargetTopicName()
     */
    @Override
    public String getTargetTopicName() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming().getInboundPersistedEventsTopic(getTenantEngine().getTenant());
    }
}