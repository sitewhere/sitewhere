/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

/**
 * Used as a clear indicator of the current status of a device assignment.
 * 
 * @author dadams
 */
public enum DeviceAssignmentStatus {

    /** Device is active */
    Active,

    /** Device reported missing */
    Missing,

    /** Device released */
    Released;
}