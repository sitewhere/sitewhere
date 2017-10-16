/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.search.device;

import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * Search criteria for locating assignments for a given asset type.
 * 
 * @author Derek
 */
public interface IAssignmentsForAssetSearchCriteria extends ISearchCriteria {

    /**
     * Limits search to a given site.
     * 
     * @return
     */
    public String getSiteToken();

    /**
     * Only return assignments with the given status.
     * 
     * @return
     */
    public DeviceAssignmentStatus getStatus();
}