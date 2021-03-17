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

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

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

/*
 * Controller for device state operations.
 */
@Path("/api/devicestates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "devicestates")
@Tag(name = "Device States", description = "Services used to query the current state of one or more devices.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class DeviceStates {

    @Inject
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
     * @param includeEventDetails
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/search")
    @Operation(summary = "List device states matching criteria", description = "List device states matching criteria")
    public Response searchDeviceStates(
	    @Parameter(description = "Include device information", required = false) @QueryParam("includeDevice") @DefaultValue("false") boolean includeDevice,
	    @Parameter(description = "Include device type information", required = false) @QueryParam("includeDeviceType") @DefaultValue("false") boolean includeDeviceType,
	    @Parameter(description = "Include device assignment information", required = false) @QueryParam("includeDeviceAssignment") @DefaultValue("false") boolean includeDeviceAssignment,
	    @Parameter(description = "Include customer information", required = false) @QueryParam("includeCustomer") @DefaultValue("false") boolean includeCustomer,
	    @Parameter(description = "Include area information", required = false) @QueryParam("includeArea") @DefaultValue("false") boolean includeArea,
	    @Parameter(description = "Include asset information", required = false) @QueryParam("includeAsset") @DefaultValue("false") boolean includeAsset,
	    @Parameter(description = "Include recent events", required = false) @QueryParam("includeRecentEvents") @DefaultValue("false") boolean includeRecentEvents,
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