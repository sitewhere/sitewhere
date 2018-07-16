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
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.communication.protobuf.DeviceTypeProtoBuilder;
import com.sitewhere.device.marshaling.DeviceTypeMarshalHelper;
import com.sitewhere.rest.model.device.request.DeviceTypeCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.label.ILabelGeneration;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.annotation.SiteWhereCrossOrigin;
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
@SiteWhereCrossOrigin
@RequestMapping(value = "/devicetypes")
@Api(value = "devicetypes")
public class DeviceTypes extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(DeviceTypes.class);

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
	IDeviceType result = getDeviceManagement().createDeviceType(request);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	return helper.convert(result);
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
	return helper.convert(result);
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
	return helper.convert(result);
    }

    /**
     * Get label for device type based on a specific generator.
     * 
     * @param token
     * @param generatorId
     * @param servletRequest
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{token}/label/{generatorId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get label for device type")
    public ResponseEntity<byte[]> getDeviceTypeLabel(
	    @ApiParam(value = "Token", required = true) @PathVariable String token,
	    @ApiParam(value = "Generator id", required = true) @PathVariable String generatorId,
	    HttpServletRequest servletRequest, HttpServletResponse response) throws SiteWhereException {
	IDeviceType deviceType = assertDeviceTypeByToken(token);
	ILabel label = getLabelGeneration().getDeviceTypeLabel(generatorId, deviceType.getId());
	if (label == null) {
	    return ResponseEntity.notFound().build();
	}
	final HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.IMAGE_PNG);
	return new ResponseEntity<byte[]>(label.getContent(), headers, HttpStatus.OK);
    }

    /**
     * List device types that meet the given criteria.
     * 
     * @param includeAsset
     * @param page
     * @param pageSize
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "List device types that match criteria")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceType> listDeviceTypes(
	    @ApiParam(value = "Include detailed asset information", required = false) @RequestParam(defaultValue = "true") boolean includeAsset,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<IDeviceType> results = getDeviceManagement().listDeviceTypes(criteria);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	List<IDeviceType> typesConv = new ArrayList<IDeviceType>();
	for (IDeviceType type : results.getResults()) {
	    typesConv.add(helper.convert(type));
	}
	return new SearchResults<IDeviceType>(typesConv, results.getNumResults());
    }

    /**
     * Delete an existing device type.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{token}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete existing device type")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceType deleteDeviceType(@ApiParam(value = "Token", required = true) @PathVariable String token)
	    throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	IDeviceType result = getDeviceManagement().deleteDeviceType(existing.getId());
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	return helper.convert(result);
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

    private ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiDemux().getApiChannel();
    }
}