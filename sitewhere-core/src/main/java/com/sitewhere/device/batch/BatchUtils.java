/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.batch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sitewhere.SiteWhere;
import com.sitewhere.device.group.DeviceGroupUtils;
import com.sitewhere.rest.model.search.device.DeviceSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.request.IBatchCommandForCriteriaRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;

/**
 * Utility methods for batch operations.
 * 
 * @author Derek
 */
public class BatchUtils {

	/**
	 * Get hardware ids based on the given criteria.
	 * 
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<String> getHardwareIds(IBatchCommandForCriteriaRequest criteria)
			throws SiteWhereException {
		if (criteria.getSpecificationToken() == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceSpecificationToken, ErrorLevel.ERROR);
		}

		boolean hasGroup = false;
		boolean hasGroupsWithRole = false;
		if ((criteria.getGroupToken() != null) && (criteria.getGroupToken().trim().length() > 0)) {
			hasGroup = true;
		}
		if ((criteria.getGroupsWithRole() != null) && (criteria.getGroupsWithRole().trim().length() > 0)) {
			hasGroupsWithRole = true;
		}
		if (hasGroup && hasGroupsWithRole) {
			throw new SiteWhereException("Only one of groupToken or groupsWithRole may be specified.");
		}

		IDeviceSearchCriteria deviceSearch =
				DeviceSearchCriteria.createDeviceBySpecificationSearch(criteria.getSpecificationToken(), 1,
						0, criteria.getStartDate(), criteria.getEndDate(), criteria.isExcludeAssigned());

		Collection<IDevice> matches;
		if (hasGroup) {
			matches = DeviceGroupUtils.getDevicesInGroup(criteria.getGroupToken(), deviceSearch);
		} else if (hasGroupsWithRole) {
			matches = DeviceGroupUtils.getDevicesInGroupsWithRole(criteria.getGroupsWithRole(), deviceSearch);
		} else {
			matches =
					SiteWhere.getServer().getDeviceManagement().listDevices(false, deviceSearch).getResults();
		}
		List<String> hardwareIds = new ArrayList<String>();
		for (IDevice match : matches) {
			hardwareIds.add(match.getHardwareId());
		}
		return hardwareIds;
	}
}