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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.rest.model.search.device.DeviceStatusSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Controller for device status operations.
 */
@RestController
@RequestMapping("/api/statuses")
public class DeviceStatuses {

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * List statuses that match the given criteria.
     * 
     * @param deviceTypeToken
     * @param code
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    public ISearchResults<? extends IDeviceStatus> listDeviceStatuses(
	    @RequestParam(required = false) String deviceTypeToken, @RequestParam(required = false) String code,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
	DeviceStatusSearchCriteria criteria = new DeviceStatusSearchCriteria(page, pageSize);
	criteria.setDeviceTypeToken(deviceTypeToken);

	// Add code if specified.
	if (code != null) {
	    criteria.setCode(code);
	}

	return getDeviceManagement().listDeviceStatuses(criteria);
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}
