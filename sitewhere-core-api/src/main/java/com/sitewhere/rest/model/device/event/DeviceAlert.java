/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceAlert;

/**
 * Model object for an alert event from a remote device.
 * 
 * @author dadams
 */
@JsonInclude(Include.NON_NULL)
public class DeviceAlert extends DeviceEvent implements IDeviceAlert, Serializable {

    /** For Java serialization */
    private static final long serialVersionUID = 594540716893472520L;

    /** Alert source */
    private AlertSource source;

    /** Alert level */
    private AlertLevel level;

    /** Alert type */
    private String type;

    /** Alert message */
    private String message;

    public DeviceAlert() {
	super(DeviceEventType.Alert);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAlert#getSource()
     */
    public AlertSource getSource() {
	return source;
    }

    public void setSource(AlertSource source) {
	this.source = source;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAlert#getLevel()
     */
    public AlertLevel getLevel() {
	return level;
    }

    public void setLevel(AlertLevel level) {
	this.level = level;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAlert#getType()
     */
    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAlert#getMessage()
     */
    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    /**
     * Create a copy of an SPI object. Used by web services for marshaling.
     * 
     * @param input
     * @return
     */
    public static DeviceAlert copy(IDeviceAlert input) throws SiteWhereException {
	DeviceAlert result = new DeviceAlert();
	DeviceEvent.copy(input, result);
	result.setSource(input.getSource());
	result.setType(input.getType());
	result.setMessage(input.getMessage());
	result.setLevel(input.getLevel());
	return result;
    }
}