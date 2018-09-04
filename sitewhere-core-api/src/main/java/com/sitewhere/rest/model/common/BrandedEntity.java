/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.common;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.IBrandedEntity;

/**
 * Base class for entities that include branding information.
 * 
 * @author Derek
 */
public abstract class BrandedEntity extends PersistentEntity implements IBrandedEntity {

    /** Serial version UID */
    private static final long serialVersionUID = 3921264232075671735L;

    /** Image URL */
    private String imageUrl;

    /** Icon */
    private String icon;

    /** Background color */
    private String backgroundColor;

    /** Foreground color */
    private String foregroundColor;

    /** Border color */
    private String borderColor;

    /*
     * @see com.sitewhere.spi.common.IImageProvider#getImageUrl()
     */
    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    /*
     * @see com.sitewhere.spi.common.IIconProvider#getIcon()
     */
    public String getIcon() {
	return icon;
    }

    public void setIcon(String icon) {
	this.icon = icon;
    }

    /*
     * @see com.sitewhere.spi.common.IColorProvider#getBackgroundColor()
     */
    public String getBackgroundColor() {
	return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
	this.backgroundColor = backgroundColor;
    }

    /*
     * @see com.sitewhere.spi.common.IColorProvider#getForegroundColor()
     */
    public String getForegroundColor() {
	return foregroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
	this.foregroundColor = foregroundColor;
    }

    /*
     * @see com.sitewhere.spi.common.IColorProvider#getBorderColor()
     */
    public String getBorderColor() {
	return borderColor;
    }

    public void setBorderColor(String borderColor) {
	this.borderColor = borderColor;
    }

    /**
     * Copy fields from source to target.
     * 
     * @param source
     * @param target
     */
    public static void copy(IBrandedEntity source, BrandedEntity target) throws SiteWhereException {
	target.setImageUrl(source.getImageUrl());
	target.setIcon(source.getIcon());
	target.setBackgroundColor(source.getBackgroundColor());
	target.setForegroundColor(source.getForegroundColor());
	target.setBorderColor(source.getBorderColor());
	PersistentEntity.copy(source, target);
    }
}
