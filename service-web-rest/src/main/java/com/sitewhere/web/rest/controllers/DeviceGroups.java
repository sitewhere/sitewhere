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
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.device.marshaling.DeviceGroupElementMarshalHelper;
import com.sitewhere.device.marshaling.DeviceGroupMarshalHelper;
import com.sitewhere.rest.model.device.request.DeviceGroupCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupElementCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.rest.RestControllerBase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for device group operations.
 * 
 * @author Derek Adams
 */
@RestController
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code" })
@RequestMapping(value = "/devicegroups")
@Api(value = "devicegroups")
public class DeviceGroups extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(DeviceGroups.class);

    /**
     * Create a device group.
     * 
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Create new device group")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceGroup createDeviceGroup(@RequestBody DeviceGroupCreateRequest request,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getDeviceManagement().createDeviceGroup(request);
    }

    /**
     * Get device group by unique token.
     * 
     * @param groupToken
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{groupToken}", method = RequestMethod.GET)
    @ApiOperation(value = "Get a device group by unique token")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceGroup getDeviceGroupByToken(
	    @ApiParam(value = "Unique token that identifies group", required = true) @PathVariable String groupToken,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return assureDeviceGroup(groupToken);
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
    @ApiOperation(value = "Update an existing device group")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceGroup updateDeviceGroup(
	    @ApiParam(value = "Unique token that identifies device group", required = true) @PathVariable String groupToken,
	    @RequestBody DeviceGroupCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	IDeviceGroup group = assureDeviceGroup(groupToken);
	return getDeviceManagement().updateDeviceGroup(group.getId(), request);
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
    @ApiOperation(value = "Delete device group by unique token")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceGroup deleteDeviceGroup(
	    @ApiParam(value = "Unique token that identifies device group", required = true) @PathVariable String groupToken,
	    @ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	IDeviceGroup group = assureDeviceGroup(groupToken);
	return getDeviceManagement().deleteDeviceGroup(group.getId(), force);
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
    @ApiOperation(value = "List device groups that match criteria")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceGroup> listDeviceGroups(
	    @ApiParam(value = "Role", required = false) @RequestParam(required = false) String role,
	    @ApiParam(value = "Include deleted", required = false) @RequestParam(defaultValue = "false") boolean includeDeleted,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<IDeviceGroup> results;
	if (role == null) {
	    results = getDeviceManagement().listDeviceGroups(includeDeleted, criteria);
	} else {
	    results = getDeviceManagement().listDeviceGroupsWithRole(role, includeDeleted, criteria);
	}
	DeviceGroupMarshalHelper helper = new DeviceGroupMarshalHelper();
	List<IDeviceGroup> groupsConv = new ArrayList<IDeviceGroup>();
	for (IDeviceGroup group : results.getResults()) {
	    groupsConv.add(helper.convert(group));
	}
	return new SearchResults<IDeviceGroup>(groupsConv, results.getNumResults());
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
    @ApiOperation(value = "List elements in a device group")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceGroupElement> listDeviceGroupElements(
	    @ApiParam(value = "Unique token that identifies device group", required = true) @PathVariable String groupToken,
	    @ApiParam(value = "Include detailed element information", required = false) @RequestParam(defaultValue = "false") boolean includeDetails,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	DeviceGroupElementMarshalHelper helper = new DeviceGroupElementMarshalHelper(getDeviceManagement())
		.setIncludeDetails(includeDetails);
	SearchCriteria criteria = new SearchCriteria(page, pageSize);

	IDeviceGroup group = assureDeviceGroup(groupToken);
	ISearchResults<IDeviceGroupElement> results = getDeviceManagement().listDeviceGroupElements(group.getId(),
		criteria);
	List<IDeviceGroupElement> elmConv = new ArrayList<IDeviceGroupElement>();
	for (IDeviceGroupElement elm : results.getResults()) {
	    elmConv.add(helper.convert(elm, getAssetManagement()));
	}
	return new SearchResults<IDeviceGroupElement>(elmConv, results.getNumResults());
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
    @ApiOperation(value = "Add elements to device group")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceGroupElement> addDeviceGroupElements(
	    @ApiParam(value = "Unique token that identifies device group", required = true) @PathVariable String groupToken,
	    @RequestBody List<DeviceGroupElementCreateRequest> request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	DeviceGroupElementMarshalHelper helper = new DeviceGroupElementMarshalHelper(getDeviceManagement())
		.setIncludeDetails(false);
	List<IDeviceGroupElementCreateRequest> elements = (List<IDeviceGroupElementCreateRequest>) (List<? extends IDeviceGroupElementCreateRequest>) request;

	// Validate the list of new elements.
	validateDeviceGroupElements(request, getDeviceManagement());

	IDeviceGroup group = assureDeviceGroup(groupToken);
	List<IDeviceGroupElement> results = getDeviceManagement().addDeviceGroupElements(group.getId(), elements, true);
	List<IDeviceGroupElement> converted = new ArrayList<IDeviceGroupElement>();
	for (IDeviceGroupElement elm : results) {
	    converted.add(helper.convert(elm, getAssetManagement()));
	}
	return new SearchResults<IDeviceGroupElement>(converted);
    }

    /**
     * Validate new elements to assure they reference real objects.
     * 
     * @param elements
     * @param deviceManagement
     * @throws SiteWhereException
     */
    protected void validateDeviceGroupElements(List<DeviceGroupElementCreateRequest> elements,
	    IDeviceManagement deviceManagement) throws SiteWhereException {
	for (DeviceGroupElementCreateRequest request : elements) {
	    if (request.getDeviceToken() != null) {
		IDevice device = deviceManagement.getDeviceByToken(request.getDeviceToken());
		if (device == null) {
		    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceToken, ErrorLevel.ERROR);
		}
	    }
	    if (request.getNestedGroupToken() != null) {
		IDeviceGroup group = deviceManagement.getDeviceGroupByToken(request.getNestedGroupToken());
		if (group == null) {
		    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceGroupToken, ErrorLevel.ERROR);
		}
	    }
	}
    }

    /**
     * Delete a single device group element.
     * 
     * @param groupToken
     * @param type
     * @param elementId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{groupToken}/elements/{elementId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete elements from device group")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceGroupElement> deleteDeviceGroupElement(
	    @ApiParam(value = "Unique token that identifies device group", required = true) @PathVariable String groupToken,
	    @ApiParam(value = "Element id", required = true) @PathVariable UUID elementId) throws SiteWhereException {
	List<UUID> elements = new ArrayList<>();
	elements.add(elementId);
	return deleteDeviceGroupElements(groupToken, elements);
    }

    /**
     * Delete a list of elements from an existing device group.
     * 
     * @param groupToken
     * @param elementIds
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{groupToken}/elements", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete elements from device group")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceGroupElement> deleteDeviceGroupElements(
	    @ApiParam(value = "Unique token that identifies device group", required = true) @PathVariable String groupToken,
	    @RequestBody List<UUID> elementIds) throws SiteWhereException {
	DeviceGroupElementMarshalHelper helper = new DeviceGroupElementMarshalHelper(getDeviceManagement())
		.setIncludeDetails(false);

	List<IDeviceGroupElement> results = getDeviceManagement().removeDeviceGroupElements(elementIds);
	List<IDeviceGroupElement> converted = new ArrayList<IDeviceGroupElement>();
	for (IDeviceGroupElement elm : results) {
	    converted.add(helper.convert(elm, getAssetManagement()));
	}
	return new SearchResults<IDeviceGroupElement>(converted);
    }

    /**
     * Assure that a device group exists for the given token.
     * 
     * @param groupToken
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceGroup assureDeviceGroup(String groupToken) throws SiteWhereException {
	IDeviceGroup group = getDeviceManagement().getDeviceGroupByToken(groupToken);
	if (group == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceGroupToken, ErrorLevel.ERROR);
	}
	return group;
    }

    private IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiDemux().getApiChannel();
    }

    private IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagementApiDemux().getApiChannel();
    }
}