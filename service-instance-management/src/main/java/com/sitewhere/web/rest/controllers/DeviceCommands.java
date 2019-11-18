/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.rest.model.device.command.DeviceCommandNamespace;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.DeviceCommandSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.IDeviceCommandNamespace;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for device command operations.
 */
@Path("/commands")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "commands")
public class DeviceCommands {

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /**
     * List commands that match the given criteria.
     * 
     * @param deviceTypeToken
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @ApiOperation(value = "List device commands that match criteria.")
    public Response listDeviceCommands(
	    @ApiParam(value = "Device type token", required = false) @QueryParam("deviceTypeToken") String deviceTypeToken,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	DeviceCommandSearchCriteria criteria = new DeviceCommandSearchCriteria(page, pageSize);
	criteria.setDeviceTypeToken(deviceTypeToken);

	return Response.ok(getDeviceManagement().listDeviceCommands(criteria)).build();
    }

    /**
     * List commands grouped by namespace.
     * 
     * @param deviceTypeToken
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/namespaces")
    @ApiOperation(value = "List device commands by namespace")
    public Response listAllDeviceCommandsByNamespace(
	    @ApiParam(value = "Device type token", required = false) @QueryParam("deviceTypeToken") String deviceTypeToken)
	    throws SiteWhereException {
	DeviceCommandSearchCriteria criteria = new DeviceCommandSearchCriteria(1, 0);
	criteria.setDeviceTypeToken(deviceTypeToken);

	List<IDeviceCommand> results = getDeviceManagement().listDeviceCommands(criteria).getResults();
	Collections.sort(results, new Comparator<IDeviceCommand>() {
	    public int compare(IDeviceCommand o1, IDeviceCommand o2) {
		if ((o1.getNamespace() == null) && (o2.getNamespace() != null)) {
		    return -1;
		}
		if ((o1.getNamespace() != null) && (o2.getNamespace() == null)) {
		    return 1;
		}
		if ((o1.getNamespace() == null) && (o2.getNamespace() == null)) {
		    return o1.getName().compareTo(o2.getName());
		}
		if (!o1.getNamespace().equals(o2.getNamespace())) {
		    return o1.getNamespace().compareTo(o2.getNamespace());
		}
		return o1.getName().compareTo(o2.getName());
	    }
	});
	List<IDeviceCommandNamespace> namespaces = new ArrayList<IDeviceCommandNamespace>();
	DeviceCommandNamespace current = null;
	for (IDeviceCommand command : results) {
	    if ((current == null) || ((current.getValue() == null) && (command.getNamespace() != null))
		    || ((current.getValue() != null) && (!current.getValue().equals(command.getNamespace())))) {
		current = new DeviceCommandNamespace();
		current.setValue(command.getNamespace());
		namespaces.add(current);
	    }
	    current.getCommands().add(command);
	}
	return Response.ok(new SearchResults<IDeviceCommandNamespace>(namespaces)).build();
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiChannel();
    }

    protected IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}