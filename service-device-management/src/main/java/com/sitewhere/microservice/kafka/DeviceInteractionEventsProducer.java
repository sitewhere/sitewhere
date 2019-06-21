/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka;

import com.sitewhere.device.spi.kafka.IDeviceInteractionEventsProducer;
import com.sitewhere.microservice.kafka.AckPolicy;
import com.sitewhere.microservice.kafka.MicroserviceKafkaProducer;
import com.sitewhere.spi.SiteWhereException;

/**
 * Kafka producer that sends events triggered by device management interactions.
 */
public class DeviceInteractionEventsProducer extends MicroserviceKafkaProducer
	implements IDeviceInteractionEventsProducer {

    public DeviceInteractionEventsProducer() {
	super(AckPolicy.Leader);
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#
     * getTargetTopicName()
     */
    @Override
    public String getTargetTopicName() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming().getInboundEventsTopic(getTenantEngine().getTenant());
    }
}
