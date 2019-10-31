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
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.device.asset.IDeviceEventWithAsset;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Wraps a device event and provides extra information the associated asset from
 * its assignment.
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

    public DeviceEventWithAsset(IDeviceEvent wrapped, IAssetManagement assetManagement) throws SiteWhereException {
	this.wrapped = wrapped;
	if (wrapped.getAssetId() != null) {
	    this.asset = assetManagement.getAsset(wrapped.getAssetId());
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
     * @see com.sitewhere.spi.common.IMetadataProvider#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return getWrapped().getMetadata();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getId()
     */
    @Override
    public UUID getId() {
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
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getDeviceId()
     */
    @Override
    public UUID getDeviceId() {
	return getWrapped().getDeviceId();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getDeviceAssignmentId()
     */
    @Override
    public UUID getDeviceAssignmentId() {
	return getWrapped().getDeviceAssignmentId();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getCustomerId()
     */
    @Override
    public UUID getCustomerId() {
	return getWrapped().getCustomerId();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getAreaId()
     */
    @Override
    public UUID getAreaId() {
	return getWrapped().getAreaId();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getAssetId()
     */
    @Override
    public UUID getAssetId() {
	return getWrapped().getDeviceId();
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