/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.marshaling.invalid;

import com.sitewhere.rest.model.asset.AssetType;

/**
 * Used to show broken link if referenced asset type is deleted.
 */
public class InvalidAssetType extends AssetType {

    /** Serial version UID */
    private static final long serialVersionUID = -376943314147215826L;

    public InvalidAssetType() {
	setName("Missing Asset Type");
	setImageUrl("https://s3.amazonaws.com/sitewhere-demo/broken-link.png");
    }
}