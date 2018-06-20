/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.spi.processing;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Logic applied to decoded inbound event payloads.
 * 
 * @author Derek
 */
public interface IInboundPayloadProcessingLogic extends ITenantEngineLifecycleComponent {

    /**
     * Process a batch of records from Kafka.
     * 
     * @param records
     * @throws SiteWhereException
     */
    public void process(List<ConsumerRecord<String, byte[]>> records) throws SiteWhereException;
}