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
package com.sitewhere.devicestate.persistence;

import com.sitewhere.microservice.persistence.Persistence;
import com.sitewhere.rest.model.device.state.DeviceState;
import com.sitewhere.rest.model.device.state.RecentStateEvent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest;
import com.sitewhere.spi.device.state.request.IRecentStateEventCreateRequest;

/**
 * Common methods needed by device state management implementations.
 */
public class DeviceStatePersistence extends Persistence {

    /**
     * Common logic for creating new device state object and populating it from
     * request.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceState deviceStateCreateLogic(IDeviceStateCreateRequest request) throws SiteWhereException {
	DeviceState state = new DeviceState();

	requireNotNull("Device id", request.getDeviceId());
	state.setDeviceId(request.getDeviceId());

	requireNotNull("Device type id", request.getDeviceTypeId());
	state.setDeviceTypeId(request.getDeviceTypeId());

	requireNotNull("Device assignment id", request.getDeviceAssignmentId());
	state.setDeviceAssignmentId(request.getDeviceAssignmentId());

	state.setCustomerId(request.getCustomerId());
	state.setAreaId(request.getAreaId());
	state.setAssetId(request.getAssetId());
	state.setLastInteractionDate(request.getLastInteractionDate());
	state.setPresenceMissingDate(request.getPresenceMissingDate());
	return state;
    }

    /**
     * Common logic for updating an existing device state object.
     * 
     * @param request
     * @param target
     * @throws SiteWhereException
     */
    public static void deviceStateUpdateLogic(IDeviceStateCreateRequest request, DeviceState target)
	    throws SiteWhereException {
	target.setLastInteractionDate(request.getLastInteractionDate());
	target.setPresenceMissingDate(request.getPresenceMissingDate());
	if (request.getDeviceTypeId() != null) {
	    target.setDeviceTypeId(request.getDeviceTypeId());
	}
	if (request.getCustomerId() != null) {
	    target.setCustomerId(request.getCustomerId());
	}
	if (request.getAreaId() != null) {
	    target.setAreaId(request.getAreaId());
	}
	if (request.getAssetId() != null) {
	    target.setAssetId(request.getAssetId());
	}
    }

    /**
     * Common logic for creating new recent state event object and populating it
     * from request.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static RecentStateEvent recentStateEventCreateLogic(IRecentStateEventCreateRequest request)
	    throws SiteWhereException {
	RecentStateEvent recent = new RecentStateEvent();

	requireNotNull("Device state id", request.getDeviceStateId());
	recent.setDeviceStateId(request.getDeviceStateId());
	recent.setEventDate(request.getEventDate());
	return recent;
    }

    /**
     * Common logic for updating an existing recent state event object.
     * 
     * @param request
     * @param target
     * @throws SiteWhereException
     */
    public static void recentStateEventUpdateLogic(IRecentStateEventCreateRequest request, RecentStateEvent target)
	    throws SiteWhereException {
	if (request.getEventId() != null) {
	    target.setEventId(request.getEventId());
	}
	if (request.getEventDate() != null) {
	    target.setEventDate(request.getEventDate());
	}
    }
}
