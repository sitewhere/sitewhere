/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.core.user.SitewhereRoles;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.device.asset.DeviceAlertWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceLocationWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceMeasurementsWithAsset;
import com.sitewhere.rest.model.device.request.SiteCreateRequest;
import com.sitewhere.rest.model.device.request.ZoneCreateRequest;
import com.sitewhere.rest.model.search.DateRangeSearchCriteria;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.device.IDeviceAlert;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceLocation;
import com.sitewhere.spi.device.IDeviceMeasurements;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.web.rest.model.DeviceAssignmentMarshalHelper;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for site operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/sites")
@Api(value = "", description = "Operations related to SiteWhere sites.")
public class SitesController extends SiteWhereController {

	/**
	 * Create a new site.
	 * 
	 * @param input
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a new site")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_SITES })
	public Site createSite(@RequestBody SiteCreateRequest input) throws SiteWhereException {
		ISite site = SiteWhereServer.getInstance().getDeviceManagement().createSite(input);
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
	@ApiOperation(value = "Get a site by unique token")
	public Site getSiteByToken(
			@ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken)
			throws SiteWhereException {
		ISite site = SiteWhereServer.getInstance().getDeviceManagement().getSiteByToken(siteToken);
		if (site == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
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
	@ApiOperation(value = "Update an existing site")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_SITES })
	public Site updateSite(
			@ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
			@RequestBody SiteCreateRequest request) throws SiteWhereException {
		ISite site = SiteWhereServer.getInstance().getDeviceManagement().updateSite(siteToken, request);
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
	@ApiOperation(value = "Delete a site by unique token")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_SITES })
	public Site deleteSiteByToken(
			@ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
			@ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force)
			throws SiteWhereException {
		ISite site = SiteWhereServer.getInstance().getDeviceManagement().deleteSite(siteToken, force);
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
	@ApiOperation(value = "List all sites")
	public ISearchResults<ISite> listSites(
			@ApiParam(value = "Page Number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize)
			throws SiteWhereException {
		SearchCriteria criteria = new SearchCriteria(page, pageSize);
		return SiteWhereServer.getInstance().getDeviceManagement().listSites(criteria);
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
	@ApiOperation(value = "List measurements associated with a site")
	public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForSite(
			@ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
			throws SiteWhereException {
		DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
		ISearchResults<IDeviceMeasurements> results =
				SiteWhereServer.getInstance().getDeviceManagement().listDeviceMeasurementsForSite(siteToken,
						criteria);

		// Marshal with asset info since multiple assignments might match.
		List<IDeviceMeasurements> wrapped = new ArrayList<IDeviceMeasurements>();
		IAssetModuleManager assets = SiteWhereServer.getInstance().getAssetModuleManager();
		for (IDeviceMeasurements result : results.getResults()) {
			wrapped.add(new DeviceMeasurementsWithAsset(result, assets));
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
	@ApiOperation(value = "List locations associated with a site")
	public ISearchResults<IDeviceLocation> listDeviceLocationsForSite(
			@ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
			throws SiteWhereException {
		DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
		ISearchResults<IDeviceLocation> results =
				SiteWhereServer.getInstance().getDeviceManagement().listDeviceLocationsForSite(siteToken,
						criteria);

		// Marshal with asset info since multiple assignments might match.
		List<IDeviceLocation> wrapped = new ArrayList<IDeviceLocation>();
		IAssetModuleManager assets = SiteWhereServer.getInstance().getAssetModuleManager();
		for (IDeviceLocation result : results.getResults()) {
			wrapped.add(new DeviceLocationWithAsset(result, assets));
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
	@ApiOperation(value = "List alerts associated with a site")
	public ISearchResults<IDeviceAlert> listDeviceAlertsForSite(
			@ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
			throws SiteWhereException {
		DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
		ISearchResults<IDeviceAlert> results =
				SiteWhereServer.getInstance().getDeviceManagement().listDeviceAlertsForSite(siteToken,
						criteria);

		// Marshal with asset info since multiple assignments might match.
		List<IDeviceAlert> wrapped = new ArrayList<IDeviceAlert>();
		IAssetModuleManager assets = SiteWhereServer.getInstance().getAssetModuleManager();
		for (IDeviceAlert result : results.getResults()) {
			wrapped.add(new DeviceAlertWithAsset(result, assets));
		}
		return new SearchResults<IDeviceAlert>(wrapped, results.getNumResults());
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
	@ApiOperation(value = "List device assignments associated with a site")
	public ISearchResults<DeviceAssignment> findAssignmentsForSite(
			@ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
			@ApiParam(value = "Include detailed device information", required = false) @RequestParam(defaultValue = "false") boolean includeDevice,
			@ApiParam(value = "Include detailed asset information", required = false) @RequestParam(defaultValue = "false") boolean includeAsset,
			@ApiParam(value = "Include detailed site information", required = false) @RequestParam(defaultValue = "false") boolean includeSite,
			@ApiParam(value = "Page Number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize)
			throws SiteWhereException {
		SearchCriteria criteria = new SearchCriteria(page, pageSize);
		ISearchResults<IDeviceAssignment> matches =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceAssignmentsForSite(siteToken,
						criteria);
		DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
		helper.setIncludeAsset(includeAsset);
		helper.setIncludeDevice(includeDevice);
		helper.setIncludeSite(includeSite);
		List<DeviceAssignment> converted = new ArrayList<DeviceAssignment>();
		for (IDeviceAssignment assignment : matches.getResults()) {
			converted.add(helper.convert(assignment, SiteWhereServer.getInstance().getAssetModuleManager()));
		}
		return new SearchResults<DeviceAssignment>(converted, matches.getNumResults());
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
	@ApiOperation(value = "Create a new zone associated with a site")
	public Zone createZone(
			@ApiParam(value = "Unique site token", required = true) @PathVariable String siteToken,
			@RequestBody ZoneCreateRequest request) throws SiteWhereException {
		ISite site = SiteWhereServer.getInstance().getDeviceManagement().getSiteByToken(siteToken);
		if (site == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
		}
		IZone zone = SiteWhereServer.getInstance().getDeviceManagement().createZone(site, request);
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
	@ApiOperation(value = "List zones associated with a site")
	public ISearchResults<IZone> listZonesForSite(
			@ApiParam(value = "Unique token that identifies site", required = true) @PathVariable String siteToken,
			@ApiParam(value = "Page Number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize)
			throws SiteWhereException {
		SearchCriteria criteria = new SearchCriteria(page, pageSize);
		return SiteWhereServer.getInstance().getDeviceManagement().listZones(siteToken, criteria);
	}
}