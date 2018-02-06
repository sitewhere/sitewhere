/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

/**
 * Status indicator for a command.
 * 
 * @author Derek
 */
public enum CommandStatus {

    /** Command is pending delivery */
    Pending,

    /** Command has started processing */
    Processing,

    /** Command has been sent on the underlying transport */
    Sent,

    /** Response message has been received */
    Responded;
}