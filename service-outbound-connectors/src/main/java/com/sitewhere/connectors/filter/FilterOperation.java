/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.filter;

/**
 * Used to indicate if matches should be included or excluded from the result
 * set.
 */
public enum FilterOperation {

    /** Include matches */
    Include,

    /** Exclude matches */
    Exclude;
}