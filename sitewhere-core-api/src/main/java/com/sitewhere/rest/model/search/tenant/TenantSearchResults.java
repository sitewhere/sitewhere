/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search.tenant;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.tenant.Tenant;

/**
 * Search results that contain tenants. Needed so that JSON marshaling has a
 * concrete class to inflate.
 * 
 * @author dadams
 */
public class TenantSearchResults extends SearchResults<Tenant> {

    public TenantSearchResults() {
	super(new ArrayList<Tenant>());
    }

    public TenantSearchResults(List<Tenant> results) {
	super(results);
    }
}