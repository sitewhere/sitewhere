/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.rest.model.asset.Asset;

/**
 * Search results that contain assets. Used by JSON marshaling for generic
 * assets.
 * 
 * @author Derek
 */
public class AssetSearchResults extends SearchResults<Asset> {

    public AssetSearchResults() {
	super(new ArrayList<Asset>());
    }

    public AssetSearchResults(List<Asset> results) {
	super(results);
    }
}