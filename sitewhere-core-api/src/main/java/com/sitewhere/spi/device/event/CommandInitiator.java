/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

/**
 * Enumerates actors that can initiate commands.
 * 
 * @author Derek
 */
public enum CommandInitiator {

    /** Command initiated by REST call */
    REST,

    /** Command initiated by batch operation */
    BatchOperation,

    /** Command initiated by script */
    Script,

    /** Command initiated by scheduler */
    Scheduler,
}