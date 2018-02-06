/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.asset;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.rest.model.datatype.JsonDateSerializer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetReference;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.asset.IDeviceEventWithAsset;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Wraps a device event and provides extra information the associated asset from
 * its assignment.
 * 
 * @author Derek
 */
public class DeviceEventWithAsset implements IDeviceEventWithAsset {

    /** Serial version UID */
    private static final long serialVersionUID = 4865401913475898245L;

    /** Text shown when an asset is not assigned */
    public static final String UNASSOCIATED_ASSET_NAME = "Unassociated";

    /** Wrapped event */
    protected IDeviceEvent wrapped;

    /** Associated asset */
    protected IAsset asset;

    public DeviceEventWithAsset(IDeviceEvent wrapped, IAssetResolver assetResolver) throws SiteWhereException {
	this.wrapped = wrapped;
	if (wrapped.getAssignmentType() == DeviceAssignmentType.Associated) {
	    this.asset = assetResolver.getAssetModuleManagement().getAsset(wrapped.getAssetReference());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.asset.IDeviceEventWithAsset#getAssetName()
     */
    @Override
    public String getAssetName() {
	if (asset != null) {
	    return asset.getName();
	}
	return UNASSOCIATED_ASSET_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.common.IMetadataProvider#addOrReplaceMetadata(java.lang
     * .String, java.lang.String)
     */
    @Override
    public void addOrReplaceMetadata(String name, String value) throws SiteWhereException {
	getWrapped().addOrReplaceMetadata(name, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.common.IMetadataProvider#removeMetadata(java.lang.
     * String)
     */
    @Override
    public String removeMetadata(String name) {
	return getWrapped().removeMetadata(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.common.IMetadataProvider#getMetadata(java.lang.String)
     */
    @Override
    public String getMetadata(String name) {
	return getWrapped().getMetadata(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.common.IMetadataProvider#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return getWrapped().getMetadata();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.common.IMetadataProvider#clearMetadata()
     */
    @Override
    public void clearMetadata() {
	getWrapped().clearMetadata();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(IDeviceEvent o) {
	return getWrapped().compareTo(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceEvent#getId()
     */
    @Override
    public String getId() {
	return getWrapped().getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getAlternateId()
     */
    @Override
    public String getAlternateId() {
	return getWrapped().getAlternateId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getEventType()
     */
    @Override
    public DeviceEventType getEventType() {
	return getWrapped().getEventType();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getSiteId()
     */
    @Override
    public UUID getSiteId() {
	return getWrapped().getSiteId();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getDeviceAssignmentId()
     */
    @Override
    public UUID getDeviceAssignmentId() {
	return getWrapped().getDeviceAssignmentId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceEvent#getAssignmentType()
     */
    @Override
    public DeviceAssignmentType getAssignmentType() {
	return getWrapped().getAssignmentType();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getAssetReference()
     */
    @Override
    public IAssetReference getAssetReference() {
	return getWrapped().getAssetReference();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceEvent#getEventDate()
     */
    @Override
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getEventDate() {
	return getWrapped().getEventDate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceEvent#getReceivedDate()
     */
    @Override
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getReceivedDate() {
	return getWrapped().getReceivedDate();
    }

    protected IDeviceEvent getWrapped() {
	return wrapped;
    }
}