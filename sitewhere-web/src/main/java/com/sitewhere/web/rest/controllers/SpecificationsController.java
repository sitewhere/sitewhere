/*
 * SpecificationsController.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
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

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.rest.model.device.request.DeviceSpecificationCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.web.rest.model.DeviceSpecificationMarshalHelper;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for device specification operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/specifications")
@Api(value = "", description = "Operations related to SiteWhere device specifications.")
public class SpecificationsController extends SiteWhereController {

	/**
	 * Create a device specification.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a new device specification")
	public IDeviceSpecification createDeviceSpecification(
			@RequestBody DeviceSpecificationCreateRequest request) throws SiteWhereException {
		IAsset asset =
				SiteWhereServer.getInstance().getAssetModuleManager().getAssetById(AssetType.Device,
						request.getAssetId());
		if (asset == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidAssetReferenceId, ErrorLevel.ERROR,
					HttpServletResponse.SC_NOT_FOUND);
		}
		IDeviceSpecification result =
				SiteWhereServer.getInstance().getDeviceManagement().createDeviceSpecification(request);
		DeviceSpecificationMarshalHelper helper = new DeviceSpecificationMarshalHelper();
		helper.setIncludeAsset(true);
		return helper.convert(result, SiteWhereServer.getInstance().getAssetModuleManager());
	}

	/**
	 * Get a device specification by unique token.
	 * 
	 * @param hardwareId
	 * @return
	 */
	@RequestMapping(value = "/{token}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get a device specification by unique token")
	public IDeviceSpecification getDeviceSpecificationByToken(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			@ApiParam(value = "Include detailed asset information", required = false) @RequestParam(defaultValue = "true") boolean includeAsset)
			throws SiteWhereException {
		IDeviceSpecification result = assertDeviceSpecificationByToken(token);
		DeviceSpecificationMarshalHelper helper = new DeviceSpecificationMarshalHelper();
		helper.setIncludeAsset(includeAsset);
		return helper.convert(result, SiteWhereServer.getInstance().getAssetModuleManager());
	}

	/**
	 * Update an existing device specification.
	 * 
	 * @param token
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}", method = RequestMethod.PUT)
	@ResponseBody
	@ApiOperation(value = "Update device specification information")
	public IDeviceSpecification updateDeviceSpecification(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			@RequestBody DeviceSpecificationCreateRequest request) throws SiteWhereException {
		IDeviceSpecification result =
				SiteWhereServer.getInstance().getDeviceManagement().updateDeviceSpecification(token, request);
		DeviceSpecificationMarshalHelper helper = new DeviceSpecificationMarshalHelper();
		helper.setIncludeAsset(true);
		return helper.convert(result, SiteWhereServer.getInstance().getAssetModuleManager());
	}

	/**
	 * List device specifications that meet the given criteria.
	 * 
	 * @param includeDeleted
	 * @param includeAsset
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List device specifications that match certain criteria")
	public ISearchResults<IDeviceSpecification> listDeviceSpecifications(
			@ApiParam(value = "Include deleted", required = false) @RequestParam(defaultValue = "false") boolean includeDeleted,
			@ApiParam(value = "Include detailed asset information", required = false) @RequestParam(defaultValue = "true") boolean includeAsset,
			@ApiParam(value = "Page Number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize)
			throws SiteWhereException {
		SearchCriteria criteria = new SearchCriteria(page, pageSize);
		ISearchResults<IDeviceSpecification> results =
				SiteWhereServer.getInstance().getDeviceManagement().listDeviceSpecifications(includeDeleted,
						criteria);
		DeviceSpecificationMarshalHelper helper = new DeviceSpecificationMarshalHelper();
		helper.setIncludeAsset(includeAsset);
		List<IDeviceSpecification> specsConv = new ArrayList<IDeviceSpecification>();
		for (IDeviceSpecification device : results.getResults()) {
			specsConv.add(helper.convert(device, SiteWhereServer.getInstance().getAssetModuleManager()));
		}
		Collections.sort(specsConv, new Comparator<IDeviceSpecification>() {
			public int compare(IDeviceSpecification o1, IDeviceSpecification o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return new SearchResults<IDeviceSpecification>(specsConv, results.getNumResults());
	}

	/**
	 * Delete an existing device specification.
	 * 
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}", method = RequestMethod.DELETE)
	@ResponseBody
	@ApiOperation(value = "Delete a device specification based on token")
	public IDeviceSpecification deleteDeviceSpecification(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			@ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force)
			throws SiteWhereException {
		IDeviceSpecification result =
				SiteWhereServer.getInstance().getDeviceManagement().deleteDeviceSpecification(token, force);
		DeviceSpecificationMarshalHelper helper = new DeviceSpecificationMarshalHelper();
		helper.setIncludeAsset(true);
		return helper.convert(result, SiteWhereServer.getInstance().getAssetModuleManager());
	}

	/**
	 * Gets a device specification by token and throws an exception if not found.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDeviceSpecification assertDeviceSpecificationByToken(String token) throws SiteWhereException {
		IDeviceSpecification result =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceSpecificationByToken(token);
		if (result == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceSpecificationToken, ErrorLevel.ERROR,
					HttpServletResponse.SC_NOT_FOUND);
		}
		return result;
	}
}