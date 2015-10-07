/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.rest.model.asset.AssetCategory;
import com.sitewhere.rest.model.asset.AssetModule;
import com.sitewhere.rest.model.asset.request.AssetCategoryCreateRequest;
import com.sitewhere.rest.model.asset.request.HardwareAssetCreateRequest;
import com.sitewhere.rest.model.asset.request.LocationAssetCreateRequest;
import com.sitewhere.rest.model.asset.request.PersonAssetCreateRequest;
import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.device.IDeviceAssignment;

public class Assets {

	public static class GetAssetModuleResponse extends AssetModule {

		public GetAssetModuleResponse() throws SiteWhereException {
			setId(ExampleData.AM_PERSONS.getId());
			setName(ExampleData.AM_PERSONS.getName());
			setAssetType(ExampleData.AM_PERSONS.getAssetType());
		}
	}

	public static class SearchAssetModuleResponse extends SearchResults<Asset> {

		private static List<Asset> assets = new ArrayList<Asset>();

		static {
			assets.add(ExampleData.ASSET_DEREK);
		}

		public SearchAssetModuleResponse() {
			super(assets);
			setNumResults(1);
		}
	}

	public static class GetAssetByIdPersonResponse extends ExampleData.Person_Derek {

		private static final long serialVersionUID = -2017792156204810161L;

		public GetAssetByIdPersonResponse() {
		}
	}

	public static class GetAssetByIdHardwareResponse extends ExampleData.Hardware_Caterpillar {

		private static final long serialVersionUID = 2878695642408035468L;

		public GetAssetByIdHardwareResponse() {
		}
	}

	public static class GetAssetByIdLocationResponse extends ExampleData.Location_Trailer {

		private static final long serialVersionUID = -7626071163612595490L;

		public GetAssetByIdLocationResponse() {
		}
	}

	public static class GetAssignmentsForAsset extends SearchResults<IDeviceAssignment> {

		private static List<IDeviceAssignment> assignments = new ArrayList<IDeviceAssignment>();

		static {
			assignments.add(ExampleData.TRACKER_TO_DEREK);
			assignments.add(ExampleData.HEART_MONITOR_TO_DEREK);
		}

		public GetAssignmentsForAsset() {
			super(assignments);
			setNumResults(2);
		}
	}

	public static class ListAssetModules {

		public Object generate() throws SiteWhereException {
			List<AssetModule> modules = new ArrayList<AssetModule>();
			modules.add(ExampleData.AM_PERSONS);
			modules.add(ExampleData.AM_DEVICES);
			return modules;
		}
	}

	public static class RefreshAssetModules {

		public Object generate() throws SiteWhereException {
			List<CommandResponse> responses = new ArrayList<CommandResponse>();
			responses.add(new CommandResponse(CommandResult.Successful, "Module loaded successfully."));
			responses.add(new CommandResponse(CommandResult.Failed, "Module failed to refresh."));
			return responses;
		}

	}

	public static class CreateAssetCategoryRequest {

		public Object generate() throws SiteWhereException {
			AssetCategoryCreateRequest request = new AssetCategoryCreateRequest();
			request.setId("my-devices");
			request.setName("My Devices");
			request.setAssetType(AssetType.Device);
			return request;
		}
	}

	public static class UpdateAssetCategoryRequest {

		public Object generate() throws SiteWhereException {
			AssetCategoryCreateRequest request = new AssetCategoryCreateRequest();
			request.setId("my-devices");
			request.setName("My Updated Devices");
			request.setAssetType(AssetType.Device);
			return request;
		}
	}

	public static class GetAssetCategoryByIdResponse {

		public Object generate() throws SiteWhereException {
			return ExampleData.AC_DEVICES;
		}
	}

	public static class ListAssetCategories {

		public Object generate() throws SiteWhereException {
			List<AssetCategory> categories = new ArrayList<AssetCategory>();
			categories.add(ExampleData.AC_DEVICES);
			categories.add(ExampleData.AC_PERSONS);
			return categories;
		}
	}

	public static class CreatePersonAssetInCategory {

		public Object generate() throws SiteWhereException {
			PersonAssetCreateRequest request = new PersonAssetCreateRequest();
			request.setId(ExampleData.ASSET_DEREK.getId());
			request.setName(ExampleData.ASSET_DEREK.getName());
			request.setUserName(ExampleData.ASSET_DEREK.getUserName());
			request.setEmailAddress(ExampleData.ASSET_DEREK.getEmailAddress());
			request.setImageUrl(ExampleData.ASSET_DEREK.getImageUrl());
			request.setRoles(ExampleData.ASSET_DEREK.getRoles());
			request.setProperties(ExampleData.ASSET_DEREK.getProperties());
			return request;
		}
	}

