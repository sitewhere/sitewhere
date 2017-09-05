/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.search;

import java.util.Date;

/**
 * Extends basic search criteria with ability to search by a date range.
 * 
 * @author Derek
 */
public interface IDateRangeSearchCriteria extends ISearchCriteria {

    /**
     * Get date range start.
     * 
     * @return
     */
    public Date getStartDate();

    /**
     * Get date range end.
     * 
     * @return
     */
    public Date getEndDate();
}