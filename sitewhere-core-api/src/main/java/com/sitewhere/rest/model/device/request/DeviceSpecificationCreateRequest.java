/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.rest.model.device.element.DeviceSlot;
import com.sitewhere.rest.model.device.element.DeviceUnit;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.element.IDeviceElementSchema;
import com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest;

/**
 * Holds fields needed to create a new device specification.
 * 
 * @author Derek Adams
 */
@JsonInclude(Include.NON_NULL)
public class DeviceSpecificationCreateRequest implements IDeviceSpecificationCreateRequest, Serializable {

	/** Serialization version identifier */
	private static final long serialVersionUID = 1L;

	/** Specification name */
	private String name;

	/** Asset module id */
	private String assetModuleId;

	/** Asset id */
	private String assetId;

	/** Specification id (Optional) */
	private String token;

	/** Indicates if device instances can contain nested devices */
	private DeviceContainerPolicy containerPolicy;

	/** Device element schema for specifications that support nested devices */
	private DeviceElementSchema deviceElementSchema;

	/** Metadata values */
	private Map<String, String> metadata;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest#getName()
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
	 * @see
	 * com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest#getAssetModuleId
	 * ()
	 */
	public String getAssetModuleId() {
		return assetModuleId;
	}

	public void setAssetModuleId(String assetModuleId) {
		this.assetModuleId = assetModuleId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest#getAssetId()
	 */
	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest#getToken()
	 */
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest#
	 * getContainerPolicy ()
	 */
	public DeviceContainerPolicy getContainerPolicy() {
		return containerPolicy;
	}

	public void setContainerPolicy(DeviceContainerPolicy containerPolicy) {
		this.containerPolicy = containerPolicy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest#
	 * getDeviceElementSchema()
	 */
	public IDeviceElementSchema getDeviceElementSchema() {
		return deviceElementSchema;
	}

	public void setDeviceElementSchema(DeviceElementSchema deviceElementSchema) {
		this.deviceElementSchema = deviceElementSchema;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest#getMetadata()
	 */
	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public static class Builder {

		/** Request being built */
		private DeviceSpecificationCreateRequest request = new DeviceSpecificationCreateRequest();

		public Builder(String token, String name, String assetModuleId, String assetId) {
			request.setToken(token);
			request.setName(name);
			request.setAssetModuleId(assetModuleId);
			request.setAssetId(assetId);
			request.setContainerPolicy(DeviceContainerPolicy.Standalone);
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

		public DeviceSpecificationCreateRequest build() {
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