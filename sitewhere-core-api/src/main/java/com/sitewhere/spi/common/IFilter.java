/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.common;

/**
 * Determines whether a record should be filtered from a result set.
 * 
 * @author Derek
 */
public interface IFilter<T> {

    /**
     * Indicates whether the item is excluded.
     * 
     * @param item
     * @return
     */
    public boolean isExcluded(T item);
}