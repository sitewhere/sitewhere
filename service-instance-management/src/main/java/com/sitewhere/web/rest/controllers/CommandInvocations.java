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
import com.sitewhere.microservice.api.device.DeviceCommandInvocationMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.rest.model.device.event.view.DeviceCommandInvocationSummary;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceCommandInvocation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.web.rest.view.DeviceInvocationSummaryBuilder;

/**
 * Controller for command invocation operations.
 */
@RestController
@RequestMapping("/api/invocations")
public class CommandInvocations {

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(CommandInvocations.class);

    /**
     * Get a command invocation by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/id/{id}")
    public MarshaledDeviceCommandInvocation getDeviceCommandInvocation(@PathVariable UUID id)
	    throws SiteWhereException {
	IDeviceEvent found = getDeviceEventManagement().getDeviceEventById(id);
	if (!(found instanceof IDeviceCommandInvocation)) {
	    throw new SiteWhereException("Event with the corresponding id is not a command invocation.");
	}
	DeviceCommandInvocationMarshalHelper helper = new DeviceCommandInvocationMarshalHelper(getDeviceManagement());
	return helper.convert((IDeviceCommandInvocation) found);
    }

    /**
     * Get a summarized version of the given device command invocation.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/id/{id}/summary")
    public DeviceCommandInvocationSummary getDeviceCommandInvocationSummary(@PathVariable UUID id)
	    throws SiteWhereException {
	IDeviceEvent found = getDeviceEventManagement().getDeviceEventById(id);
	if (!(found instanceof IDeviceCommandInvocation)) {
	    throw new SiteWhereException("Event with the corresponding id is not a command invocation.");
	}
	IDeviceCommandInvocation invocation = (IDeviceCommandInvocation) found;
	DeviceCommandInvocationMarshalHelper helper = new DeviceCommandInvocationMarshalHelper(getDeviceManagement());
	helper.setIncludeCommand(true);
	MarshaledDeviceCommandInvocation converted = helper.convert(invocation);
	ISearchResults<IDeviceCommandResponse> responses = getDeviceEventManagement()
		.listDeviceCommandInvocationResponses(found.getId());
	return DeviceInvocationSummaryBuilder.build(converted, responses.getResults(), getDeviceManagement(),
		getDeviceEventManagement());
    }

    /**
     * List all responses for a command invocation.
     * 
     * @param invocationId
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/id/{invocationId}/responses")
    public ISearchResults<IDeviceCommandResponse> listCommandInvocationResponses(@PathVariable UUID invocationId)
	    throws SiteWhereException {
	return getDeviceEventManagement().listDeviceCommandInvocationResponses(invocationId);
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    protected IDeviceEventManagement getDeviceEventManagement() {
	return getMicroservice().getDeviceEventManagementApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}