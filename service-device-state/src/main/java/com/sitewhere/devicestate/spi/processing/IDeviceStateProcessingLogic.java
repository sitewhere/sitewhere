/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.devicestate.spi.processing;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Logic applied to enriched event payloads to process device state.
 */
public interface IDeviceStateProcessingLogic extends ITenantEngineLifecycleComponent {

    /**
     * Process a batch of records from Kafka.
     * 
     * @param records
     * @throws SiteWhereException
     */
    public void process(List<ConsumerRecord<String, byte[]>> records) throws SiteWhereException;
}