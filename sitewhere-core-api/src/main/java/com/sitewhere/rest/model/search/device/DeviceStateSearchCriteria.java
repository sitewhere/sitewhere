/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search.device;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.search.device.IDeviceStateSearchCriteria;

/**
 * Provides filter criteria for device state searches.
 * 
 * @author Derek
 */
public class DeviceStateSearchCriteria extends SearchCriteria implements IDeviceStateSearchCriteria {

    /** Filter by last interaction date before a given value */
    private Date lastInteractionDateBefore;

    /** Device type ids to be included */
    private List<UUID> deviceTypeIds;

    /** Customer ids to be included */
    private List<UUID> customerIds;

    /** Area ids to be included */
    private List<UUID> areaIds;

    /** Asset ids to be included */
    private List<UUID> assetIds;

    public DeviceStateSearchCriteria() {
	super();
    }

    public DeviceStateSearchCriteria(int pageNumber, int pageSize) {
	super(pageNumber, pageSize);
    }

    /*
     * @see
     * com.sitewhere.spi.search.device.IDeviceStateSearchCriteria#getDeviceTypeIds()
     */
    @Override
    public List<UUID> getDeviceTypeIds() {
	return deviceTypeIds;
    }

    public void setDeviceTypeIds(List<UUID> deviceTypeIds) {
	this.deviceTypeIds = deviceTypeIds;
    }

    /*
     * @see
     * com.sitewhere.spi.search.device.IDeviceStateSearchCriteria#getCustomerIds()
     */
    @Override
    public List<UUID> getCustomerIds() {
	return customerIds;
    }

    public void setCustomerIds(List<UUID> customerIds) {
	this.customerIds = customerIds;
    }

    /*
     * @see com.sitewhere.spi.search.device.IDeviceStateSearchCriteria#getAreaIds()
     */
    @Override
    public List<UUID> getAreaIds() {
	return areaIds;
    }

    public void setAreaIds(List<UUID> areaIds) {
	this.areaIds = areaIds;
    }

    /*
     * @see com.sitewhere.spi.search.device.IDeviceStateSearchCriteria#getAssetIds()
     */
    @Override
    public List<UUID> getAssetIds() {
	return assetIds;
    }

    public void setAssetIds(List<UUID> assetIds) {
	this.assetIds = assetIds;
    }

    /*
     * @see com.sitewhere.spi.search.device.IDeviceStateSearchCriteria#
     * getLastInteractionDateBefore()
     */
    @Override
    public Date getLastInteractionDateBefore() {
	return lastInteractionDateBefore;
    }

    public void setLastInteractionDateBefore(Date lastInteractionDateBefore) {
	this.lastInteractionDateBefore = lastInteractionDateBefore;
    }
}