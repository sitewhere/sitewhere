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

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.core.user.SitewhereRoles;
import com.sitewhere.device.marshaling.DeviceGroupElementMarshalHelper;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.request.DeviceGroupCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupElementCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for device group operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/devicegroups")
@Api(value = "devicegroups", description = "Operations related to SiteWhere device groups.")
public class DeviceGroupsController extends SiteWhereController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DeviceGroupsController.class);

	/**
	 * Create a device group.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a new device group")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public IDeviceGroup createDeviceGroup(@RequestBody DeviceGroupCreateRequest request,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createDeviceGroup", LOGGER);
		try {
			IDeviceGroup result =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).createDeviceGroup(
							request);
			return DeviceGroup.copy(result);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Get a device group by unique token.
	 * 
	 * @param groupToken
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{groupToken}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get a device group by unique token")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public IDeviceGroup getDeviceGroupByToken(
			@ApiParam(value = "Unique token that identifies group", required = true) @PathVariable String groupToken,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getDeviceGroupByToken", LOGGER);
		try {
			IDeviceGroup group =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).getDeviceGroup(
							groupToken);
			if (group == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidDeviceGroupToken, ErrorLevel.ERROR);
			}
			return DeviceGroup.copy(group);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Update an existing device group.
	 * 
	 * @param groupToken
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{groupToken}", method = RequestMethod.PUT)
	@ResponseBody
	@ApiOperation(value = "Update an existing device group")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public IDeviceGroup updateDeviceGroup(
			@ApiParam(value = "Unique token that identifies device group", required = true) @PathVariable String groupToken,
			@RequestBody DeviceGroupCreateRequest request, HttpServletRequest servletRequest)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "updateDeviceGroup", LOGGER);
		try {
			IDeviceGroup group =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).updateDeviceGroup(
							groupToken, request);
			return DeviceGroup.copy(group);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Delete an existing device group.
	 * 
	 * @param groupToken
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{groupToken}", method = RequestMethod.DELETE)
	@ResponseBody
	@ApiOperation(value = "Delete a device group by unique token")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public IDeviceGroup deleteDeviceGroup(
			@ApiParam(value = "Unique token that identifies device group", required = true) @PathVariable String groupToken,
			@ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "deleteDeviceGroup", LOGGER);
		try {
			IDeviceGroup group =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).deleteDeviceGroup(
							groupToken, force);
			return DeviceGroup.copy(group);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * List all device groups.
	 * 
	 * @param role
	 * @param includeDeleted
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List all device groups")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public ISearchResults<IDeviceGroup> listDeviceGroups(
			@ApiParam(value = "Role", required = false) @RequestParam(required = false) String role,
			@ApiParam(value = "Include deleted", required = false) @RequestParam(defaultValue = "false") boolean includeDeleted,
			@ApiParam(value = "Page Number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listDeviceGroups", LOGGER);
		try {
			SearchCriteria criteria = new SearchCriteria(page, pageSize);
			ISearchResults<IDeviceGroup> results;
			if (role == null) {
				results =
						SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).listDeviceGroups(
								includeDeleted, criteria);
			} else {
				results =
						SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).listDeviceGroupsWithRole(
								role, includeDeleted, criteria);
			}
			List<IDeviceGroup> groupsConv = new ArrayList<IDeviceGroup>();
			for (IDeviceGroup group : results.getResults()) {
				groupsConv.add(DeviceGroup.copy(group));
			}
			return new SearchResults<IDeviceGroup>(groupsConv, results.getNumResults());
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * List elements from a device group that meet the given criteria.
	 * 
	 * @param groupToken
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{groupToken}/elements", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List elements from a device group")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public ISearchResults<IDeviceGroupElement> listDeviceGroupElements(
			@ApiParam(value = "Unique token that identifies device group", required = true) @PathVariable String groupToken,
			@ApiParam(value = "Include detailed element information", required = false) @RequestParam(defaultValue = "false") boolean includeDetails,
			@ApiParam(value = "Page Number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listDeviceGroupElements", LOGGER);
		try {
			DeviceGroupElementMarshalHelper helper =
					new DeviceGroupElementMarshalHelper(getTenant(servletRequest)).setIncludeDetails(includeDetails);
			SearchCriteria criteria = new SearchCriteria(page, pageSize);
			ISearchResults<IDeviceGroupElement> results =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).listDeviceGroupElements(
							groupToken, criteria);
			List<IDeviceGroupElement> elmConv = new ArrayList<IDeviceGroupElement>();
			for (IDeviceGroupElement elm : results.getResults()) {
				elmConv.add(helper.convert(elm,
						SiteWhere.getServer().getAssetModuleManager(getTenant(servletRequest))));
			}
			return new SearchResults<IDeviceGroupElement>(elmConv, results.getNumResults());
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Add a list of device group elements to an existing group.
	 * 
	 * @param groupToken
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{groupToken}/elements", method = RequestMethod.PUT)
	@ResponseBody
	@ApiOperation(value = "Add elements to a device group")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public ISearchResults<IDeviceGroupElement> addDeviceGroupElements(
			@ApiParam(value = "Unique token that identifies device group", required = true) @PathVariable String groupToken,
			@RequestBody List<DeviceGroupElementCreateRequest> request, HttpServletRequest servletRequest)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "addDeviceGroupElements", LOGGER);
		try {
			DeviceGroupElementMarshalHelper helper =
					new DeviceGroupElementMarshalHelper(getTenant(servletRequest)).setIncludeDetails(false);
			List<IDeviceGroupElementCreateRequest> elements =
					(List<IDeviceGroupElementCreateRequest>) (List<? extends IDeviceGroupElementCreateRequest>) request;
			List<IDeviceGroupElement> results =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).addDeviceGroupElements(
							groupToken, elements);
			List<IDeviceGroupElement> converted = new ArrayList<IDeviceGroupElement>();
			for (IDeviceGroupElement elm : results) {
				converted.add(helper.convert(elm,
						SiteWhere.getServer().getAssetModuleManager(getTenant(servletRequest))));
			}
			return new SearchResults<IDeviceGroupElement>(converted);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Delete a list of elements from an existing device group.
	 * 
	 * @param groupToken
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{groupToken}/elements", method = RequestMethod.DELETE)
	@ResponseBody
	@ApiOperation(value = "Delete elements from a device group")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public ISearchResults<IDeviceGroupElement> deleteDeviceGroupElements(
			@ApiParam(value = "Unique token that identifies device group", required = true) @PathVariable String groupToken,
			@RequestBody List<DeviceGroupElementCreateRequest> request, HttpServletRequest servletRequest)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "deleteDeviceGroupElements", LOGGER);
		try {
			DeviceGroupElementMarshalHelper helper =
					new DeviceGroupElementMarshalHelper(getTenant(servletRequest)).setIncludeDetails(false);
			List<IDeviceGroupElementCreateRequest> elements =
					(List<IDeviceGroupElementCreateRequest>) (List<? extends IDeviceGroupElementCreateRequest>) request;
			List<IDeviceGroupElement> results =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).removeDeviceGroupElements(
							groupToken, elements);
			List<IDeviceGroupElement> converted = new ArrayList<IDeviceGroupElement>();
			for (IDeviceGroupElement elm : results) {
				converted.add(helper.convert(elm,
						SiteWhere.getServer().getAssetModuleManager(getTenant(servletRequest))));
			}
			return new SearchResults<IDeviceGroupElement>(converted);
		} finally {
			Tracer.stop(LOGGER);
		}
	}
}