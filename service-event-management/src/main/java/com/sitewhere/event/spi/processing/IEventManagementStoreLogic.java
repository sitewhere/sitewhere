/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.spi.processing;

import com.sitewhere.grpc.model.DeviceEventModel;
import com.sitewhere.grpc.model.DeviceEventModel.GDecodedEventPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;
import org.apache.kafka.common.TopicPartition;

import java.util.List;

/**
 * Logic applied to decoded inbound event payloads.
 * 
 * @author Derek
 */
public interface IEventManagementStoreLogic extends ITenantEngineLifecycleComponent {

    /**
     * Process batch of decoded records for a topic partition.
     * 
     * @param topicPartition
     * @param decoded
     * @throws SiteWhereException
     */
    public void process(TopicPartition topicPartition, List<DeviceEventModel.GPreprocessedEventPayload> decoded) throws SiteWhereException;

}