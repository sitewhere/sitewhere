/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.spi.microservice;

import com.sitewhere.grpc.model.spi.client.ITenantManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IUserManagementApiChannel;
import com.sitewhere.instance.spi.kafka.IStateAggregatorKafkaConsumer;
import com.sitewhere.instance.spi.templates.IInstanceTemplateManager;
import com.sitewhere.spi.microservice.IMicroservice;

/**
 * API for instance management microservice.
 * 
 * @author Derek
 */
public interface IInstanceManagementMicroservice extends IMicroservice {

    /**
     * Get instance template manager instance.
     * 
     * @return
     */
    public IInstanceTemplateManager getInstanceTemplateManager();

    /**
     * Get the user management API channel.
     * 
     * @return
     */
    public IUserManagementApiChannel getUserManagementApiChannel();

    /**
     * Get the tenant management API channel.
     * 
     * @return
     */
    public ITenantManagementApiChannel getTenantManagementApiChannel();

    /**
     * Get Kafka consumer that aggregates state updates from microservices and
     * tenant engines.
     * 
     * @return
     */
    public IStateAggregatorKafkaConsumer getStateAggregatorKafkaConsumer();
}
