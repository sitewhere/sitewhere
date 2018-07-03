/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceMeasurement;

/**
 * Implementation of device measurements.
 * 
 * @author dadams
 */
@JsonIgnoreProperties
@JsonInclude(Include.NON_NULL)
public class DeviceMeasurement extends DeviceEvent implements IDeviceMeasurement {

    /** Serial version UID */
    private static final long serialVersionUID = 8280584663755620411L;

    /** Measurement name */
    private String name;

    /** Measurement value */
    private Double value;

    public DeviceMeasurement() {
	super(DeviceEventType.Measurement);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceMeasurement#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceMeasurement#getValue()
     */
    @Override
    public Double getValue() {
	return value;
    }

    public void setValue(Double value) {
	this.value = value;
    }
}