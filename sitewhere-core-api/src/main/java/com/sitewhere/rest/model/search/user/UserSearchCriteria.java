/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search.user;

import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.user.IUserSearchCriteria;

/**
 * Implementation of IUserSearchCriteria.
 * 
 * @author Derek Adams
 */
public class UserSearchCriteria extends SearchCriteria implements IUserSearchCriteria {

    /** Serial version UID */
    private static final long serialVersionUID = 1269929281474978628L;

    public UserSearchCriteria() {
	super();
    }

    public UserSearchCriteria(int pageNumber, int pageSize) {
	super(pageNumber, pageSize);
    }
}