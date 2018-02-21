/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.device.marshaling.DeviceCommandInvocationMarshalHelper;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.view.DeviceCommandInvocationSummary;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.rest.RestControllerBase;
import com.sitewhere.web.rest.view.DeviceInvocationSummaryBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for command invocation operations.
 * 
 * @author Derek Adams
 */
@RestController
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code" })
@RequestMapping(value = "/invocations")
@Api(value = "invocations")
public class CommandInvocations extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(CommandInvocations.class);

    /**
     * Get a command invocation by unique id.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get command invocation by unique id.")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceCommandInvocation getDeviceCommandInvocation(
	    @ApiParam(value = "Unique id", required = true) @PathVariable String id, HttpServletRequest servletRequest)
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
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{id}/summary", method = RequestMethod.GET)
    @ApiOperation(value = "Get command invocation summary")
    @Secured({ SiteWhereRoles.REST })
    public DeviceCommandInvocationSummary getDeviceCommandInvocationSummary(
	    @ApiParam(value = "Unique id", required = true) @PathVariable String id, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	IDeviceEvent found = getDeviceEventManagement().getDeviceEventById(id);
	if (!(found instanceof IDeviceCommandInvocation)) {
	    throw new SiteWhereException("Event with the corresponding id is not a command invocation.");
	}
	IDeviceCommandInvocation invocation = (IDeviceCommandInvocation) found;
	DeviceCommandInvocationMarshalHelper helper = new DeviceCommandInvocationMarshalHelper(getDeviceManagement());
	helper.setIncludeCommand(true);
	DeviceCommandInvocation converted = helper.convert(invocation);
	ISearchResults<IDeviceCommandResponse> responses = getDeviceEventManagement()
		.listDeviceCommandInvocationResponses(found.getId());
	return DeviceInvocationSummaryBuilder.build(converted, responses.getResults(), getDeviceEventManagement());
    }

    /**
     * List all responses for a command invocation.
     * 
     * @param assignmentToken
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{id}/responses", method = RequestMethod.GET)
    @ApiOperation(value = "List responses for command invocation")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceCommandResponse> listCommandInvocationResponses(
	    @ApiParam(value = "Invocation id", required = true) @PathVariable String id,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getDeviceEventManagement().listDeviceCommandInvocationResponses(id);
    }

    private IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiDemux().getApiChannel();
    }

    private IDeviceEventManagement getDeviceEventManagement() {
	return getMicroservice().getDeviceEventManagementApiDemux().getApiChannel();
    }
}