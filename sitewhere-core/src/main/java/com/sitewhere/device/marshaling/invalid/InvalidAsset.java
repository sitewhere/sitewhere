/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.marshaling.invalid;

import com.sitewhere.rest.model.asset.Asset;

/**
 * Used to show broken link if referenced asset is deleted.
 */
public class InvalidAsset extends Asset {

    /** Serial version UID */
    private static final long serialVersionUID = 1383739852322979924L;

    public InvalidAsset() {
	setName("Missing Asset");
	setImageUrl("https://s3.amazonaws.com/sitewhere-demo/broken-link.png");
    }
}
