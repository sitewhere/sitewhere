/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.grpc.client.event.BlockingDeviceEventManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.annotation.SiteWhereCrossOrigin;
import com.sitewhere.web.rest.RestControllerBase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for event operations.
 * 
 * @author Derek Adams
 */
@RestController
@SiteWhereCrossOrigin
@RequestMapping(value = "/events")
@Api(value = "events")
public class DeviceEvents extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(DeviceEvents.class);

    /**
     * Find a device event by unique id.
     * 
     * @param eventId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/id/{eventId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get event by unique id")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceEvent getEventById(@ApiParam(value = "Event id", required = true) @PathVariable String eventId)
	    throws SiteWhereException {
	return getDeviceEventManagement().getDeviceEventById(UUID.fromString(eventId));
    }

    /**
     * Find a device event by alternate id.
     * 
     * @param alternateId
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/alternate/{alternateId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get event by alternate (external) id")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceEvent getEventByAlternateId(
	    @ApiParam(value = "Alternate id", required = true) @PathVariable String alternateId,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getDeviceEventManagement().getDeviceEventByAlternateId(alternateId);
    }

    private IDeviceEventManagement getDeviceEventManagement() {
	return new BlockingDeviceEventManagement(getMicroservice().getDeviceEventManagementApiChannel());
    }
}