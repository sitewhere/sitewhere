/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.common;

import org.bson.Document;

import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.spi.common.IBrandedEntity;

/**
 * Used to load or save {@link BrandedEntity} data to MongoDB.
 * 
 * @author dadams
 */
public class MongoBrandedEntity {

    /** Property for image URL */
    public static final String PROP_IMAGE_URL = "imgu";

    /** Property for icon */
    public static final String PROP_ICON = "icon";

    /** Property for background color */
    public static final String PROP_BACKGROUND_COLOR = "bgco";

    /** Property for foreground color */
    public static final String PROP_FOREGROUND_COLOR = "fgco";

    /** Property for border color */
    public static final String PROP_BORDER_COLOR = "bdco";

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IBrandedEntity source, Document target) {
	target.append(PROP_IMAGE_URL, source.getImageUrl());
	target.append(PROP_ICON, source.getIcon());
	target.append(PROP_BACKGROUND_COLOR, source.getBackgroundColor());
	target.append(PROP_FOREGROUND_COLOR, source.getForegroundColor());
	target.append(PROP_BORDER_COLOR, source.getBorderColor());

	MongoPersistentEntity.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, BrandedEntity target) {
	String imageUrl = (String) source.get(PROP_IMAGE_URL);
	String icon = (String) source.get(PROP_ICON);
	String bgColor = (String) source.get(PROP_BACKGROUND_COLOR);
	String fgColor = (String) source.get(PROP_FOREGROUND_COLOR);
	String bdColor = (String) source.get(PROP_BORDER_COLOR);

	target.setImageUrl(imageUrl);
	target.setIcon(icon);
	target.setBackgroundColor(bgColor);
	target.setForegroundColor(fgColor);
	target.setBorderColor(bdColor);

	MongoPersistentEntity.fromDocument(source, target);
    }
}
