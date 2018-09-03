/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.request.PersistentEntityCreateRequest;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.rest.model.device.element.DeviceSlot;
import com.sitewhere.rest.model.device.element.DeviceUnit;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.element.IDeviceElementSchema;
import com.sitewhere.spi.device.request.IDeviceTypeCreateRequest;

/**
 * Holds fields needed to create a new device specification.
 * 
 * @author Derek Adams
 */
@JsonInclude(Include.NON_NULL)
public class DeviceTypeCreateRequest extends PersistentEntityCreateRequest implements IDeviceTypeCreateRequest {

    /** Serialization version identifier */
    private static final long serialVersionUID = 1L;

    /** Name */
    private String name;

    /** Decription */
    private String description;

    /** Image URL */
    private String imageUrl;

    /** Indicates if device instances can contain nested devices */
    private DeviceContainerPolicy containerPolicy;

    /** Device element schema for specifications that support nested devices */
    private DeviceElementSchema deviceElementSchema;

    /*
     * @see com.sitewhere.spi.device.request.IDeviceTypeCreateRequest#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see
     * com.sitewhere.spi.device.request.IDeviceTypeCreateRequest#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see com.sitewhere.spi.device.request.IDeviceTypeCreateRequest#getImageUrl()
     */
    @Override
    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    /*
     * @see
     * com.sitewhere.spi.device.request.IDeviceTypeCreateRequest#getContainerPolicy(
     * )
     */
    @Override
    public DeviceContainerPolicy getContainerPolicy() {
	return containerPolicy;
    }

    public void setContainerPolicy(DeviceContainerPolicy containerPolicy) {
	this.containerPolicy = containerPolicy;
    }

    /*
     * @see com.sitewhere.spi.device.request.IDeviceTypeCreateRequest#
     * getDeviceElementSchema()
     */
    @Override
    public IDeviceElementSchema getDeviceElementSchema() {
	return deviceElementSchema;
    }

    public void setDeviceElementSchema(DeviceElementSchema deviceElementSchema) {
	this.deviceElementSchema = deviceElementSchema;
    }

    public static class Builder {

	/** Request being built */
	private DeviceTypeCreateRequest request = new DeviceTypeCreateRequest();

	public Builder(String token, String name) {
	    request.setToken(token);
	    request.setName(name);
	    request.setContainerPolicy(DeviceContainerPolicy.Standalone);
	}

	public Builder withDescription(String description) {
	    request.setDescription(description);
	    return this;
	}

	public Builder withImageUrl(String imageUrl) {
	    request.setImageUrl(imageUrl);
	    return this;
	}

	public Builder makeComposite() {
	    request.setContainerPolicy(DeviceContainerPolicy.Composite);
	    return this;
	}

	public DeviceElementSchemaBuilder newSchema() {
	    DeviceElementSchemaBuilder schema = new DeviceElementSchemaBuilder();
	    request.setDeviceElementSchema(schema.build());
	    return schema;
	}

	public Builder metadata(String name, String value) {
	    if (request.getMetadata() == null) {
		request.setMetadata(new HashMap<String, String>());
	    }
	    request.getMetadata().put(name, value);
	    return this;
	}

	public DeviceTypeCreateRequest build() {
	    return request;
	}
    }

    public static class DeviceElementSchemaBuilder {

	private DeviceElementSchema schema = new DeviceElementSchema();

	public DeviceUnitBuilder addUnit(String name, String path) {
	    DeviceUnitBuilder unit = new DeviceUnitBuilder(name, path);
	    schema.getDeviceUnits().add(unit.build());
	    return unit;
	}

	public DeviceElementSchemaBuilder addSlot(String name, String path) {
	    DeviceSlot slot = new DeviceSlot();
	    slot.setName(name);
	    slot.setPath(path);
	    schema.getDeviceSlots().add(slot);
	    return this;
	}

	public DeviceElementSchema build() {
	    return schema;
	}
    }

    public static class DeviceUnitBuilder {

	private DeviceUnit unit = new DeviceUnit();

	public DeviceUnitBuilder(String name, String path) {
	    unit.setName(name);
	    unit.setPath(path);
	}

	public DeviceUnitBuilder addUnit(String name, String path) {
	    DeviceUnitBuilder sub = new DeviceUnitBuilder(name, path);
	    unit.getDeviceUnits().add(sub.build());
	    return sub;
	}

	public DeviceUnitBuilder addSlot(String name, String path) {
	    DeviceSlot slot = new DeviceSlot();
	    slot.setName(name);
	    slot.setPath(path);
	    unit.getDeviceSlots().add(slot);
	    return this;
	}

	public DeviceUnit build() {
	    return unit;
	}
    }
}