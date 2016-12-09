/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.search.device;

import com.sitewhere.spi.search.IDateRangeSearchCriteria;

/**
 * Search criteria particular to device searches.
 * 
 * @author Derek
 */
public interface IDeviceSearchCriteria extends IDateRangeSearchCriteria {

    /**
     * Get token if filtered by specification.
     * 
     * @return
     */
    public String getSpecificationToken();

    /**
     * Get token if filtered by site.
     * 
     * @return
     */
    public String getSiteToken();

    /**
     * Indicates whether assigned devices should be excluded.
     * 
     * @return
     */
    public boolean isExcludeAssigned();
}