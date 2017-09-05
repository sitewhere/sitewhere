/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event;

import java.io.Serializable;

import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventOriginator;

/**
 * Default implementation of {@link IDeviceEventOriginator}. This object is used
 * to provide a strongly-typed argument in interactions with device agents that
 * use Java introspection to find the right method to invoke.
 * 
 * @author Derek
 */
public class DeviceEventOriginator implements IDeviceEventOriginator, Serializable {

    /** Used for Java serialization */
    private static final long serialVersionUID = -5674524224174870647L;

    /** Originating event id */
    private String eventId;

    public DeviceEventOriginator(IDeviceEvent event) {
	this.eventId = event.getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventOriginator#getEventId()
     */
    public String getEventId() {
	return eventId;
    }

    public void setEventId(String eventId) {
	this.eventId = eventId;
    }
}