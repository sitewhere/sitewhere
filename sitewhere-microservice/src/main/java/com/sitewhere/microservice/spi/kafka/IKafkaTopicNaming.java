/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.spi.kafka;

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
}