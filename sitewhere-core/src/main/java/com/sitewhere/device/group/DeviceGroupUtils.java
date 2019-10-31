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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.device.marshaling.DeviceGroupElementMarshalHelper;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceGroupElement;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;

/**
 * Utility methods for maniupulating device groups.
 */
public class DeviceGroupUtils {

    /**
     * Get devices in a group that match the given criteria.
     * 
     * @param group
     * @param criteria
     * @param deviceManagement
     * @param assetManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<IDevice> getDevicesInGroup(IDeviceGroup group, IDeviceSearchCriteria criteria,
	    IDeviceManagement deviceManagement, IAssetManagement assetManagement) throws SiteWhereException {
	Collection<IDevice> devices = getDevicesInGroup(group.getId(), deviceManagement, assetManagement);
	List<IDevice> matches = new ArrayList<IDevice>();
	for (IDevice device : devices) {

	    // Handle filter by device type.
	    if (criteria.getDeviceTypeToken() != null) {
		IDeviceType deviceType = deviceManagement.getDeviceTypeByToken(criteria.getDeviceTypeToken());
		if (!device.getDeviceTypeId().equals(deviceType.getId())) {
		    continue;
		}
	    }

	    // Handle exclude assigned.
	    if (criteria.isExcludeAssigned() && (device.getActiveDeviceAssignmentIds().size() > 0)) {
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
     * Get list of all devices in a group. Recurse into nested groups and prevent
     * duplicates or loops in the group hierarchy.
     * 
     * @param groupId
     * @param deviceManagement
     * @param assetManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<IDevice> getDevicesInGroup(UUID groupId, IDeviceManagement deviceManagement,
	    IAssetManagement assetManagement) throws SiteWhereException {
	Map<String, IDevice> devices = new HashMap<>();
	Map<String, IDeviceGroup> groups = new HashMap<>();
	getDevicesInGroup(groupId, deviceManagement, assetManagement, devices, groups);
	List<IDevice> sorted = new ArrayList<>();
	sorted.addAll(devices.values());
	sorted.sort(new Comparator<IDevice>() {

	    @Override
	    public int compare(IDevice o1, IDevice o2) {
		return o1.getCreatedDate().compareTo(o2.getCreatedDate());
	    }
	});
	return sorted;
    }

    /**
     * Get the list of unique devices in a group. (Recurses into subgroups and
     * removes duplicates). Also prevents loops in group references.
     * 
     * @param groupId
     * @param deviceManagement
     * @param assetManagement
     * @param devices
     * @param groups
     * @throws SiteWhereException
     */
    protected static void getDevicesInGroup(UUID groupId, IDeviceManagement deviceManagement,
	    IAssetManagement assetManagement, Map<String, IDevice> devices, Map<String, IDeviceGroup> groups)
	    throws SiteWhereException {
	ISearchResults<IDeviceGroupElement> elements = deviceManagement.listDeviceGroupElements(groupId,
		SearchCriteria.ALL);
	DeviceGroupElementMarshalHelper helper = new DeviceGroupElementMarshalHelper(deviceManagement);
	for (IDeviceGroupElement element : elements.getResults()) {
	    MarshaledDeviceGroupElement inflated = helper.convert(element, assetManagement);
	    if (inflated.getDevice() != null) {
		IDevice device = inflated.getDevice();
		devices.put(device.getToken(), device);
	    } else if (inflated.getDeviceGroup() != null) {
		IDeviceGroup nested = inflated.getDeviceGroup();

		// Prevent loops.
		if (groups.get(nested.getToken()) != null) {
		}
	    }
	}
    }

    /**
     * Gets devices in all groups that have the given role. Duplicates are removed.
     * 
     * @param groupRole
     * @param criteria
     * @param deviceManagement
     * @param assetManagement
     * @return
     * @throws SiteWhereException
     */
    public static Collection<IDevice> getDevicesInGroupsWithRole(String groupRole, IDeviceSearchCriteria criteria,
	    IDeviceManagement deviceManagement, IAssetManagement assetManagement) throws SiteWhereException {
	Map<String, IDevice> devices = new HashMap<String, IDevice>();
	ISearchCriteria groupCriteria = new SearchCriteria(1, 0);
	ISearchResults<IDeviceGroup> groups = deviceManagement.listDeviceGroupsWithRole(groupRole, groupCriteria);
	for (IDeviceGroup group : groups.getResults()) {
	    List<IDevice> groupDevices = getDevicesInGroup(group, criteria, deviceManagement, assetManagement);
	    for (IDevice groupDevice : groupDevices) {
		devices.put(groupDevice.getToken(), groupDevice);
	    }
	}
	return devices.values();
    }
}