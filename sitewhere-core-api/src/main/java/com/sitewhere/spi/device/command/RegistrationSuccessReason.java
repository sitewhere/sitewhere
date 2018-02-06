/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.command;

/**
 * Enumerates reasons registration was successful.
 * 
 * @author Derek
 */
public enum RegistrationSuccessReason {

    /** Indicates a new device registration */
    NewRegistration,

    /** Indicates device was already registered in the system */
    AlreadyRegistered;
}