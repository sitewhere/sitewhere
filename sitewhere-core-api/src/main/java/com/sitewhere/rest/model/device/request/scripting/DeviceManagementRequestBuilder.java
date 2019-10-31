/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request.scripting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sitewhere.rest.model.area.request.AreaCreateRequest;
import com.sitewhere.rest.model.area.request.AreaTypeCreateRequest;
import com.sitewhere.rest.model.area.request.ZoneCreateRequest;
import com.sitewhere.rest.model.customer.request.CustomerCreateRequest;
import com.sitewhere.rest.model.customer.request.CustomerTypeCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceAlarmCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupElementCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceTypeCreateRequest;
import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.customer.ICustomerType;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAlarm;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Builder that supports creating device management entities.
 */
public class DeviceManagementRequestBuilder {

    /** Device management implementation */
    private IDeviceManagement deviceManagement;

    public DeviceManagementRequestBuilder(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    /**
     * Create a new customer type.
     * 
     * @param token
     * @param name
     * @return
     */
    public CustomerTypeCreateRequest.Builder newCustomerType(String token, String name) {
	return new CustomerTypeCreateRequest.Builder(token, name);
    }

    /**
     * Persist a previously created customer type.
     * 
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public ICustomerType persist(CustomerTypeCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createCustomerType(builder.build());
    }

    /**
     * Create a new customer.
     * 
     * @param customerTypeToken
     * @param parentCustomerToken
     * @param token
     * @param name
     * @return
     */
    public CustomerCreateRequest.Builder newCustomer(String customerTypeToken, String parentCustomerToken, String token,
	    String name) {
	return new CustomerCreateRequest.Builder(customerTypeToken, parentCustomerToken, token, name);
    }

    /**
     * Persist previously created customer.
     * 
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public ICustomer persist(CustomerCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createCustomer(builder.build());
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
    public ZoneCreateRequest.Builder newZone(String token, String name, IArea area) {
	return new ZoneCreateRequest.Builder(token, name, area);
    }

    /**
     * Get an existing zone by unique token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public IZone zoneByToken(String token) throws SiteWhereException {
	return getDeviceManagement().getZoneByToken(token);
    }

    /**
     * Persist a previously created zone.
     * 
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public IZone persist(ZoneCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createZone(builder.build());
    }

    /**
     * Create a new device type.
     * 
     * @param token
     * @param name
     * @return
     */
    public DeviceTypeCreateRequest.Builder newDeviceType(String token, String name) {
	return new DeviceTypeCreateRequest.Builder(token, name);
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
    public DeviceCommandCreateRequest.Builder newCommand(String deviceTypeToken, String token, String namespace,
	    String name) {
	return new DeviceCommandCreateRequest.Builder(deviceTypeToken, token, namespace, name);
    }

    /**
     * Persist a previously created command.
     * 
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public IDeviceCommand persist(DeviceCommandCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createDeviceCommand(builder.build());
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
     * @param deviceToken
     * @param customerToken
     * @param areaToken
     * @param assetToken
     * @return
     */
    public DeviceAssignmentCreateRequest.Builder newAssignment(String deviceToken, String customerToken,
	    String areaToken, String assetToken) {
	return new DeviceAssignmentCreateRequest.Builder(deviceToken, customerToken, areaToken, assetToken);
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
     * Returns all active device assignments.
     * 
     * @return
     * @throws SiteWhereException
     */
    public List<IDeviceAssignment> allActiveAssignments() throws SiteWhereException {
	DeviceAssignmentSearchCriteria criteria = new DeviceAssignmentSearchCriteria(1, 0);
	criteria.setAssignmentStatuses(Collections.singletonList(DeviceAssignmentStatus.Active));

	ISearchResults<IDeviceAssignment> matches = getDeviceManagement().listDeviceAssignments(criteria);
	return matches.getResults();
    }

    /**
     * Create a new device alarm.
     * 
     * @param deviceAssignmentToken
     * @param message
     * @return
     */
    public DeviceAlarmCreateRequest.Builder newDeviceAlarm(String deviceAssignmentToken, String message) {
	return new DeviceAlarmCreateRequest.Builder(deviceAssignmentToken, message);
    }

    /**
     * Persist a previously created device alarm.
     * 
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAlarm persist(DeviceAlarmCreateRequest.Builder builder) throws SiteWhereException {
	return getDeviceManagement().createDeviceAlarm(builder.build());
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
     * @param token
     * @return
     */
    public DeviceGroupElementCreateRequest.Builder newGroupElement(String token) {
	return new DeviceGroupElementCreateRequest.Builder(token);
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
}