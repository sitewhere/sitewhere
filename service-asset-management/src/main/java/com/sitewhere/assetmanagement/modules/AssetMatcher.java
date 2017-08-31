/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.assetmanagement.modules;

import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IHardwareAsset;
import com.sitewhere.spi.asset.ILocationAsset;
import com.sitewhere.spi.asset.IPersonAsset;

/**
 * Used for searches to find assets that match criteria.
 * 
 * @author Derek Adams
 */
public class AssetMatcher {

    /**
     * Delegate match criteria by type.
     * 
     * @param type
     * @param asset
     * @param criteria
     * @return
     */
    public boolean isMatch(AssetType type, IAsset asset, String criteria) {
	switch (type) {
	case Device: {
	    return isHardwareMatch((IHardwareAsset) asset, criteria);
	}
	case Hardware: {
	    return isHardwareMatch((IHardwareAsset) asset, criteria);
	}
	case Person: {
	    return isPersonMatch((IPersonAsset) asset, criteria);
	}
	case Location: {
	    return isLocationMatch((ILocationAsset) asset, criteria);
	}
	}
	return false;
    }

    /**
     * Indicates if hardware asset matches the given criteria.
     * 
     * @param asset
     * @param criteria
     * @return
     */
    public boolean isHardwareMatch(IHardwareAsset asset, String criteria) {
	if ((contains(asset.getName(), criteria)) || (contains(asset.getDescription(), criteria))
		|| (contains(asset.getId(), criteria))) {
	    return true;
	}
	return false;
    }

    /**
     * Indicates if hardware asset matches the given criteria.
     * 
     * @param asset
     * @param criteria
     * @return
     */
    public boolean isPersonMatch(IPersonAsset asset, String criteria) {
	if ((contains(asset.getName(), criteria)) || (contains(asset.getEmailAddress(), criteria))
		|| (contains(asset.getUserName(), criteria)) || (contains(asset.getId(), criteria))) {
	    return true;
	}
	return false;
    }

    /**
     * Indicates if location asset matches the given criteria.
     * 
     * @param asset
     * @param criteria
     * @return
     */
    public boolean isLocationMatch(ILocationAsset asset, String criteria) {
	if (contains(asset.getName(), criteria)) {
	    return true;
	}
	return false;
    }

    /**
     * Simplifies comparing possibly null non-case sensitive values.
     * 
     * @param field
     * @param value
     * @return
     */
    protected boolean contains(String field, String value) {
	if (field == null) {
	    return false;
	}
	return field.trim().toLowerCase().indexOf(value.toLowerCase()) != -1;
    }
}