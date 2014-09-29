/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.assetmodule.magento;

/**
 * Interface for fields available on a Magento object.
 * 
 * @author dadams
 */
public interface IMagentoFields {

	/** Field that specifies asset id */
	public static final String PROP_ASSET_ID = "product_id";

	/** Field that specifies SKU */
	public static final String PROP_SKU = "sku";

	/** Field that specifies name */
	public static final String PROP_NAME = "name";

	/** Field that specifies description */
	public static final String PROP_DESCRIPTION = "short_description";

	/** Field that specifies description */
	public static final String PROP_IMAGE_URL = "image_url";
}