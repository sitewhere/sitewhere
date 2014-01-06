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

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAsset;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for site operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/assets")
@Api(value = "", description = "Operations related to SiteWhere assets.")
public class AssetsController extends SiteWhereController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(AssetsController.class);

	/**
	 * Search hardware assets for the given criteria.
	 * 
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/hardware", method = RequestMethod.GET)
	@ResponseBody
	@SuppressWarnings("unchecked")
	@ApiOperation(value = "Search hardware assets")
	public SearchResults<HardwareAsset> searchHardwareAssets(
			@ApiParam(value = "Criteria for search", required = false) @RequestParam(defaultValue = "") String criteria)
			throws SiteWhereException {
		List<HardwareAsset> found = (List<HardwareAsset>) SiteWhereServer.getInstance()
				.getAssetModuleManager().search(AssetType.Hardware, criteria);
		SearchResults<HardwareAsset> results = new SearchResults<HardwareAsset>(found);
		return results;
	}

	/**
	 * Get a hardware asset by unique id.
	 * 
	 * @param assetId
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/hardware/{assetId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find hardware asset by unique id")
	public HardwareAsset getHardwareAssetById(
			@ApiParam(value = "Unique asset id", required = true) @PathVariable String assetId)
			throws SiteWhereException {
		IAsset result = SiteWhereServer.getInstance().getAssetModuleManager()
				.getAssetById(AssetType.Hardware, assetId);
		if (result instanceof HardwareAsset) {
			return (HardwareAsset) result;
		} else {
			LOGGER.error("Result could not be marshaled as a hardware asset.");
			return null;
		}
	}

	/**
	 * Search device assets for the given criteria.
	 * 
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/devices", method = RequestMethod.GET)
	@ResponseBody
	@SuppressWarnings("unchecked")
	@ApiOperation(value = "Search device assets")
	public SearchResults<HardwareAsset> searchDeviceAssets(
			@ApiParam(value = "Criteria for search", required = false) @RequestParam(defaultValue = "") String criteria)
			throws SiteWhereException {
		List<HardwareAsset> found = (List<HardwareAsset>) SiteWhereServer.getInstance()
				.getAssetModuleManager().search(AssetType.Device, criteria);
		SearchResults<HardwareAsset> results = new SearchResults<HardwareAsset>(found);
		return results;
	}

	/**
	 * Get a device asset by unique id.
	 * 
	 * @param assetId
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/devices/{assetId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find device asset by unique id")
	public HardwareAsset getDeviceAssetById(
			@ApiParam(value = "Unique asset id", required = true) @PathVariable String assetId)
			throws SiteWhereException {
		IAsset result = SiteWhereServer.getInstance().getAssetModuleManager()
				.getAssetById(AssetType.Device, assetId);
		if (result instanceof HardwareAsset) {
			return (HardwareAsset) result;
		} else {
			LOGGER.error("Result could not be marshaled as a hardware asset.");
			return null;
		}
	}

	/**
	 * Search person assets for the given criteria.
	 * 
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/people", method = RequestMethod.GET)
	@ResponseBody
	@SuppressWarnings("unchecked")
	@ApiOperation(value = "Search person assets")
	public SearchResults<PersonAsset> searchPersonAssets(
			@ApiParam(value = "Criteria for search", required = false) @RequestParam(defaultValue = "") String criteria)
			throws SiteWhereException {
		List<PersonAsset> found = (List<PersonAsset>) SiteWhereServer.getInstance().getAssetModuleManager()
				.search(AssetType.Person, criteria);
		SearchResults<PersonAsset> results = new SearchResults<PersonAsset>(found);
		return results;
	}

	/**
	 * Get a person asset by unique id.
	 * 
	 * @param assetId
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/people/{assetId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find person asset by unique id")
	public PersonAsset getPersonAssetById(
			@ApiParam(value = "Unique asset id", required = true) @PathVariable String assetId)
			throws SiteWhereException {
		IAsset result = SiteWhereServer.getInstance().getAssetModuleManager()
				.getAssetById(AssetType.Person, assetId);
		if (result instanceof PersonAsset) {
			return (PersonAsset) result;
		} else {
			LOGGER.error("Result could not be marshaled as a person asset.");
			return null;
		}
	}
}