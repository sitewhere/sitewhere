/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.element.IDeviceElementSchema;

/**
 * Model object for device specification information.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceType extends BrandedEntity implements IDeviceType {

    /** Serialization version identifier */
    private static final long serialVersionUID = -2798346634280966544L;

    /** Name */
    private String name;

    /** Decription */
    private String description;

    /** Device container policy */
    private DeviceContainerPolicy containerPolicy = DeviceContainerPolicy.Standalone;

    /** Schema that specifies allowable locations of nested devices */
    private DeviceElementSchema deviceElementSchema;

    /*
     * @see com.sitewhere.spi.device.IDeviceType#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceType#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceType#getContainerPolicy()
     */
    @Override
    public DeviceContainerPolicy getContainerPolicy() {
	return containerPolicy;
    }

    public void setContainerPolicy(DeviceContainerPolicy containerPolicy) {
	this.containerPolicy = containerPolicy;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceType#getDeviceElementSchema()
     */
    @Override
    public IDeviceElementSchema getDeviceElementSchema() {
	return deviceElementSchema;
    }

    public void setDeviceElementSchema(DeviceElementSchema deviceElementSchema) {
	this.deviceElementSchema = deviceElementSchema;
    }
}