	public static class UpdatePersonAssetInCategory {

		public Object generate() throws SiteWhereException {
			PersonAssetCreateRequest request = new PersonAssetCreateRequest();
			request.setName(ExampleData.ASSET_DEREK.getName());
			request.setUserName(ExampleData.ASSET_DEREK.getUserName());
			request.setEmailAddress(ExampleData.ASSET_DEREK.getEmailAddress());
			request.setImageUrl(ExampleData.ASSET_DEREK.getImageUrl());
			request.setRoles(ExampleData.ASSET_DEREK.getRoles());
			request.setProperties(ExampleData.ASSET_DEREK.getProperties());
			return request;
		}
	}

	public static class CreateHardwareAssetInCategory {

		public Object generate() throws SiteWhereException {
			HardwareAssetCreateRequest request = new HardwareAssetCreateRequest();
			request.setId(ExampleData.ASSET_CATERPILLAR.getId());
			request.setName(ExampleData.ASSET_CATERPILLAR.getName());
			request.setSku(ExampleData.ASSET_CATERPILLAR.getSku());
			request.setDescription(ExampleData.ASSET_CATERPILLAR.getDescription());
			request.setImageUrl(ExampleData.ASSET_CATERPILLAR.getImageUrl());
			request.setProperties(ExampleData.ASSET_CATERPILLAR.getProperties());
			return request;
		}
	}

	public static class UpdateHardwareAssetInCategory {

		public Object generate() throws SiteWhereException {
			HardwareAssetCreateRequest request = new HardwareAssetCreateRequest();
			request.setName(ExampleData.ASSET_CATERPILLAR.getName());
			request.setSku(ExampleData.ASSET_CATERPILLAR.getSku());
			request.setDescription(ExampleData.ASSET_CATERPILLAR.getDescription());
			request.setImageUrl(ExampleData.ASSET_CATERPILLAR.getImageUrl());
			request.setProperties(ExampleData.ASSET_CATERPILLAR.getProperties());
			return request;
		}
	}

	public static class CreateLocationAssetInCategory {

		public Object generate() throws SiteWhereException {
			LocationAssetCreateRequest request = new LocationAssetCreateRequest();
			request.setId(ExampleData.ASSET_TRAILER.getId());
			request.setName(ExampleData.ASSET_TRAILER.getName());
			request.setLatitude(ExampleData.ASSET_TRAILER.getLatitude());
			request.setLongitude(ExampleData.ASSET_TRAILER.getLongitude());
			request.setElevation(ExampleData.ASSET_TRAILER.getElevation());
			request.setImageUrl(ExampleData.ASSET_TRAILER.getImageUrl());
			request.setProperties(ExampleData.ASSET_TRAILER.getProperties());
			return request;
		}
	}

	public static class UpdateLocationAssetInCategory {

		public Object generate() throws SiteWhereException {
			LocationAssetCreateRequest request = new LocationAssetCreateRequest();
			request.setName(ExampleData.ASSET_TRAILER.getName());
			request.setLatitude(ExampleData.ASSET_TRAILER.getLatitude());
			request.setLongitude(ExampleData.ASSET_TRAILER.getLongitude());
			request.setElevation(ExampleData.ASSET_TRAILER.getElevation());
			request.setImageUrl(ExampleData.ASSET_TRAILER.getImageUrl());
			request.setProperties(ExampleData.ASSET_TRAILER.getProperties());
			return request;
		}
	}

	public static class GetCategoryAssetPerson {

		public Object generate() throws SiteWhereException {
			return ExampleData.ASSET_DEREK;
		}
	}

	public static class GetCategoryAssetHardware {

		public Object generate() throws SiteWhereException {
			return ExampleData.ASSET_CATERPILLAR;
		}
	}

	public static class GetCategoryAssetLocation {

		public Object generate() throws SiteWhereException {
			return ExampleData.ASSET_TRAILER;
		}
	}

	public static class ListCategoryAssets {

		public Object generate() throws SiteWhereException {
			List<IAsset> assets = new ArrayList<IAsset>();
			assets.add(ExampleData.ASSET_DEREK);
			assets.add(ExampleData.ASSET_MARTIN);
			return new SearchResults<IAsset>(assets, 2);
		}
	}
}