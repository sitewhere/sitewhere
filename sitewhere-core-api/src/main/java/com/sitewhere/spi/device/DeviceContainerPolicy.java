/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

/**
 * Used to differentiate between device container policies.
 * 
 * @author Derek
 */
public enum DeviceContainerPolicy {

    /** Indicates a device that does not contain other devices */
    Standalone,

    /** Indicates a device that contains other devices */
    Composite;
}