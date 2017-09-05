/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.state;

/**
 * Enumerates possible system registration states.
 * 
 * @author Derek
 */
public enum RegistrationState {

    /** Device is not registered with the system */
    Unregistered,

    /** Device is registered with the system */
    Registered,
}