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
package com.sitewhere.connectors.spi.multicast;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Adds ability to send events to multiple desinations.
 */
public interface IDeviceEventMulticaster<T> extends ITenantEngineLifecycleComponent {

    /**
     * Calculates the list of routes to which an event should be sent.
     * 
     * @param event
     * @param deivice
     * @param assignment
     * @return
     * @throws SiteWhereException
     */
    public List<T> calculateRoutes(IDeviceEvent event, IDevice deivice, IDeviceAssignment assignment)
	    throws SiteWhereException;
}