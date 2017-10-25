/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.marshaling;

import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.Site;

/**
 * Extends {@link Device} to support fields that can be included on REST calls.
 * 
 * @author Derek
 */
public class MarshaledDevice extends Device {

    /** Serial version UID */
    private static final long serialVersionUID = -7249138366647616811L;

    /** Device specification */
    private DeviceSpecification specification;

    /** Current device assignment */
    private DeviceAssignment assignment;

    /** Current site deployment */
    private Site site;

    /** Asset id from device specification (only for marshaling) */
    private String assetId;

    /** Asset name from device specification (only for marshaling) */
    private String assetName;

    /** Asset image url from device specification (only for marshaling) */
    private String assetImageUrl;

    public DeviceSpecification getSpecification() {
	return specification;
    }

    public void setSpecification(DeviceSpecification specification) {
	this.specification = specification;
    }

    public DeviceAssignment getAssignment() {
	return assignment;
    }

    public void setAssignment(DeviceAssignment assignment) {
	this.assignment = assignment;
    }

    public Site getSite() {
	return site;
    }

    public void setSite(Site site) {
	this.site = site;
    }

    public String getAssetId() {
	return assetId;
    }

    public void setAssetId(String assetId) {
	this.assetId = assetId;
    }

    public String getAssetName() {
	return assetName;
    }

    public void setAssetName(String assetName) {
	this.assetName = assetName;
    }

    public String getAssetImageUrl() {
	return assetImageUrl;
    }

    public void setAssetImageUrl(String assetImageUrl) {
	this.assetImageUrl = assetImageUrl;
    }
}