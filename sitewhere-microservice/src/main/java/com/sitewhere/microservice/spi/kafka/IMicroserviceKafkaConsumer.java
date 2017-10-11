package com.sitewhere.microservice.spi.kafka;

import com.sitewhere.microservice.spi.instance.IInstanceSettings;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Component that consumes messages that are sent to a Kafka topic.
 * 
 * @author Derek
 */
public interface IMicroserviceKafkaConsumer extends ILifecycleComponent {

    /**
     * Get SiteWhere instance settings.
     * 
     * @return
     */
    public IInstanceSettings getInstanceSettings();

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
     * Get name of Kafka topic which will provide the messages.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getSourceTopicName() throws SiteWhereException;

    /**
     * Received a message from the topic.
     * 
     * @param key
     * @param message
     * @throws SiteWhereException
     */
    public void received(String key, byte[] message) throws SiteWhereException;
}