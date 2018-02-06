/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.state;

/**
 * Enumerates possible system presence states.
 * 
 * @author Derek
 */
public enum PresenceState {

    /** Device was determined to be present */
    PRESENT,

    /** Device was determined not to be present */
    NOT_PRESENT;
}