/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.asset;

import com.sitewhere.spi.search.ISearchCriteria;

/**
 * Criteria used to search for assets.
 * 
 * @author Derek
 */
public interface IAssetSearchCriteria extends ISearchCriteria {

    /**
     * Get the search query.
     * 
     * @return
     */
    public String getQuery();
}