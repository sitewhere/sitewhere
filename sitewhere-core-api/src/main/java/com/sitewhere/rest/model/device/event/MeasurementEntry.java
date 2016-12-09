/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event;

import java.io.Serializable;

import com.sitewhere.spi.device.event.IMeasurementEntry;

/**
 * A single measurement entry associated with a measurements event.
 * 
 * @author Derek
 */
public class MeasurementEntry implements IMeasurementEntry, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -427406980480474639L;

    /** Measurement name */
    private String name;

    /** Measurement value */
    private Double value;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.common.IMeasurementEntry#getName()
     */
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
    public Double getValue() {
	return value;
    }

    public void setValue(Double value) {
	this.value = value;
    }
}