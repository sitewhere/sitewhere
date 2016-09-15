/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about one or more caches.
 * 
 * @author Derek
 */
public class CacheSummary {

    /** List of cache information entries */
    private List<CacheInformation> caches = new ArrayList<CacheInformation>();

    public List<CacheInformation> getCaches() {
	return caches;
    }

    public void setCaches(List<CacheInformation> caches) {
	this.caches = caches;
    }
}