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

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.rest.model.device.command.DeviceCommandNamespace;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.DeviceCommandSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.IDeviceCommandNamespace;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.annotation.SiteWhereCrossOrigin;
import com.sitewhere.web.rest.RestControllerBase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for device command operations.
 * 
 * @author Derek Adams
 */
@RestController
@SiteWhereCrossOrigin
@RequestMapping(value = "/commands")
@Api(value = "commands")
public class DeviceCommands extends RestControllerBase {

    /**
     * List commands that match the given criteria.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "List device commands that match criteria.")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceCommand> listDeviceCommands(
	    @ApiParam(value = "Device type token", required = false) @RequestParam(required = false) String deviceTypeToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize)
	    throws SiteWhereException {
	DeviceCommandSearchCriteria criteria = new DeviceCommandSearchCriteria(page, pageSize);

	// Look up device type if specified.
	IDeviceType deviceType = null;
	if (deviceTypeToken != null) {
	    deviceType = getDeviceManagement().getDeviceTypeByToken(deviceTypeToken);
	    if (deviceType == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	    }
	    criteria.setDeviceTypeId(deviceType.getId());
	}

	return getDeviceManagement().listDeviceCommands(criteria);
    }

    /**
     * List commands grouped by namespace.
     * 
     * @param token
     * @param includeDeleted
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/namespaces", method = RequestMethod.GET)
    @ApiOperation(value = "List device commands by namespace")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceCommandNamespace> listAllDeviceCommandsByNamespace(
	    @ApiParam(value = "Device type token", required = false) @RequestParam(defaultValue = "false") String deviceTypeToken)
	    throws SiteWhereException {
	DeviceCommandSearchCriteria criteria = new DeviceCommandSearchCriteria(1, 0);

	// Look up device type if specified.
	IDeviceType deviceType = null;
	if (deviceTypeToken != null) {
	    deviceType = getDeviceManagement().getDeviceTypeByToken(deviceTypeToken);
	    if (deviceType == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	    }
	    criteria.setDeviceTypeId(deviceType.getId());
	}

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
	return new SearchResults<IDeviceCommandNamespace>(namespaces);
    }

    private IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiChannel();
    }
}