/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.kafka;

import com.sitewhere.batch.spi.kafka.IUnprocessedBatchOperationsProducer;
import com.sitewhere.microservice.kafka.AckPolicy;
import com.sitewhere.microservice.kafka.MicroserviceKafkaProducer;
import com.sitewhere.spi.SiteWhereException;

/**
 * Kafka producer that sends sends unprocessed batch operations to a topic for
 * further processing.
 */
public class UnprocessedBatchOperationsProducer extends MicroserviceKafkaProducer
	implements IUnprocessedBatchOperationsProducer {

    public UnprocessedBatchOperationsProducer() {
	super(AckPolicy.FireAndForget);
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#
     * getTargetTopicName()
     */
    @Override
    public String getTargetTopicName() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming()
		.getUnprocessedBatchOperationsTopic(getTenantEngine().getTenantResource());
    }
}