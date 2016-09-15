/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search.user;

import com.sitewhere.spi.user.IUserSearchCriteria;

/**
 * Implementation of IUserSearchCriteria.
 * 
 * @author Derek Adams
 */
public class UserSearchCriteria implements IUserSearchCriteria {

    /** Flag for whether deleted devices are included */
    private boolean includeDeleted = false;

    public boolean isIncludeDeleted() {
	return includeDeleted;
    }

    public void setIncludeDeleted(boolean includeDeleted) {
	this.includeDeleted = includeDeleted;
    }
}