/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.warp10;

import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.warp10.rest.QueryParams;

public class Warp10Persistence {

    public static void addDateSearchCriteria(QueryParams queryParams, IDateRangeSearchCriteria criteria) {

        if ((criteria.getStartDate() == null) && (criteria.getEndDate() == null)) {
            return;
        }
        if (criteria.getStartDate() != null) {
            queryParams.setStartDate(criteria.getStartDate());
        }
        if (criteria.getEndDate() != null) {
            queryParams.setEndDate(criteria.getEndDate());
        }
    }
}
