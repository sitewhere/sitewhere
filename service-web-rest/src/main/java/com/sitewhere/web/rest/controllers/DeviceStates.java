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

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.device.marshaling.DeviceStateMarshalHelper;
import com.sitewhere.grpc.client.event.BlockingDeviceEventManagement;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.DeviceStateSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.state.IDeviceState;
import com.sitewhere.spi.device.state.IDeviceStateManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.annotation.SiteWhereCrossOrigin;
import com.sitewhere.web.rest.RestControllerBase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/*
 * Controller for device state operations.
 * 
 * @author Derek Adams
 */
@RestController
@SiteWhereCrossOrigin
@RequestMapping(value = "/devicestates")
@Api(value = "devicestates")
public class DeviceStates extends RestControllerBase {

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ApiOperation(value = "List device states matching criteria")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceState> searchDeviceStates(
	    @ApiParam(value = "Include device information", required = false) @RequestParam(defaultValue = "false") boolean includeDevice,
	    @ApiParam(value = "Include device type information", required = false) @RequestParam(defaultValue = "false") boolean includeDeviceType,
	    @ApiParam(value = "Include device assignment information", required = false) @RequestParam(defaultValue = "false") boolean includeDeviceAssignment,
	    @ApiParam(value = "Include customer information", required = false) @RequestParam(defaultValue = "false") boolean includeCustomer,
	    @ApiParam(value = "Include area information", required = false) @RequestParam(defaultValue = "false") boolean includeArea,
	    @ApiParam(value = "Include asset information", required = false) @RequestParam(defaultValue = "false") boolean includeAsset,
	    @ApiParam(value = "Include event details", required = false) @RequestParam(defaultValue = "false") boolean includeEventDetails,
	    @RequestBody DeviceStateSearchCriteria criteria) throws SiteWhereException {

	// Perform search.
	ISearchResults<IDeviceState> matches = getDeviceStateManagement().searchDeviceStates(criteria);
	DeviceStateMarshalHelper helper = new DeviceStateMarshalHelper(getDeviceManagement(),
		getDeviceEventManagement());
	helper.setIncludeDevice(includeDevice);
	helper.setIncludeDeviceType(includeDeviceType);
	helper.setIncludeDeviceAssignment(includeDeviceAssignment);
	helper.setIncludeCustomer(includeCustomer);
	helper.setIncludeArea(includeArea);
	helper.setIncludeAsset(includeAsset);
	helper.setIncludeEventDetails(includeEventDetails);

	List<IDeviceState> results = new ArrayList<>();
	for (IDeviceState assn : matches.getResults()) {
	    results.add(helper.convert(assn, getAssetManagement()));
	}
	return new SearchResults<IDeviceState>(results, matches.getNumResults());
    }

    private IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiChannel();
    }

    private IDeviceEventManagement getDeviceEventManagement() {
	return new BlockingDeviceEventManagement(getMicroservice().getDeviceEventManagementApiChannel());
    }

    private IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagementApiChannel();
    }

    private IDeviceStateManagement getDeviceStateManagement() {
	return getMicroservice().getDeviceStateApiChannel();
    }
}