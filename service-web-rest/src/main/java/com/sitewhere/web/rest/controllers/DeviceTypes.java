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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.communication.protobuf.DeviceTypeProtoBuilder;
import com.sitewhere.device.marshaling.DeviceTypeMarshalHelper;
import com.sitewhere.rest.model.device.command.DeviceCommandNamespace;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceStatusCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceTypeCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.DeviceSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.IDeviceCommandNamespace;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.rest.RestControllerBase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for device specification operations.
 * 
 * @author Derek Adams
 */
@RestController
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code" })
@RequestMapping(value = "/devicetypes")
@Api(value = "devicetypes")
public class DeviceTypes extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /**
     * Create a device type.
     * 
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Create new device type")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceType createDeviceType(@RequestBody DeviceTypeCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	IAsset asset = getAssetResolver().getAssetModuleManagement().getAsset(request.getAssetReference());
	if (asset == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAssetReferenceId, ErrorLevel.ERROR,
		    HttpServletResponse.SC_NOT_FOUND);
	}
	IDeviceType result = getDeviceManagement().createDeviceType(request);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	return helper.convert(result, getAssetResolver());
    }

    /**
     * Get a device type by unique token.
     * 
     * @param token
     * @param includeAsset
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{token}", method = RequestMethod.GET)
    @ApiOperation(value = "Get device type by unique token")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceType getDeviceTypeByToken(@ApiParam(value = "Token", required = true) @PathVariable String token,
	    @ApiParam(value = "Include detailed asset information", required = false) @RequestParam(defaultValue = "true") boolean includeAsset)
	    throws SiteWhereException {
	IDeviceType result = assertDeviceTypeByToken(token);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(includeAsset);
	return helper.convert(result, getAssetResolver());
    }

    /**
     * Get default protobuf definition for device type.
     * 
     * @param token
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{token}/proto", method = RequestMethod.GET)
    @ApiOperation(value = "Get specification GPB by unique token")
    @Secured({ SiteWhereRoles.REST })
    public String getDeviceTypeProtoByToken(@ApiParam(value = "Token", required = true) @PathVariable String token,
	    HttpServletResponse response) throws SiteWhereException {
	IDeviceType deviceType = assertDeviceTypeByToken(token);
	String proto = DeviceTypeProtoBuilder.getProtoForDeviceType(deviceType, getDeviceManagement());
	response.setContentType("text/plain");
	return proto;
    }

    /**
     * Get default protobuf definition file for device type.
     * 
     * @param hardwareId
     * @return
     */
    @RequestMapping(value = "/{token}/spec.proto", method = RequestMethod.GET)
    @ApiOperation(value = "Get device type GPB file by unique token")
    @Secured({ SiteWhereRoles.REST })
    public ResponseEntity<byte[]> getDeviceTypeProtoFileByToken(
	    @ApiParam(value = "Token", required = true) @PathVariable String token, HttpServletRequest servletRequest,
	    HttpServletResponse response) throws SiteWhereException {
	IDeviceType deviceType = assertDeviceTypeByToken(token);
	String proto = DeviceTypeProtoBuilder.getProtoForDeviceType(deviceType, getDeviceManagement());

	final HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	headers.set("Content-Disposition", "attachment; filename=Spec_" + deviceType.getToken() + ".proto");
	return new ResponseEntity<byte[]>(proto.getBytes(), headers, HttpStatus.OK);
    }

    /**
     * Update an existing device type.
     * 
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{token}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update existing device type")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceType updateDeviceType(@ApiParam(value = "Token", required = true) @PathVariable String token,
	    @RequestBody DeviceTypeCreateRequest request, HttpServletRequest servletRequest) throws SiteWhereException {
	IDeviceType deviceType = assertDeviceTypeByToken(token);
	IDeviceType result = getDeviceManagement().updateDeviceType(deviceType.getId(), request);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	return helper.convert(result, getAssetResolver());
    }

    /**
     * List device types that meet the given criteria.
     * 
     * @param includeDeleted
     * @param includeAsset
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "List device types that match criteria")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceType> listDeviceTypes(
	    @ApiParam(value = "Include deleted", required = false) @RequestParam(defaultValue = "false") boolean includeDeleted,
	    @ApiParam(value = "Include detailed asset information", required = false) @RequestParam(defaultValue = "true") boolean includeAsset,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<IDeviceType> results = getDeviceManagement().listDeviceTypes(includeDeleted, criteria);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(includeAsset);
	List<IDeviceType> typesConv = new ArrayList<IDeviceType>();
	for (IDeviceType type : results.getResults()) {
	    typesConv.add(helper.convert(type, getAssetResolver()));
	}
	Collections.sort(typesConv, new Comparator<IDeviceType>() {
	    public int compare(IDeviceType o1, IDeviceType o2) {
		return o1.getName().compareTo(o2.getName());
	    }
	});
	return new SearchResults<IDeviceType>(typesConv, results.getNumResults());
    }

    /**
     * Delete an existing device type.
     * 
     * @param token
     * @param force
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{token}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete existing device specification")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceType deleteDeviceType(@ApiParam(value = "Token", required = true) @PathVariable String token,
	    @ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	IDeviceManagement devices = getDeviceManagement();

	// Do not allow delete if specification is being used
	// (SITEWHERE-267)
	DeviceSearchCriteria criteria = new DeviceSearchCriteria(1, 0, null, null);
	criteria.setDeviceTypeToken(token);
	criteria.setExcludeAssigned(false);
	ISearchResults<IDevice> matches = devices.listDevices(false, criteria);
	if (matches.getNumResults() > 0) {
	    throw new SiteWhereException("Unable to delete device type. Device type is being used by "
		    + matches.getNumResults() + " devices.");
	}

	IDeviceType existing = assertDeviceTypeByToken(token);
	IDeviceType result = devices.deleteDeviceType(existing.getId(), force);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	return helper.convert(result, getAssetResolver());
    }

    /**
     * Create a new command for a device specification.
     * 
     * @param token
     * @param request
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{token}/commands", method = RequestMethod.POST)
    @ApiOperation(value = "Create device command for specification.")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceCommand createDeviceCommand(
	    @ApiParam(value = "Specification token", required = true) @PathVariable String token,
	    @RequestBody DeviceCommandCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	IDeviceCommand result = getDeviceManagement().createDeviceCommand(existing.getId(), request);
	return result;
    }

    /**
     * List commands for a device specification.
     * 
     * @param token
     * @param includeDeleted
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{token}/commands", method = RequestMethod.GET)
    @ApiOperation(value = "List device commands for specification")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceCommand> listDeviceCommands(
	    @ApiParam(value = "Specification token", required = true) @PathVariable String token,
	    @ApiParam(value = "Include deleted", required = false) @RequestParam(defaultValue = "false") boolean includeDeleted,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	List<IDeviceCommand> results = getDeviceManagement().listDeviceCommands(existing.getId(), includeDeleted);
	Collections.sort(results, new Comparator<IDeviceCommand>() {
	    public int compare(IDeviceCommand o1, IDeviceCommand o2) {
		if (o1.getName().equals(o2.getName())) {
		    return o1.getNamespace().compareTo(o2.getNamespace());
		}
		return o1.getName().compareTo(o2.getName());
	    }
	});
	return new SearchResults<IDeviceCommand>(results);
    }

    /**
     * List commands grouped by namespace.
     * 
     * @param token
     * @param includeDeleted
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{token}/namespaces", method = RequestMethod.GET)
    @ApiOperation(value = "List device commands by namespace")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceCommandNamespace> listDeviceCommandsByNamespace(
	    @ApiParam(value = "Token", required = true) @PathVariable String token,
	    @ApiParam(value = "Include deleted", required = false) @RequestParam(defaultValue = "false") boolean includeDeleted,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	List<IDeviceCommand> results = getDeviceManagement().listDeviceCommands(existing.getId(), includeDeleted);
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
	return new SearchResults<IDeviceCommandNamespace>(namespaces);
    }

    /**
     * Create a device status for a device specification.
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = "/{token}/statuses", method = RequestMethod.POST)
    @ApiOperation(value = "Create device status for specification.")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceStatus createDeviceStatus(@ApiParam(value = "Token", required = true) @PathVariable String token,
	    @RequestBody DeviceStatusCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	return getDeviceManagement().createDeviceStatus(existing.getId(), request);
    }

    /**
     * Get device status by unique status code.
     * 
     * @param token
     * @param code
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{token}/statuses/{code}", method = RequestMethod.GET)
    @ApiOperation(value = "Get device status by unique code")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceStatus getDeviceStatus(@ApiParam(value = "Token", required = true) @PathVariable String token,
	    @ApiParam(value = "Code", required = true) @PathVariable String code, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	return getDeviceManagement().getDeviceStatusByCode(existing.getId(), code);
    }

    /**
     * Update information for an existing device status entry.
     * 
     * @param token
     * @param code
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{token}/statuses/{code}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update existing device status entry")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceStatus updateDeviceStatus(@ApiParam(value = "Token", required = true) @PathVariable String token,
	    @ApiParam(value = "Code", required = true) @PathVariable String code,
	    @RequestBody DeviceStatusCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	return getDeviceManagement().updateDeviceStatus(existing.getId(), code, request);
    }

    @RequestMapping(value = "/{token}/statuses", method = RequestMethod.GET)
    @ApiOperation(value = "List device statuses for specification")
    @Secured({ SiteWhereRoles.REST })
    public List<IDeviceStatus> listDeviceStatuses(
	    @ApiParam(value = "Token", required = true) @PathVariable String token, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	List<IDeviceStatus> results = getDeviceManagement().listDeviceStatuses(existing.getId());
	Collections.sort(results, new Comparator<IDeviceStatus>() {
	    public int compare(IDeviceStatus o1, IDeviceStatus o2) {
		return o1.getName().compareTo(o2.getName());
	    }
	});
	return results;
    }

    /**
     * Delete information for an existing device status entry.
     * 
     * @param token
     * @param code
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{token}/statuses/{code}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Update existing device status entry")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceStatus deleteDeviceStatus(@ApiParam(value = "Token", required = true) @PathVariable String token,
	    @ApiParam(value = "Code", required = true) @PathVariable String code, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	return getDeviceManagement().deleteDeviceStatus(existing.getId(), code);
    }

    /**
     * Gets a device type by token and throws an exception if not found.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceType assertDeviceTypeByToken(String token) throws SiteWhereException {
	IDeviceType result = getDeviceManagement().getDeviceTypeByToken(token);
	if (result == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}
	return result;
    }

    private IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiDemux().getApiChannel();
    }

    private IAssetResolver getAssetResolver() {
	return getMicroservice().getAssetResolver();
    }
}