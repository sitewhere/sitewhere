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
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.DeviceStateMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.api.state.IDeviceStateManagement;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.DeviceStateSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.state.IDeviceState;
import com.sitewhere.spi.search.ISearchResults;

/*
 * Controller for device state operations.
 */
@RestController
@RequestMapping("/api/devicestates")
public class DeviceStates {

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Search device states.
     * 
     * @param includeDevice
     * @param includeDeviceType
     * @param includeDeviceAssignment
     * @param includeCustomer
     * @param includeArea
     * @param includeAsset
     * @param includeRecentEvents
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/search")
    public SearchResults<IDeviceState> searchDeviceStates(
	    @RequestParam(defaultValue = "false", required = false) boolean includeDevice,
	    @RequestParam(defaultValue = "false", required = false) boolean includeDeviceType,
	    @RequestParam(defaultValue = "false", required = false) boolean includeDeviceAssignment,
	    @RequestParam(defaultValue = "false", required = false) boolean includeCustomer,
	    @RequestParam(defaultValue = "false", required = false) boolean includeArea,
	    @RequestParam(defaultValue = "false", required = false) boolean includeAsset,
	    @RequestParam(defaultValue = "false", required = false) boolean includeRecentEvents,
	    @RequestBody DeviceStateSearchCriteria criteria) throws SiteWhereException {

	// Perform search.
	ISearchResults<? extends IDeviceState> matches = getDeviceStateManagement().searchDeviceStates(criteria);
	DeviceStateMarshalHelper helper = new DeviceStateMarshalHelper(getDeviceManagement(),
		getDeviceEventManagement(), getAssetManagement());
	helper.setIncludeDevice(includeDevice);
	helper.setIncludeDeviceType(includeDeviceType);
	helper.setIncludeDeviceAssignment(includeDeviceAssignment);
	helper.setIncludeCustomer(includeCustomer);
	helper.setIncludeArea(includeArea);
	helper.setIncludeAsset(includeAsset);
	helper.setIncludeRecentEvents(includeRecentEvents);

	List<IDeviceState> results = new ArrayList<>();
	for (IDeviceState assn : matches.getResults()) {
	    results.add(helper.convert(assn));
	}
	return new SearchResults<IDeviceState>(results, matches.getNumResults());
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    protected IDeviceEventManagement getDeviceEventManagement() {
	return getMicroservice().getDeviceEventManagementApiChannel();
    }

    protected IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagement();
    }

    protected IDeviceStateManagement getDeviceStateManagement() {
	return getMicroservice().getDeviceStateApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}