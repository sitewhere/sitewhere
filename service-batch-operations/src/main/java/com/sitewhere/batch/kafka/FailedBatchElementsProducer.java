/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.kafka;

import com.sitewhere.batch.spi.kafka.IFailedBatchElementsProducer;
import com.sitewhere.microservice.kafka.MicroserviceKafkaProducer;
import com.sitewhere.spi.SiteWhereException;

/**
 * Kafka producer that sends sends unprocessed batch elements to a topic for
 * further processing.
 */
public class FailedBatchElementsProducer extends MicroserviceKafkaProducer<String, byte[]>
	implements IFailedBatchElementsProducer {

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#
     * getTargetTopicName()
     */
    @Override
    public String getTargetTopicName() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming()
		.getFailedBatchElementsTopic(getTenantEngine().getTenantResource());
    }
}