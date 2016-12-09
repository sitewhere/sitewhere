/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event;

import java.io.Serializable;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceMeasurement;

/**
 * Model object for a single measurement.
 * 
 * @author Derek
 */
public class DeviceMeasurement extends DeviceEvent implements IDeviceMeasurement, Serializable {

    /** For Java serialization */
    private static final long serialVersionUID = 5255345217091668945L;

    /** Measurement name */
    private String name;

    /** Measurement value */
    private Double value;

    public DeviceMeasurement() {
	super(DeviceEventType.Measurement);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.common.IMeasurementEntry#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.common.IMeasurementEntry#getValue()
     */
    @Override
    public Double getValue() {
	return value;
    }

    public void setValue(Double value) {
	this.value = value;
    }

    /**
     * Create a copy of an SPI object. Used by web services for marshaling.
     * 
     * @param input
     * @return
     */
    public static DeviceMeasurement copy(IDeviceMeasurement input) throws SiteWhereException {
	DeviceMeasurement result = new DeviceMeasurement();
	DeviceEvent.copy(input, result);
	result.setName(input.getName());
	result.setValue(input.getValue());
	return result;
    }
}