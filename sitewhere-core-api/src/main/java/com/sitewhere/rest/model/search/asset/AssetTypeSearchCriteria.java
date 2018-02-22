/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search.asset;

import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.search.area.IAssetTypeSearchCritiera;

/**
 * Model object for properties used in asset type searches.
 * 
 * @author Derek
 */
public class AssetTypeSearchCriteria extends SearchCriteria implements IAssetTypeSearchCritiera {

    public AssetTypeSearchCriteria(int pageNumber, int pageSize) {
	super(pageNumber, pageSize);
    }
}