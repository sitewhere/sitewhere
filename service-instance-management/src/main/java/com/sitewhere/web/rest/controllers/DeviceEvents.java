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

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Controller for event operations.
 */
@RestController
@RequestMapping("/api/events")
public class DeviceEvents {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(DeviceEvents.class);

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Find a device event by unique id.
     * 
     * @param eventId
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/id/{eventId}")
    public IDeviceEvent getEventById(@PathVariable String eventId) throws SiteWhereException {
	return getDeviceEventManagement().getDeviceEventById(UUID.fromString(eventId));
    }

    /**
     * Find a device event by alternate id.
     * 
     * @param alternateId
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/alternate/{alternateId}")
    public IDeviceEvent getEventByAlternateId(@PathVariable String alternateId) throws SiteWhereException {
	return getDeviceEventManagement().getDeviceEventByAlternateId(alternateId);
    }

    protected IDeviceEventManagement getDeviceEventManagement() {
	return getMicroservice().getDeviceEventManagementApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}