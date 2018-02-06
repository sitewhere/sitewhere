/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.element;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.device.element.IDeviceElement;

/**
 * Default implementation of {@link IDeviceElement}.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceElement implements IDeviceElement, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = 8334544031222730874L;

    /** Element name */
    private String name;

    /** Path relative to parent */
    private String path;

    public DeviceElement() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.element.IDeviceElement#getName()
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
     * @see com.sitewhere.spi.device.element.IDeviceElement#getPath()
     */
    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }
}