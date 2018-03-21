/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

import java.util.UUID;

/**
 * Represents a response from a device regarding a previously sent command.
 * 
 * @author Derek
 */
public interface IDeviceCommandResponse extends IDeviceEvent {

    /**
     * Get id of {@link IDeviceCommandInvocation} that triggered the response.
     * 
     * @return
     */
    public UUID getOriginatingEventId();

    /**
     * Get id of event sent as a response.
     * 
     * @return
     */
    public UUID getResponseEventId();

    /**
     * Get response payload.
     * 
     * @return
     */
    public String getResponse();
}