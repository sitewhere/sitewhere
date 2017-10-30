/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.spi.kafka;

import com.sitewhere.microservice.spi.instance.IInstanceSettings;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Component that produces messages that are sent to a Kafka topic.
 * 
 * @author Derek
 */
public interface IMicroserviceKafkaProducer extends ILifecycleComponent {

    /**
     * Get SiteWhere instance settings.
     * 
     * @return
     */
    public IInstanceSettings getInstanceSettings();

    /**
     * Get name of Kafka topic which will receive the messages.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getTargetTopicName() throws SiteWhereException;

    /**
     * Send a message to the topic.
     * 
     * @param key
     * @param message
     * @throws SiteWhereException
     */
    public void send(String key, byte[] message) throws SiteWhereException;
}