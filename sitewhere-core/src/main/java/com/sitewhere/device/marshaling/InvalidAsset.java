/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.marshaling;

import com.sitewhere.rest.model.asset.HardwareAsset;

/**
 * Used to show broken link if referenced asset is deleted.
 * 
 * @author Derek
 */
public class InvalidAsset extends HardwareAsset {

    /** Serial version UID */
    private static final long serialVersionUID = 1383739852322979924L;

    public InvalidAsset() {
	super();
	setId("invalid");
	setAssetCategoryId("invalid");
	setName("Missing Asset");
	setDescription("Referenced asset was not found.");
	setImageUrl("https://s3.amazonaws.com/sitewhere-demo/broken-link.png");
	setSku("invalid");
    }
}
