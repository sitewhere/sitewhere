/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sitewhere.grpc.client.event.BlockingDeviceEventManagement;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/*
 * Controller for device state operations.
 */
@Path("/devicestates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "devicestates")
public class DeviceStates {

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /**
     * Search device states.
     * 
     * @param includeDevice
     * @param includeDeviceType
     * @param includeDeviceAssignment
     * @param includeCustomer
     * @param includeArea
     * @param includeAsset
     * @param includeEventDetails
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/search")
    @ApiOperation(value = "List device states matching criteria")
    public Response searchDeviceStates(
	    @ApiParam(value = "Include device information", required = false) @QueryParam("includeDevice") @DefaultValue("false") boolean includeDevice,
	    @ApiParam(value = "Include device type information", required = false) @QueryParam("includeDeviceType") @DefaultValue("false") boolean includeDeviceType,
	    @ApiParam(value = "Include device assignment information", required = false) @QueryParam("includeDeviceAssignment") @DefaultValue("false") boolean includeDeviceAssignment,
	    @ApiParam(value = "Include customer information", required = false) @QueryParam("includeCustomer") @DefaultValue("false") boolean includeCustomer,
	    @ApiParam(value = "Include area information", required = false) @QueryParam("includeArea") @DefaultValue("false") boolean includeArea,
	    @ApiParam(value = "Include asset information", required = false) @QueryParam("includeAsset") @DefaultValue("false") boolean includeAsset,
	    @ApiParam(value = "Include recent events", required = false) @QueryParam("includeRecentEvents") @DefaultValue("false") boolean includeRecentEvents,
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
	return Response.ok(new SearchResults<IDeviceState>(results, matches.getNumResults())).build();
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiChannel();
    }

    protected IDeviceEventManagement getDeviceEventManagement() {
	return new BlockingDeviceEventManagement(getMicroservice().getDeviceEventManagementApiChannel());
    }

    protected IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagementApiChannel();
    }

    protected IDeviceStateManagement getDeviceStateManagement() {
	return getMicroservice().getDeviceStateApiChannel();
    }

    protected IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}