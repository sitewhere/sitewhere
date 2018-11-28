/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.command;

import java.io.Serializable;
import java.util.UUID;

/**
 * Acknowledges device stream create request.
 * 
 * @author Derek
 */
public interface IDeviceStreamAckCommand extends ISystemCommand, Serializable {

    /**
     * Get unique id of stream being created.
     * 
     * @return
     */
    public UUID getStreamId();

    /**
     * Get status of stream creation.
     * 
     * @return
     */
    public DeviceStreamStatus getStatus();
}