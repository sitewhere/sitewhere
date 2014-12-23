/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.group;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.SiteWhere;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.search.ISearchCriteria;
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
	 * @param groupToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<IDevice> getDevicesInGroup(String groupToken, IDeviceSearchCriteria criteria)
			throws SiteWhereException {
		List<IDevice> devices = getDevicesInGroup(groupToken);
		List<IDevice> matches = new ArrayList<IDevice>();
		for (IDevice device : devices) {
			switch (criteria.getSearchType()) {
			case All: {
				break;
			}
			case UsesSpecification: {
				if (!device.getSpecificationToken().equals(
						criteria.getDeviceBySpecificationParameters().getSpecificationToken())) {
					continue;
				}
			}
			}
			if (criteria.isExcludeAssigned() && (device.getAssignmentToken() != null)) {
				continue;
			}
			if ((criteria.getStartDate() != null)
					&& (device.getCreatedDate().before(criteria.getStartDate()))) {
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
	 * Get the list of unique devices in a group. (Recurses into subgroups)
	 * 
	 * @param groupToken
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<IDevice> getDevicesInGroup(String groupToken) throws SiteWhereException {
		List<IDevice> devices = new ArrayList<IDevice>();
		ISearchCriteria criteria = new SearchCriteria(1, 0);
		SearchResults<IDeviceGroupElement> elements =
				SiteWhere.getServer().getDeviceManagement().listDeviceGroupElements(groupToken, criteria);
		for (IDeviceGroupElement element : elements.getResults()) {
			switch (element.getType()) {
			case Device: {
				devices.add(SiteWhere.getServer().getDeviceManagement().getDeviceByHardwareId(
						element.getElementId()));
				break;
			}
			case Group: {
				devices.addAll(getDevicesInGroup(element.getElementId()));
				break;
			}
			}
		}
		return devices;
	}
}