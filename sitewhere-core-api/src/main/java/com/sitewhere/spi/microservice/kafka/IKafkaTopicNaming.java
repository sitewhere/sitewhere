/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.kafka;

import com.sitewhere.spi.tenant.ITenant;

/**
 * Provides names for Kafka topics used in SiteWhere.
 * 
 * @author Derek
 */
public interface IKafkaTopicNaming {

    /**
     * Get prefix that uniquely identifies SiteWhere instance.
     * 
     * @return
     */
    public String getInstancePrefix();

    /**
     * Get topic name for tracking tenant model updates.
     * 
     * @return
     */
    public String getTenantUpdatesTopic();

    /**
     * Get name for topic that contains events that have been decoded from
     * inbound event sources.
     * 
     * @param tenant
     * @return
     */
    public String getEventSourceDecodedEventsTopic(ITenant tenant);

    /**
     * Get name for topic that contains events that could not be decoded from
     * event sources.
     * 
     * @param tenant
     * @return
     */
    public String getEventSourceFailedDecodedTopic(ITenant tenant);
}