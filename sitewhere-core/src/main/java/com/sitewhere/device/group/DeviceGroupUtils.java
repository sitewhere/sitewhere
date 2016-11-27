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

import com.sitewhere.SiteWhere;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Utility methods for maniupulating device groups.
 * 
 * @author Derek
 */
public class DeviceGroupUtils {

    /**
     * Get devices in a group that match the given criteria.
     * 
     * @param groupToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static List<IDevice> getDevicesInGroup(String groupToken, IDeviceSearchCriteria criteria, ITenant tenant)
	    throws SiteWhereException {
	Collection<IDevice> devices = getDevicesInGroup(groupToken, tenant);
	List<IDevice> matches = new ArrayList<IDevice>();
	for (IDevice device : devices) {

	    // Handle filter by specification.
	    if (criteria.getSpecificationToken() != null) {
		if (!device.getSpecificationToken().equals(criteria.getSpecificationToken())) {
		    continue;
		}
	    }

	    // Handle filter by site.
	    if (criteria.getSiteToken() != null) {
		if (!device.getSiteToken().equals(criteria.getSiteToken())) {
		    continue;
		}
	    }

	    // Handle exclude assigned.
	    if (criteria.isExcludeAssigned() && (device.getAssignmentToken() != null)) {
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
     * @param groupToken
     * @return
     * @throws SiteWhereException
     */
    public static Collection<IDevice> getDevicesInGroup(String groupToken, ITenant tenant) throws SiteWhereException {
	Map<String, IDevice> devices = new HashMap<String, IDevice>();
	ISearchResults<IDeviceGroupElement> elements = SiteWhere.getServer().getDeviceManagement(tenant)
		.listDeviceGroupElements(groupToken, SearchCriteria.ALL);
	for (IDeviceGroupElement element : elements.getResults()) {
	    switch (element.getType()) {
	    case Device: {
		devices.put(element.getElementId(), SiteWhere.getServer().getDeviceManagement(tenant)
			.getDeviceByHardwareId(element.getElementId()));
		break;
	    }
	    case Group: {
		Collection<IDevice> subDevices = getDevicesInGroup(element.getElementId(), tenant);
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
     * Gets devices in all groups that have the given role. Duplicates are
     * removed.
     * 
     * @param groupRole
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static Collection<IDevice> getDevicesInGroupsWithRole(String groupRole, IDeviceSearchCriteria criteria,
	    ITenant tenant) throws SiteWhereException {
	Map<String, IDevice> devices = new HashMap<String, IDevice>();
	ISearchCriteria groupCriteria = new SearchCriteria(1, 0);
	ISearchResults<IDeviceGroup> groups = SiteWhere.getServer().getDeviceManagement(tenant)
		.listDeviceGroupsWithRole(groupRole, false, groupCriteria);
	for (IDeviceGroup group : groups.getResults()) {
	    List<IDevice> groupDevices = getDevicesInGroup(group.getToken(), criteria, tenant);
	    for (IDevice groupDevice : groupDevices) {
		devices.put(groupDevice.getHardwareId(), groupDevice);
	    }
	}
	return devices.values();
    }
}