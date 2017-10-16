/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.command;

/**
 * Enumeration of results for device mapping operations.
 * 
 * @author Derek
 */
public enum DeviceMappingResult {

    /** Indicates a mapping was successfully created */
    MappingCreated,

    /** Indicates mapping not created since one already exists */
    MappingFailedDueToExisting;
}