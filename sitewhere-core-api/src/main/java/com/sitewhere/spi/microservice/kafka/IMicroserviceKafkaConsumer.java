/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.kafka;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Component that consumes messages that are sent to a Kafka topic.
 */
public interface IMicroserviceKafkaConsumer extends ITenantEngineLifecycleComponent {

    /**
     * Get unique consumer id.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getConsumerId() throws SiteWhereException;

    /**
     * Get unique consumer group id.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getConsumerGroupId() throws SiteWhereException;

    /**
     * Get wrapped consumer instance.
     * 
     * @return
     */
    public KafkaConsumer<String, byte[]> getConsumer();

    /**
     * Get name of Kafka topics which will provide the messages.
     * 
     * @return
     * @throws SiteWhereException
     */
    public List<String> getSourceTopicNames() throws SiteWhereException;

    /**
     * Process a batch of records for a partition.
     * 
     * @param topicPartition
     * @param records
     */
    public void process(TopicPartition topicPartition, List<ConsumerRecord<String, byte[]>> records);
}