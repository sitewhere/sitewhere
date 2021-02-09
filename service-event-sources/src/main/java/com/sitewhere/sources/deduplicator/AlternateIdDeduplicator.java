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
package com.sitewhere.sources.deduplicator;

import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDeduplicator;
import com.sitewhere.sources.spi.microservice.IEventSourcesMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IDeviceEventDeduplicator} that checks the alternate
 * id (if present) in an event against the index already stored in the
 * datastore. If the alternate id is already present, the event is considered a
 * duplicate.
 */
public class AlternateIdDeduplicator extends TenantEngineLifecycleComponent implements IDeviceEventDeduplicator {

    public AlternateIdDeduplicator() {
	super(LifecycleComponentType.DeviceEventDeduplicator);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceEventDeduplicator#
     * isDuplicate(com.sitewhere.spi.device.communication.IDecodedDeviceRequest)
     */
    @Override
    public boolean isDuplicate(IDecodedDeviceRequest<?> request) throws SiteWhereException {
	if (request.getRequest() instanceof IDeviceEventCreateRequest) {
	    IDeviceEventCreateRequest createRequest = (IDeviceEventCreateRequest) request.getRequest();
	    String alternateId = createRequest.getAlternateId();
	    if (alternateId != null) {
		IDeviceEvent existing = getDeviceEventManagement().getDeviceEventByAlternateId(alternateId);
		if (existing != null) {
		    getLogger().info("Found event with same alternate id. Will be treated as duplicate.");
		    return true;
		}
		return false;
	    }
	}
	return false;
    }

    private IDeviceEventManagement getDeviceEventManagement() {
	return ((IEventSourcesMicroservice) getMicroservice()).getDeviceEventManagementApiChannel();
    }
}