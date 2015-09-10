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
import com.sitewhere.rest.model.asset.AssetModule;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;

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

		public SearchAssetModuleResponse() {
			super(assets);
			assets.add(ExampleData.ASSET_DEREK);
			setNumResults(1);
		}
	}

	public static class GetAssetByIdResponse extends ExampleData.Person_Derek {

		public GetAssetByIdResponse() {
		}
	}
}