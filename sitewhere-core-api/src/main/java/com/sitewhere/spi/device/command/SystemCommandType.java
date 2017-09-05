/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.command;

/**
 * Enumerates types of system commands that may be sent to devices.
 * 
 * @author Derek
 */
public enum SystemCommandType {

    /** Acknowledges successful registration */
    RegistrationAck,

    /** Indicates failed registration */
    RegistrationFailure,

    /** Acknowledges device stream creation */
    DeviceStreamAck,

    /** Sends a chunk of device stream data */
    SendDeviceStreamData,

    /** Acknowledges response of device mapping operation */
    DeviceMappingAck;
}