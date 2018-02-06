/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.resource;

/**
 * Creation mode for
 * 
 * @author Derek
 *
 */
public enum ResourceCreateMode {

    /** Fail if resource path already used */
    FAIL_IF_EXISTS,

    /** Overwrite if resource path already used */
    OVERWRITE,

    /** Push new version if path already used */
    PUSH_NEW_VERSION;
}
