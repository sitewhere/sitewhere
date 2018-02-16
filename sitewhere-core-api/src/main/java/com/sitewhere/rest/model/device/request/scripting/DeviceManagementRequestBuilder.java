/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request.scripting;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.rest.model.area.request.AreaCreateRequest;
import com.sitewhere.rest.model.area.request.AreaTypeCreateRequest;
import com.sitewhere.rest.model.area.request.ZoneCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupElementCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceTypeCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;

/**
 * Builder that supports creating device management entities.
 * 
 * @author Derek
 */
public class DeviceManagementRequestBuilder {

    /** Device management implementation */
    private IDeviceManagement deviceManagement;

    public DeviceManagementRequestBuilder(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    /**
     * Create a new area type.
     * 
     * @param token
     * @param name
     * @return
     */
    public AreaTypeCreateRequest.Builder newAreaType(String token, String name) {
	return new AreaTypeCreateRequest.Builder(token, name);
    }

    /**
     * Persist a previously created area type.
     * 
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public IAreaType persist(AreaTypeCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createAreaType(builder.build());
    }

    /**
     * Create a new area.
     * 
     * @param areaTypeToken
     * @param parentAreaToken
     * @param token
     * @param name
     * @return
     */
    public AreaCreateRequest.Builder newArea(String areaTypeToken, String parentAreaToken, String token, String name) {
	return new AreaCreateRequest.Builder(areaTypeToken, parentAreaToken, token, name);
    }

    /**
     * Persist previously created area.
     * 
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public IArea persist(AreaCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createArea(builder.build());
    }

    /**
     * Create a new zone.
     * 
     * @param name
     * @return
     */
    public ZoneCreateRequest.Builder newZone(String name) {
	return new ZoneCreateRequest.Builder(name);
    }

    /**
     * Persist a previously created zone.
     * 
     * @param area
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public IZone persist(IArea area, ZoneCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createZone(area.getId(), builder.build());
    }

    /**
     * Create a new device type.
     * 
     * @param token
     * @param name
     * @param assetModuleId
     * @param assetId
     * @return
     */
    public DeviceTypeCreateRequest.Builder newDeviceType(String token, String name, String assetModuleId,
	    String assetId) {
	return new DeviceTypeCreateRequest.Builder(token, name, assetModuleId, assetId);
    }

    /**
     * Persist a previously created device type.
     * 
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public IDeviceType persist(DeviceTypeCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createDeviceType(builder.build());
    }

    /**
     * Create a new device command.
     * 
     * @param token
     * @param namespace
     * @param name
     * @return
     */
    public DeviceCommandCreateRequest.Builder newCommand(String token, String namespace, String name) {
	return new DeviceCommandCreateRequest.Builder(token, namespace, name);
    }

    /**
     * Persist a previously created command.
     * 
     * @param type
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public IDeviceCommand persist(IDeviceType type, DeviceCommandCreateRequest.Builder builder)
	    throws SiteWhereException {
	return getDeviceManagement().createDeviceCommand(type.getId(), builder.build());
    }

    /**
     * Create a new device.
     * 
     * @param deviceTypeToken
     * @param hardwareId
     * @return
     */
    public DeviceCreateRequest.Builder newDevice(String deviceTypeToken, String hardwareId) {
	return new DeviceCreateRequest.Builder(deviceTypeToken, hardwareId);
    }

    // public DeviceCreateRequest.Builder fromDevice(IDevice device) {
    // return new DeviceCreateRequest.Builder(device);
    // }

    /**
     * Persist a previously created device.
     * 
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public IDevice persist(DeviceCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createDevice(builder.build());
    }

    /**
     * Update an existing device.
     * 
     * @param device
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public IDevice update(IDevice device, DeviceCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().updateDevice(device.getId(), builder.build());
    }

    /**
     * Create a new device assignment.
     * 
     * @param hardwareId
     * @param areaToken
     * @param assetModuleId
     * @param assetId
     * @return
     */
    public DeviceAssignmentCreateRequest.Builder newAssignment(String hardwareId, String areaToken,
	    String assetModuleId, String assetId) {
	return new DeviceAssignmentCreateRequest.Builder(hardwareId, areaToken, assetModuleId, assetId);
    }

    /**
     * Persist a previously created device assignment.
     * 
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAssignment persist(DeviceAssignmentCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createDeviceAssignment(builder.build());
    }

    /**
     * Create a new device group.
     * 
     * @param token
     * @param name
     * @return
     */
    public DeviceGroupCreateRequest.Builder newGroup(String token, String name) {
	return new DeviceGroupCreateRequest.Builder(token, name);
    }

    /**
     * Persist a previously created device group.
     * 
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public IDeviceGroup persist(DeviceGroupCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createDeviceGroup(builder.build());
    }

    /**
     * Create a new device group element.
     * 
     * @param id
     * @return
     */
    public DeviceGroupElementCreateRequest.Builder newGroupElement(String id) {
	return new DeviceGroupElementCreateRequest.Builder(id);
    }

    /**
     * Persist previously created device group elements.
     * 
     * @param group
     * @param builders
     * @return
     * @throws SiteWhereException
     */
    public List<IDeviceGroupElement> persist(IDeviceGroup group, List<DeviceGroupElementCreateRequest.Builder> builders)
	    throws SiteWhereException {
	List<IDeviceGroupElementCreateRequest> elements = new ArrayList<IDeviceGroupElementCreateRequest>();
	for (DeviceGroupElementCreateRequest.Builder builder : builders) {
	    elements.add(builder.build());
	}
	return getDeviceManagement().addDeviceGroupElements(group.getId(), elements, true);
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }
}