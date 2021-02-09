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
package com.sitewhere.connectors;

import com.sitewhere.connectors.spi.IOutboundConnector;
import com.sitewhere.connectors.spi.microservice.IOutboundConnectorsMicroservice;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Default implementation of {@link IOutboundConnector}.
 */
public abstract class OutboundConnector extends TenantEngineLifecycleComponent implements IOutboundConnector {

    /** Default number of threads used for processing */
    private static final int DEFAULT_NUM_PROCESSING_THREADS = 2;

    /** Unqiue connector id */
    private String connectorId;

    /** Number of threads used for processing events */
    private int numProcessingThreads = DEFAULT_NUM_PROCESSING_THREADS;

    public OutboundConnector() {
	super(LifecycleComponentType.OutboundConnector);
    }

    /*
     * @see com.sitewhere.connectors.spi.IOutboundConnector#getConnectorId()
     */
    @Override
    public String getConnectorId() {
	return connectorId;
    }

    public void setConnectorId(String connectorId) {
	this.connectorId = connectorId;
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IOutboundConnector#getNumProcessingThreads()
     */
    @Override
    public int getNumProcessingThreads() {
	return numProcessingThreads;
    }

    public void setNumProcessingThreads(int numProcessingThreads) {
	this.numProcessingThreads = numProcessingThreads;
    }

    /*
     * @see com.sitewhere.connectors.spi.IOutboundConnector#getDeviceManagement()
     */
    @Override
    public IDeviceManagement getDeviceManagement() {
	return ((IOutboundConnectorsMicroservice) getTenantEngine().getMicroservice()).getDeviceManagement();
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IOutboundConnector#getDeviceEventManagement()
     */
    @Override
    public IDeviceEventManagement getDeviceEventManagement() {
	return ((IOutboundConnectorsMicroservice) getTenantEngine().getMicroservice())
		.getDeviceEventManagementApiChannel();
    }
}