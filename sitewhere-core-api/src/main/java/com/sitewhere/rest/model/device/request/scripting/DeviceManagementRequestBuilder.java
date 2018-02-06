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

import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupElementCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceSpecificationCreateRequest;
import com.sitewhere.rest.model.device.request.SiteCreateRequest;
import com.sitewhere.rest.model.device.request.ZoneCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
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

    public SiteCreateRequest.Builder newSite(String token, String name) {
	return new SiteCreateRequest.Builder(token, name);
    }

    public ISite persist(SiteCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createSite(builder.build());
    }

    public ZoneCreateRequest.Builder newZone(String name) {
	return new ZoneCreateRequest.Builder(name);
    }

    public IZone persist(ISite site, ZoneCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createZone(site.getId(), builder.build());
    }

    public DeviceSpecificationCreateRequest.Builder newSpecification(String token, String name, String assetModuleId,
	    String assetId) {
	return new DeviceSpecificationCreateRequest.Builder(token, name, assetModuleId, assetId);
    }

    public IDeviceSpecification persist(DeviceSpecificationCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createDeviceSpecification(builder.build());
    }

    public DeviceCommandCreateRequest.Builder newCommand(String token, String namespace, String name) {
	return new DeviceCommandCreateRequest.Builder(token, namespace, name);
    }

    public IDeviceCommand persist(IDeviceSpecification specification, DeviceCommandCreateRequest.Builder builder)
	    throws SiteWhereException {
	return getDeviceManagement().createDeviceCommand(specification.getId(), builder.build());
    }

    public DeviceCreateRequest.Builder newDevice(String siteToken, String specificationToken, String hardwareId) {
	return new DeviceCreateRequest.Builder(siteToken, specificationToken, hardwareId);
    }

    // public DeviceCreateRequest.Builder fromDevice(IDevice device) {
    // return new DeviceCreateRequest.Builder(device);
    // }

    public IDevice persist(DeviceCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createDevice(builder.build());
    }

    public IDevice update(IDevice device, DeviceCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().updateDevice(device.getId(), builder.build());
    }

    public DeviceAssignmentCreateRequest.Builder newAssignment(String hardwareId, String assetModuleId,
	    String assetId) {
	return new DeviceAssignmentCreateRequest.Builder(hardwareId, assetModuleId, assetId);
    }

    public IDeviceAssignment persist(DeviceAssignmentCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createDeviceAssignment(builder.build());
    }

    public DeviceGroupCreateRequest.Builder newGroup(String token, String name) {
	return new DeviceGroupCreateRequest.Builder(token, name);
    }

    public IDeviceGroup persist(DeviceGroupCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createDeviceGroup(builder.build());
    }

    public DeviceGroupElementCreateRequest.Builder newGroupElement(String id) {
	return new DeviceGroupElementCreateRequest.Builder(id);
    }

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