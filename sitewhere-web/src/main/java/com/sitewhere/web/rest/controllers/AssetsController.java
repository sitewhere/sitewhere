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
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.rest.model.asset.AssetModule;
import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.command.ICommandResponse;
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

	/**
	 * Search for assets in an {@link IAssetModule} that meet the given criteria.
	 * 
	 * @param assetModuleId
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{assetModuleId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Search hardware assets")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SearchResults<? extends IAsset> searchAssets(
			@ApiParam(value = "Unique asset module id", required = true) @PathVariable String assetModuleId,
			@ApiParam(value = "Criteria for search", required = false) @RequestParam(defaultValue = "") String criteria)
			throws SiteWhereException {
		List<? extends IAsset> found =
				SiteWhereServer.getInstance().getAssetModuleManager().search(assetModuleId, criteria);
		SearchResults<? extends IAsset> results = new SearchResults(found);
		return results;
	}

	/**
	 * Get an asset from an {@link IAssetModule} by unique id.
	 * 
	 * @param assetModuleId
	 * @param assetId
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{assetModuleId}/{assetId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find hardware asset by unique id")
	public IAsset getAssetById(
			@ApiParam(value = "Unique asset module id", required = true) @PathVariable String assetModuleId,
			@ApiParam(value = "Unique asset id", required = true) @PathVariable String assetId)
			throws SiteWhereException {
		IAsset result =
				SiteWhereServer.getInstance().getAssetModuleManager().getAssetById(assetModuleId, assetId);
		return result;
	}

	/**
	 * List all asset modules.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/modules", method = RequestMethod.GET)
	@ResponseBody
	public List<AssetModule> listAssetModules() throws SiteWhereException {
		List<AssetModule> amConverted = new ArrayList<AssetModule>();
		List<IAssetModule<?>> modules = SiteWhereServer.getInstance().getAssetModuleManager().getModules();
		for (IAssetModule<?> module : modules) {
			amConverted.add(AssetModule.copy(module));
		}
		return amConverted;
	}

	/**
	 * List all asset modules that contain device assets.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/modules/devices", method = RequestMethod.GET)
	@ResponseBody
	public List<AssetModule> listDeviceAssetModules() throws SiteWhereException {
		List<AssetModule> amConverted = new ArrayList<AssetModule>();
		List<IAssetModule<?>> modules = SiteWhereServer.getInstance().getAssetModuleManager().getModules();
		for (IAssetModule<?> module : modules) {
			if (module.getAssetType() == AssetType.Device) {
				amConverted.add(AssetModule.copy(module));
			}
		}
		return amConverted;
	}

	/**
	 * Refresh all asset modules.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/modules/refresh", method = RequestMethod.POST)
	@ResponseBody
	public List<CommandResponse> refreshModules() throws SiteWhereException {
		List<ICommandResponse> responses =
				SiteWhereServer.getInstance().getAssetModuleManager().refreshModules();
		List<CommandResponse> converted = new ArrayList<CommandResponse>();
		for (ICommandResponse response : responses) {
			converted.add(CommandResponse.copy(response));
		}
		return converted;
	}
}