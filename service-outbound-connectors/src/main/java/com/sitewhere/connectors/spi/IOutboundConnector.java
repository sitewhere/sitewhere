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
package com.sitewhere.connectors.spi;

import java.util.List;

import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.kafka.IProcessedEventPayload;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Connects processed events to an external entity for further processing.
 */
public interface IOutboundConnector extends ITenantEngineLifecycleComponent {

    /**
     * Get unique id for connector.
     * 
     * @return
     */
    public String getConnectorId();

    /**
     * Get number of threads used for processing events.
     * 
     * @return
     */
    public int getNumProcessingThreads();

    /**
     * Process a batch of events.
     * 
     * @param payloads
     * @throws SiteWhereException
     */
    public void processEventBatch(List<IProcessedEventPayload> payloads) throws SiteWhereException;

    /**
     * Handle a batch of events that could not be processed.
     * 
     * @param payloads
     * @param failReason
     * @throws SiteWhereException
     */
    public void handleFailedBatch(List<IProcessedEventPayload> payloads, Throwable failReason)
	    throws SiteWhereException;

    /**
     * Get device management API.
     * 
     * @return
     */
    public IDeviceManagement getDeviceManagement();

    /**
     * Get device event management API.
     * 
     * @return
     */
    public IDeviceEventManagement getDeviceEventManagement();
}