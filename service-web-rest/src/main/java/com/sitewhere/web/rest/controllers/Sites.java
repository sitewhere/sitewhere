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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.device.marshaling.DeviceAssignmentMarshalHelper;
import com.sitewhere.device.marshaling.SiteMarshalHelper;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.device.asset.DeviceAlertWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceCommandInvocationWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceCommandResponseWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceLocationWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceMeasurementsWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceStateChangeWithAsset;
import com.sitewhere.rest.model.device.request.SiteCreateRequest;
import com.sitewhere.rest.model.device.request.ZoneCreateRequest;
import com.sitewhere.rest.model.search.DateRangeSearchCriteria;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.AssignmentSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.rest.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for site operations.
 * 
 * @author Derek Adams
 */
@Controller
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code" })
@RequestMapping(value = "/sites")
@Api(value = "sites")
public class Sites extends RestController {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /**
     * Create a new site.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Create new site")
    @Secured({ SiteWhereRoles.REST })
    public Site createSite(@RequestBody SiteCreateRequest input, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	ISite site = getDeviceManagement().createSite(input);
	return Site.copy(site);
    }

    /**
     * Get information for a given site based on site token.
     * 
     * @param siteToken
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{siteToken}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get site by unique token")
    @Secured({ SiteWhereRoles.REST })
    public Site getSiteByToken(
	    @ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	ISite site = getDeviceManagement().getSiteByToken(siteToken);
	if (site == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR,
		    HttpServletResponse.SC_NOT_FOUND);
	}
	return Site.copy(site);
    }

    /**
     * Update information for a site.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{siteToken}", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "Update existing site")
    @Secured({ SiteWhereRoles.REST })
    public Site updateSite(
	    @ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
	    @RequestBody SiteCreateRequest request, HttpServletRequest servletRequest) throws SiteWhereException {
	ISite site = getDeviceManagement().updateSite(siteToken, request);
	return Site.copy(site);
    }

    /**
     * Delete information for a given site based on site token.
     * 
     * @param siteToken
     * @param force
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{siteToken}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation(value = "Delete site by unique token")
    @Secured({ SiteWhereRoles.REST })
    public Site deleteSiteByToken(
	    @ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
	    @ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	ISite site = getDeviceManagement().deleteSite(siteToken, force);
	return Site.copy(site);
    }

    /**
     * List all sites and wrap as search results.
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List sites matching criteria")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<ISite> listSites(
	    @ApiParam(value = "Include assignments", required = false) @RequestParam(defaultValue = "false") boolean includeAssignments,
	    @ApiParam(value = "Include zones", required = false) @RequestParam(defaultValue = "false") boolean includeZones,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<ISite> matches = getDeviceManagement().listSites(criteria);
	SiteMarshalHelper helper = new SiteMarshalHelper(getDeviceManagement(), getAssetResolver());
	helper.setIncludeZones(includeZones);
	helper.setIncludeAssignements(includeAssignments);

	List<ISite> results = new ArrayList<ISite>();
	for (ISite site : matches.getResults()) {
	    results.add(helper.convert(site));
	}
	return new SearchResults<ISite>(results, matches.getNumResults());
    }

    /**
     * Get device measurements for a given site.
     * 
     * @param siteToken
     * @param count
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{siteToken}/measurements", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List measurements for site")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForSite(
	    @ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	ISite site = assertSite(siteToken);
	DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
	ISearchResults<IDeviceMeasurements> results = getDeviceEventManagement().listDeviceMeasurementsForSite(site,
		criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceMeasurements> wrapped = new ArrayList<IDeviceMeasurements>();
	for (IDeviceMeasurements result : results.getResults()) {
	    wrapped.add(new DeviceMeasurementsWithAsset(result, getAssetResolver()));
	}
	return new SearchResults<IDeviceMeasurements>(wrapped, results.getNumResults());
    }

    /**
     * Get device locations for a given site.
     * 
     * @param siteToken
     * @param count
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{siteToken}/locations", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List locations for site")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceLocation> listDeviceLocationsForSite(
	    @ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	ISite site = assertSite(siteToken);
	DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
	ISearchResults<IDeviceLocation> results = getDeviceEventManagement().listDeviceLocationsForSite(site, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceLocation> wrapped = new ArrayList<IDeviceLocation>();
	for (IDeviceLocation result : results.getResults()) {
	    wrapped.add(new DeviceLocationWithAsset(result, getAssetResolver()));
	}
	return new SearchResults<IDeviceLocation>(wrapped, results.getNumResults());
    }

    /**
     * Get device alerts for a given site.
     * 
     * @param siteToken
     * @param count
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{siteToken}/alerts", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List alerts for site")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceAlert> listDeviceAlertsForSite(
	    @ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
	ISite site = getDeviceManagement().getSiteByToken(siteToken);
	ISearchResults<IDeviceAlert> results = getDeviceEventManagement().listDeviceAlertsForSite(site, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceAlert> wrapped = new ArrayList<IDeviceAlert>();
	for (IDeviceAlert result : results.getResults()) {
	    wrapped.add(new DeviceAlertWithAsset(result, getAssetResolver()));
	}
	return new SearchResults<IDeviceAlert>(wrapped, results.getNumResults());
    }

    /**
     * Get device command invocations for a given site.
     * 
     * @param siteToken
     * @param count
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{siteToken}/invocations", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List command invocations for a site")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForSite(
	    @ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	ISite site = assertSite(siteToken);
	DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
	ISearchResults<IDeviceCommandInvocation> results = getDeviceEventManagement()
		.listDeviceCommandInvocationsForSite(site, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceCommandInvocation> wrapped = new ArrayList<IDeviceCommandInvocation>();
	for (IDeviceCommandInvocation result : results.getResults()) {
	    wrapped.add(new DeviceCommandInvocationWithAsset(result, getAssetResolver()));
	}
	return new SearchResults<IDeviceCommandInvocation>(wrapped, results.getNumResults());
    }

    /**
     * Get device command responses for a given site.
     * 
     * @param siteToken
     * @param count
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{siteToken}/responses", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List command responses for site")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForSite(
	    @ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	ISite site = assertSite(siteToken);
	DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
	ISearchResults<IDeviceCommandResponse> results = getDeviceEventManagement()
		.listDeviceCommandResponsesForSite(site, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceCommandResponse> wrapped = new ArrayList<IDeviceCommandResponse>();
	for (IDeviceCommandResponse result : results.getResults()) {
	    wrapped.add(new DeviceCommandResponseWithAsset(result, getAssetResolver()));
	}
	return new SearchResults<IDeviceCommandResponse>(wrapped, results.getNumResults());
    }

    /**
     * Get device state changes for a given site.
     * 
     * @param siteToken
     * @param count
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{siteToken}/statechanges", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List state changes associated with a site")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForSite(
	    @ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	ISite site = assertSite(siteToken);
	DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
	ISearchResults<IDeviceStateChange> results = getDeviceEventManagement().listDeviceStateChangesForSite(site,
		criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceStateChange> wrapped = new ArrayList<IDeviceStateChange>();
	for (IDeviceStateChange result : results.getResults()) {
	    wrapped.add(new DeviceStateChangeWithAsset(result, getAssetResolver()));
	}
	return new SearchResults<IDeviceStateChange>(wrapped, results.getNumResults());
    }

    /**
     * Find device assignments associated with a site.
     * 
     * @param siteToken
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{siteToken}/assignments", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List device assignments for site")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<DeviceAssignment> listAssignmentsForSite(
	    @ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
	    @ApiParam(value = "Limit results to the given status", required = false) @RequestParam(required = false) String status,
	    @ApiParam(value = "Include detailed device information", required = false) @RequestParam(defaultValue = "false") boolean includeDevice,
	    @ApiParam(value = "Include detailed asset information", required = false) @RequestParam(defaultValue = "false") boolean includeAsset,
	    @ApiParam(value = "Include detailed site information", required = false) @RequestParam(defaultValue = "false") boolean includeSite,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	AssignmentSearchCriteria criteria = new AssignmentSearchCriteria(page, pageSize);
	DeviceAssignmentStatus decodedStatus = (status != null) ? DeviceAssignmentStatus.valueOf(status) : null;
	if (decodedStatus != null) {
	    criteria.setStatus(decodedStatus);
	}
	ISearchResults<IDeviceAssignment> matches = getDeviceManagement().getDeviceAssignmentsForSite(siteToken,
		criteria);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(includeAsset);
	helper.setIncludeDevice(includeDevice);
	helper.setIncludeSite(includeSite);
	List<DeviceAssignment> converted = new ArrayList<DeviceAssignment>();
	for (IDeviceAssignment assignment : matches.getResults()) {
	    converted.add(helper.convert(assignment, getAssetResolver()));
	}
	return new SearchResults<DeviceAssignment>(converted, matches.getNumResults());
    }

    @RequestMapping(value = "/{siteToken}/assignments/lastinteraction", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List device assignments for site with qualifying last interaction date")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<DeviceAssignment> listAssignmentsWithLastInteractionDate(
	    @ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Interactions after", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
	    @ApiParam(value = "Interactions before", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
	    HttpServletRequest servletRequest) throws SiteWhereException {
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
    @RequestMapping(value = "/{siteToken}/assignments/missing", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List device assignments marked as missing")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<DeviceAssignment> listMissingDeviceAssignments(
	    @ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
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
     * Create a new zone for a site.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{siteToken}/zones", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Create new zone for site")
    @Secured({ SiteWhereRoles.REST })
    public Zone createZone(@ApiParam(value = "Unique site token", required = true) @PathVariable String siteToken,
	    @RequestBody ZoneCreateRequest request, HttpServletRequest servletRequest) throws SiteWhereException {
	IZone zone = getDeviceManagement().createZone(siteToken, request);
	return Zone.copy(zone);
    }

    /**
     * List all zones for a site.
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{siteToken}/zones", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List zones for site")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IZone> listZonesForSite(
	    @ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	return getDeviceManagement().listZones(siteToken, criteria);
    }

    /**
     * Get site associated with token or throw an exception if invalid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected ISite assertSite(String token) throws SiteWhereException {
	ISite site = getDeviceManagement().getSiteByToken(token);
	if (site == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
	}
	return site;
    }

    private IDeviceManagement getDeviceManagement() {
	return null;
    }

    private IDeviceEventManagement getDeviceEventManagement() {
	return null;
    }

    private IAssetResolver getAssetResolver() {
	return null;
    }
}