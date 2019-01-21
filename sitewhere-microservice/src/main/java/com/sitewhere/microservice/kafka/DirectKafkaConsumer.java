/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;

import com.sitewhere.spi.SiteWhereException;

public abstract class DirectKafkaConsumer extends MicroserviceKafkaConsumer {

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#process(org.
     * apache.kafka.common.TopicPartition, java.util.List)
     */
    @Override
    public void process(TopicPartition topicPartition, List<ConsumerRecord<String, byte[]>> records) {
	try {
	    attemptToProcess(topicPartition, records);
	    getConsumer().commitAsync(new OffsetCommitCallback() {
		public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception e) {
		    if (e != null) {
			getLogger().error("Commit failed for offsets " + offsets, e);
		    }
		}
	    });
	} catch (SiteWhereException e) {
	    getLogger().error("Exception in consumer processing.", e);
	} catch (Throwable e) {
	    getLogger().error("Unhandled exception in consumer processing.", e);
	}
    }

    /**
     * Attempts to process a batch of records, throwing an exception if processing
     * fails.
     * 
     * @param topicPartition
     * @param records
     * @throws SiteWhereException
     */
    public abstract void attemptToProcess(TopicPartition topicPartition, List<ConsumerRecord<String, byte[]>> records)
	    throws SiteWhereException;
}