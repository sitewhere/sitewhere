package com.sitewhere.server.asset;

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
		return field.trim().toLowerCase().indexOf(value) != -1;
	}
}