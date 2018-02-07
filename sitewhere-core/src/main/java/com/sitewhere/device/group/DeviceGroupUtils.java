/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;

/**
 * Utility methods for maniupulating device groups.
 * 
 * @author Derek
 */
public class DeviceGroupUtils {

    /**
     * Get devices in a group that match the given criteria.
     * 
     * @param groupId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static List<IDevice> getDevicesInGroup(IDeviceGroup group, IDeviceSearchCriteria criteria,
	    IDeviceManagement deviceManagement) throws SiteWhereException {
	Collection<IDevice> devices = getDevicesInGroup(group.getId(), deviceManagement);
	List<IDevice> matches = new ArrayList<IDevice>();
	for (IDevice device : devices) {

	    // Handle filter by device type.
	    if (criteria.getDeviceTypeToken() != null) {
		IDeviceType deviceType = deviceManagement.getDeviceTypeByToken(criteria.getDeviceTypeToken());
		if (!device.getDeviceTypeId().equals(deviceType.getId())) {
		    continue;
		}
	    }

	    // Handle filter by site.
	    if (criteria.getSiteToken() != null) {
		ISite site = deviceManagement.getSiteByToken(criteria.getSiteToken());
		if (!device.getSiteId().equals(site.getId())) {
		    continue;
		}
	    }

	    // Handle exclude assigned.
	    if (criteria.isExcludeAssigned() && (device.getDeviceAssignmentId() != null)) {
		continue;
	    }
	    if ((criteria.getStartDate() != null) && (device.getCreatedDate().before(criteria.getStartDate()))) {
		continue;
	    }
	    if ((criteria.getEndDate() != null) && (device.getCreatedDate().after(criteria.getEndDate()))) {
		continue;
	    }
	    matches.add(device);
	}
	return matches;
    }

    /**
     * Get the list of unique devices in a group. (Recurses into subgroups and
     * removes duplicates)
     * 
     * @param groupId
     * @return
     * @throws SiteWhereException
     */
    public static Collection<IDevice> getDevicesInGroup(UUID groupId, IDeviceManagement deviceManagement)
	    throws SiteWhereException {
	Map<String, IDevice> devices = new HashMap<String, IDevice>();
	ISearchResults<IDeviceGroupElement> elements = deviceManagement.listDeviceGroupElements(groupId,
		SearchCriteria.ALL);
	for (IDeviceGroupElement element : elements.getResults()) {
	    switch (element.getType()) {
	    case Device: {
		IDevice device = deviceManagement.getDevice(element.getElementId());
		devices.put(device.getHardwareId(), device);
		break;
	    }
	    case Group: {
		Collection<IDevice> subDevices = getDevicesInGroup(element.getElementId(), deviceManagement);
		for (IDevice subDevice : subDevices) {
		    devices.put(subDevice.getHardwareId(), subDevice);
		}
		break;
	    }
	    }
	}
	return devices.values();
    }

    /**
     * Gets devices in all groups that have the given role. Duplicates are removed.
     * 
     * @param groupRole
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static Collection<IDevice> getDevicesInGroupsWithRole(String groupRole, IDeviceSearchCriteria criteria,
	    IDeviceManagement deviceManagement) throws SiteWhereException {
	Map<String, IDevice> devices = new HashMap<String, IDevice>();
	ISearchCriteria groupCriteria = new SearchCriteria(1, 0);
	ISearchResults<IDeviceGroup> groups = deviceManagement.listDeviceGroupsWithRole(groupRole, false,
		groupCriteria);
	for (IDeviceGroup group : groups.getResults()) {
	    List<IDevice> groupDevices = getDevicesInGroup(group, criteria, deviceManagement);
	    for (IDevice groupDevice : groupDevices) {
		devices.put(groupDevice.getHardwareId(), groupDevice);
	    }
	}
	return devices.values();
    }
}