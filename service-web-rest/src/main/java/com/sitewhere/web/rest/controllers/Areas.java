/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.sitewhere.device.marshaling.AreaMarshalHelper;
import com.sitewhere.device.marshaling.DeviceAssignmentMarshalHelper;
import com.sitewhere.rest.model.area.Zone;
import com.sitewhere.rest.model.area.request.AreaCreateRequest;
import com.sitewhere.rest.model.area.request.ZoneCreateRequest;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.asset.DeviceAlertWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceCommandInvocationWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceCommandResponseWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceLocationWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceMeasurementsWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceStateChangeWithAsset;
import com.sitewhere.rest.model.search.DateRangeSearchCriteria;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.area.AreaSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
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
 * Controller for area operations.
 * 
 * @author Derek Adams
 */
@RestController
@SiteWhereCrossOrigin
@RequestMapping(value = "/areas")
@Api(value = "areas")
public class Areas extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Areas.class);

    /**
     * Create a new area.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Create new area")
    @Secured({ SiteWhereRoles.REST })
    public IArea createArea(@RequestBody AreaCreateRequest input) throws SiteWhereException {
	return getDeviceManagement().createArea(input);
    }

    /**
     * Get information for a given area based on token.
     * 
     * @param areaToken
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{areaToken:.+}", method = RequestMethod.GET)
    @ApiOperation(value = "Get area by token")
    @Secured({ SiteWhereRoles.REST })
    public IArea getAreaByToken(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Include area type", required = false) @RequestParam(defaultValue = "true") boolean includeAreaType,
	    @ApiParam(value = "Include parent area information", required = false) @RequestParam(defaultValue = "true") boolean includeParentArea)
	    throws SiteWhereException {
	IArea existing = assertArea(areaToken);
	AreaMarshalHelper helper = new AreaMarshalHelper(getDeviceManagement(), getAssetManagement());
	helper.setIncludeAreaType(includeAreaType);
	helper.setIncludeParentArea(includeParentArea);
	return helper.convert(existing);
    }

    /**
     * Update information for an area.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{areaToken:.+}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update existing area")
    @Secured({ SiteWhereRoles.REST })
    public IArea updateArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @RequestBody AreaCreateRequest request) throws SiteWhereException {
	IArea existing = assertArea(areaToken);
	return getDeviceManagement().updateArea(existing.getId(), request);
    }

    /**
     * Get label for area based on a specific generator.
     * 
     * @param areaToken
     * @param generatorId
     * @param servletRequest
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{areaToken}/label/{generatorId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get label for area")
    public ResponseEntity<byte[]> getAreaLabel(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Generator id", required = true) @PathVariable String generatorId,
	    HttpServletRequest servletRequest, HttpServletResponse response) throws SiteWhereException {
	IArea existing = assertArea(areaToken);
	ILabel label = getLabelGeneration().getAreaLabel(generatorId, existing.getId());
	if (label == null) {
	    return ResponseEntity.notFound().build();
	}
	final HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.IMAGE_PNG);
	return new ResponseEntity<byte[]>(label.getContent(), headers, HttpStatus.OK);
    }

    /**
     * List areas matching criteria.
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "List areas matching criteria")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IArea> listAreas(
	    @ApiParam(value = "Limit to root elements", required = false) @RequestParam(required = false, defaultValue = "true") Boolean rootOnly,
	    @ApiParam(value = "Limit by parent area token", required = false) @RequestParam(required = false) String parentAreaToken,
	    @ApiParam(value = "Limit by area type token", required = false) @RequestParam(required = false) String areaTypeToken,
	    @ApiParam(value = "Include area type", required = false) @RequestParam(defaultValue = "false") boolean includeAreaType,
	    @ApiParam(value = "Include assignments", required = false) @RequestParam(defaultValue = "false") boolean includeAssignments,
	    @ApiParam(value = "Include zones", required = false) @RequestParam(defaultValue = "false") boolean includeZones,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize)
	    throws SiteWhereException {
	// Build criteria.
	AreaSearchCriteria criteria = buildAreaSearchCriteria(page, pageSize, rootOnly, parentAreaToken, areaTypeToken);

	// Perform search.
	ISearchResults<IArea> matches = getDeviceManagement().listAreas(criteria);
	AreaMarshalHelper helper = new AreaMarshalHelper(getDeviceManagement(), getAssetManagement());
	helper.setIncludeAreaType(includeAreaType);
	helper.setIncludeZones(includeZones);
	helper.setIncludeAssignments(includeAssignments);

	List<IArea> results = new ArrayList<IArea>();
	for (IArea area : matches.getResults()) {
	    results.add(helper.convert(area));
	}
	return new SearchResults<IArea>(results, matches.getNumResults());
    }

    /**
     * Build area search criteria from parameters.
     * 
     * @param page
     * @param pageSize
     * @param rootOnly
     * @param parentAreaToken
     * @param areaTypeToken
     * @return
     * @throws SiteWhereException
     */
    protected AreaSearchCriteria buildAreaSearchCriteria(int page, int pageSize, boolean rootOnly,
	    String parentAreaToken, String areaTypeToken) throws SiteWhereException {
	// Build criteria.
	AreaSearchCriteria criteria = new AreaSearchCriteria(page, pageSize);
	criteria.setRootOnly(rootOnly);

	// Look up parent area if provided.
	if (parentAreaToken != null) {
	    IArea parent = getDeviceManagement().getAreaByToken(parentAreaToken);
	    if (parent != null) {
		criteria.setParentAreaId(parent.getId());
	    }
	}

	// Look up area type if provided.
	if (areaTypeToken != null) {
	    IAreaType areaType = getDeviceManagement().getAreaTypeByToken(areaTypeToken);
	    if (areaType != null) {
		criteria.setAreaTypeId(areaType.getId());
	    }
	}

	return criteria;
    }

    /**
     * Delete information for a given area based on token.
     * 
     * @param areaToken
     * @param force
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{areaToken:.+}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete area by token")
    @Secured({ SiteWhereRoles.REST })
    public IArea deleteArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force)
	    throws SiteWhereException {
	IArea existing = assertArea(areaToken);
	return getDeviceManagement().deleteArea(existing.getId(), force);
    }

    /**
     * Get device measurements for a given area.
     * 
     * @param areaToken
     * @param count
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{areaToken}/measurements", method = RequestMethod.GET)
    @ApiOperation(value = "List measurements for an area")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
	    throws SiteWhereException {
	List<UUID> areas = resolveAreaIds(areaToken, true, getDeviceManagement());
	DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
	ISearchResults<IDeviceMeasurements> results = getDeviceEventManagement().listDeviceMeasurementsForAreas(areas,
		criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceMeasurements> wrapped = new ArrayList<IDeviceMeasurements>();
	for (IDeviceMeasurements result : results.getResults()) {
	    wrapped.add(new DeviceMeasurementsWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceMeasurements>(wrapped, results.getNumResults());
    }

    /**
     * Get device locations for a given area.
     * 
     * @param areaToken
     * @param count
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{areaToken}/locations", method = RequestMethod.GET)
    @ApiOperation(value = "List locations for an area")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceLocation> listDeviceLocationsForArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
	    throws SiteWhereException {
	List<UUID> areas = resolveAreaIds(areaToken, true, getDeviceManagement());
	DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
	ISearchResults<IDeviceLocation> results = getDeviceEventManagement().listDeviceLocationsForAreas(areas,
		criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceLocation> wrapped = new ArrayList<IDeviceLocation>();
	for (IDeviceLocation result : results.getResults()) {
	    wrapped.add(new DeviceLocationWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceLocation>(wrapped, results.getNumResults());
    }

    /**
     * Get device alerts for a given area.
     * 
     * @param areaToken
     * @param count
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{areaToken}/alerts", method = RequestMethod.GET)
    @ApiOperation(value = "List alerts for an area")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceAlert> listDeviceAlertsForArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
	    throws SiteWhereException {
	DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
	List<UUID> areas = resolveAreaIds(areaToken, true, getDeviceManagement());
	ISearchResults<IDeviceAlert> results = getDeviceEventManagement().listDeviceAlertsForAreas(areas, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceAlert> wrapped = new ArrayList<IDeviceAlert>();
	for (IDeviceAlert result : results.getResults()) {
	    wrapped.add(new DeviceAlertWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceAlert>(wrapped, results.getNumResults());
    }

    /**
     * Get device command invocations for a given area.
     * 
     * @param areaToken
     * @param count
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{areaToken}/invocations", method = RequestMethod.GET)
    @ApiOperation(value = "List command invocations for an area")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
	    throws SiteWhereException {
	List<UUID> areas = resolveAreaIds(areaToken, true, getDeviceManagement());
	DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
	ISearchResults<IDeviceCommandInvocation> results = getDeviceEventManagement()
		.listDeviceCommandInvocationsForAreas(areas, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceCommandInvocation> wrapped = new ArrayList<IDeviceCommandInvocation>();
	for (IDeviceCommandInvocation result : results.getResults()) {
	    wrapped.add(new DeviceCommandInvocationWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceCommandInvocation>(wrapped, results.getNumResults());
    }

    /**
     * Get device command responses for a given area.
     * 
     * @param areaToken
     * @param count
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{areaToken}/responses", method = RequestMethod.GET)
    @ApiOperation(value = "List command responses for an area")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
	    throws SiteWhereException {
	List<UUID> areas = resolveAreaIds(areaToken, true, getDeviceManagement());
	DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
	ISearchResults<IDeviceCommandResponse> results = getDeviceEventManagement()
		.listDeviceCommandResponsesForAreas(areas, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceCommandResponse> wrapped = new ArrayList<IDeviceCommandResponse>();
	for (IDeviceCommandResponse result : results.getResults()) {
	    wrapped.add(new DeviceCommandResponseWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceCommandResponse>(wrapped, results.getNumResults());
    }

    /**
     * Get device state changes for a given area.
     * 
     * @param areaToken
     * @param count
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{areaToken}/statechanges", method = RequestMethod.GET)
    @ApiOperation(value = "List state changes associated with an area")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
	    throws SiteWhereException {
	List<UUID> areas = resolveAreaIds(areaToken, true, getDeviceManagement());
	DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
	ISearchResults<IDeviceStateChange> results = getDeviceEventManagement().listDeviceStateChangesForAreas(areas,
		criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceStateChange> wrapped = new ArrayList<IDeviceStateChange>();
	for (IDeviceStateChange result : results.getResults()) {
	    wrapped.add(new DeviceStateChangeWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceStateChange>(wrapped, results.getNumResults());
    }

    /**
     * Find device assignments associated with an area.
     * 
     * @param areaToken
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{areaToken}/assignments", method = RequestMethod.GET)
    @ApiOperation(value = "List device assignments for an area")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<DeviceAssignment> listAssignmentsForArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Limit results to the given status", required = false) @RequestParam(required = false) String status,
	    @ApiParam(value = "Include detailed device information", required = false) @RequestParam(defaultValue = "false") boolean includeDevice,
	    @ApiParam(value = "Include detailed asset information", required = false) @RequestParam(defaultValue = "false") boolean includeAsset,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize)
	    throws SiteWhereException {
	DeviceAssignmentSearchCriteria criteria = new DeviceAssignmentSearchCriteria(page, pageSize);
	DeviceAssignmentStatus decodedStatus = (status != null) ? DeviceAssignmentStatus.valueOf(status) : null;
	if (decodedStatus != null) {
	    criteria.setStatus(decodedStatus);
	}
	List<UUID> areas = resolveAreaIds(areaToken, true, getDeviceManagement());
	criteria.setAreaIds(areas);

	ISearchResults<IDeviceAssignment> matches = getDeviceManagement().listDeviceAssignments(criteria);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(includeAsset);
	helper.setIncludeDevice(includeDevice);
	List<DeviceAssignment> converted = new ArrayList<DeviceAssignment>();
	for (IDeviceAssignment assignment : matches.getResults()) {
	    converted.add(helper.convert(assignment, getAssetManagement()));
	}
	return new SearchResults<DeviceAssignment>(converted, matches.getNumResults());
    }

    @RequestMapping(value = "/{areaToken}/assignments/lastinteraction", method = RequestMethod.GET)
    @ApiOperation(value = "List device assignments for area with qualifying last interaction date")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<DeviceAssignment> listAssignmentsWithLastInteractionDate(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Interactions after", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
	    @ApiParam(value = "Interactions before", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
	    throws SiteWhereException {
	// DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page,
	// pageSize, startDate, endDate);
	// ISearchResults<IDeviceAssignment> matches =
	// SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest))
	// .getDeviceAssignmentsWithLastInteraction(siteToken, criteria);
	// DeviceAssignmentMarshalHelper helper = new
	// DeviceAssignmentMarshalHelper(getTenant(servletRequest));
	// helper.setIncludeAsset(false);
	// List<DeviceAssignment> converted = new ArrayList<DeviceAssignment>();
	// for (IDeviceAssignment assignment : matches.getResults()) {
	// converted.add(
	// helper.convert(assignment,
	// SiteWhere.getServer().getAssetModuleManager(getTenant(servletRequest))));
	// }
	// return new SearchResults<DeviceAssignment>(converted,
	// matches.getNumResults());
	return null;
    }

    /**
     * List device assignments marked as missing by the presence manager.
     * 
     * @param siteToken
     * @param page
     * @param pageSize
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{areaToken}/assignments/missing", method = RequestMethod.GET)
    @ApiOperation(value = "List device assignments marked as missing")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<DeviceAssignment> listMissingDeviceAssignments(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize)
	    throws SiteWhereException {
	// SearchCriteria criteria = new SearchCriteria(page, pageSize);
	// ISearchResults<IDeviceAssignment> matches =
	// SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest))
	// .getMissingDeviceAssignments(siteToken, criteria);
	// DeviceAssignmentMarshalHelper helper = new
	// DeviceAssignmentMarshalHelper(getTenant(servletRequest));
	// helper.setIncludeAsset(false);
	// List<DeviceAssignment> converted = new ArrayList<DeviceAssignment>();
	// for (IDeviceAssignment assignment : matches.getResults()) {
	// converted.add(
	// helper.convert(assignment,
	// SiteWhere.getServer().getAssetModuleManager(getTenant(servletRequest))));
	// }
	// return new SearchResults<DeviceAssignment>(converted,
	// matches.getNumResults());
	return null;
    }

    /**
     * Create a new zone for an area.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{areaToken}/zones", method = RequestMethod.POST)
    @ApiOperation(value = "Create new zone for site")
    @Secured({ SiteWhereRoles.REST })
    public Zone createZone(
	    @ApiParam(value = "Token that identifies an area", required = true) @PathVariable String areaToken,
	    @RequestBody ZoneCreateRequest request) throws SiteWhereException {
	IArea existing = assertArea(areaToken);
	IZone zone = getDeviceManagement().createZone(existing.getId(), request);
	return Zone.copy(zone);
    }

    /**
     * List all zones for an area.
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{areaToken}/zones", method = RequestMethod.GET)
    @ApiOperation(value = "List zones for an area")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IZone> listZonesForSite(
	    @ApiParam(value = "Token that identifies an area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize)
	    throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	IArea existing = assertArea(areaToken);
	return getDeviceManagement().listZones(existing.getId(), criteria);
    }

    /**
     * Get area associated with token or throw an exception if invalid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IArea assertArea(String token) throws SiteWhereException {
	IArea area = getDeviceManagement().getAreaByToken(token);
	if (area == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
	}
	return area;
    }

    /**
     * Resolve tokens recursively for subareas based on area token.
     * 
     * @param areaToken
     * @param recursive
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    protected List<String> resolveAreaTokens(String areaToken, boolean recursive, IDeviceManagement deviceManagement)
	    throws SiteWhereException {
	List<IArea> areas = resolveAreas(areaToken, recursive, deviceManagement);
	List<String> tokens = new ArrayList<>();
	for (IArea area : areas) {
	    tokens.add(area.getToken());
	}
	return tokens;
    }

    /**
     * Resolve ids recursively for subareas based on area token.
     * 
     * @param areaToken
     * @param recursive
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<UUID> resolveAreaIds(String areaToken, boolean recursive, IDeviceManagement deviceManagement)
	    throws SiteWhereException {
	List<IArea> areas = resolveAreas(areaToken, recursive, deviceManagement);
	List<UUID> ids = new ArrayList<>();
	for (IArea area : areas) {
	    ids.add(area.getId());
	}
	return ids;
    }

    /**
     * Resolve areas including nested areas.
     * 
     * @param areaToken
     * @param recursive
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<IArea> resolveAreas(String areaToken, boolean recursive, IDeviceManagement deviceManagement)
	    throws SiteWhereException {
	IArea existing = deviceManagement.getAreaByToken(areaToken);
	if (existing == null) {
	    return new ArrayList<IArea>();
	}
	Map<String, IArea> resolved = new HashMap<>();
	resolveAreasRecursively(existing, recursive, resolved, deviceManagement);
	List<IArea> response = new ArrayList<>();
	response.addAll(resolved.values());
	return response;
    }

    /**
     * Resolve areas recursively.
     * 
     * @param current
     * @param recursive
     * @param matches
     * @param deviceManagement
     * @throws SiteWhereException
     */
    protected static void resolveAreasRecursively(IArea current, boolean recursive, Map<String, IArea> matches,
	    IDeviceManagement deviceManagement) throws SiteWhereException {
	matches.put(current.getToken(), current);
	List<IArea> children = deviceManagement.getAreaChildren(current.getToken());
	for (IArea child : children) {
	    resolveAreasRecursively(child, recursive, matches, deviceManagement);
	}
    }

    private IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiDemux().getApiChannel();
    }

    private IDeviceEventManagement getDeviceEventManagement() {
	return getMicroservice().getDeviceEventManagementApiDemux().getApiChannel();
    }

    private IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagementApiDemux().getApiChannel();
    }

    private ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiDemux().getApiChannel();
    }
}