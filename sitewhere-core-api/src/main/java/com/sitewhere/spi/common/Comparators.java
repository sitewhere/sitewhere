/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.common;

import java.util.Comparator;

/**
 * Commonly used comparators for SiteWhere entities.
 * 
 * @author Derek
 */
public class Comparators {

    /**
     * Lists objects with newest created first.
     * 
     * @author Derek
     */
    public static class InverseCreatedDateComparator implements Comparator<ISiteWhereEntity> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(ISiteWhereEntity arg0, ISiteWhereEntity arg1) {
	    if ((arg0.getCreatedDate() == null) || (arg0.getCreatedDate() == null)) {
		return 0;
	    }
	    return -1 * (arg0.getCreatedDate().compareTo(arg1.getCreatedDate()));
	}
    }
}