/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka;

import com.sitewhere.commands.spi.kafka.IUndeliveredCommandInvocationsProducer;
import com.sitewhere.microservice.kafka.AckPolicy;
import com.sitewhere.microservice.kafka.MicroserviceKafkaProducer;
import com.sitewhere.spi.SiteWhereException;

/**
 * Kafka producer for a stream of decoded events produced by all event sources
 * for a tenant.
 * 
 * @author Derek
 */
public class UndeliveredCommandInvocationsProducer extends MicroserviceKafkaProducer
	implements IUndeliveredCommandInvocationsProducer {

    public UndeliveredCommandInvocationsProducer() {
	super(AckPolicy.FireAndForget);
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#
     * getTargetTopicName()
     */
    @Override
    public String getTargetTopicName() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming()
		.getUndeliveredCommandInvocationsTopic(getTenantEngine().getTenant());
    }
}