/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.resource;

/**
 * Enumeration of reasons for resource creation failures.
 * 
 * @author Derek
 */
public enum ResourceCreateFailReason {

    /** Resource already exists and overwrite not enabled */
    ResourceExists,

    /** Unable to store resource on underlying medium */
    StorageFailure;
}