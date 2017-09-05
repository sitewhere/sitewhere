/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.command;

/**
 * Enumerates reasons device registration can fail.
 * 
 * @author Derek
 */
public enum RegistrationFailureReason {

    /** Registration manager does not allow new devices */
    NewDevicesNotAllowed,

    /** Invalid specification token was passed */
    InvalidSpecificationToken,

    /** Site token was required */
    SiteTokenRequired;
}