/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.marshaling.invalid;

import com.sitewhere.rest.model.area.Area;

/**
 * Used to show broken link if referenced area is deleted.
 */
public class InvalidArea extends Area {

    /** Serial version UID */
    private static final long serialVersionUID = 5854317179012991951L;

    public InvalidArea() {
	setName("Missing Area");
	setImageUrl("https://s3.amazonaws.com/sitewhere-demo/broken-link.png");
    }
}
