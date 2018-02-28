/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.search.device;

import java.util.List;
import java.util.UUID;

import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * Criteria available for filtering device assignment search results.
 * 
 * @author Derek
 */
public interface IDeviceAssignmentSearchCriteria extends ISearchCriteria {

    /**
     * Only return assignments with the given status.
     * 
     * @return
     */
    public DeviceAssignmentStatus getStatus();

    /**
     * Limits search to a given device.
     * 
     * @return
     */
    public UUID getDeviceId();

    /**
     * Limits search the given list of areas.
     * 
     * @return
     */
    public List<UUID> getAreaIds();

    /**
     * Limits search to a given asset.
     * 
     * @return
     */
    public UUID getAssetId();
}