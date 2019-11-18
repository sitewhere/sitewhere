/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceType;

/**
 * Utility class for common device management operations.
 */
public class DeviceManagementUtils {

    /**
     * Look up a list of device tokens to get the corresponding list of device ids.
     * 
     * @param tokens
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<UUID> getDeviceIds(List<String> tokens, IDeviceManagement deviceManagement)
	    throws SiteWhereException {
	List<UUID> result = new ArrayList<>();
	for (String token : tokens) {
	    IDevice device = deviceManagement.getDeviceByToken(token);
	    result.add(device.getId());
	}
	return result;
    }

    /**
     * Look up a list of device type tokens to get the corresponding list of device
     * type ids.
     * 
     * @param tokens
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<UUID> getDeviceTypeIds(List<String> tokens, IDeviceManagement deviceManagement)
	    throws SiteWhereException {
	List<UUID> result = new ArrayList<>();
	for (String token : tokens) {
	    IDeviceType type = deviceManagement.getDeviceTypeByToken(token);
	    result.add(type.getId());
	}
	return result;
    }

    /**
     * Look up a list of customer tokens to get the corresponding list of customer
     * ids.
     * 
     * @param tokens
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<UUID> getCustomerIds(List<String> tokens, IDeviceManagement deviceManagement)
	    throws SiteWhereException {
	List<UUID> result = new ArrayList<>();
	for (String token : tokens) {
	    ICustomer customer = deviceManagement.getCustomerByToken(token);
	    result.add(customer.getId());
	}
	return result;
    }

    /**
     * Look up a list of area tokens to get the corresponding list of area ids.
     * 
     * @param tokens
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<UUID> getAreaIds(List<String> tokens, IDeviceManagement deviceManagement)
	    throws SiteWhereException {
	List<UUID> result = new ArrayList<>();
	for (String token : tokens) {
	    IArea area = deviceManagement.getAreaByToken(token);
	    result.add(area.getId());
	}
	return result;
    }

    /**
     * Get string values for device assignment status names.
     * 
     * @param statuses
     * @return
     */
    public static List<String> getAssignmentStatusNames(List<DeviceAssignmentStatus> statuses) {
	List<String> result = new ArrayList<>();
	for (DeviceAssignmentStatus status : statuses) {
	    result.add(status.name());
	}
	return result;
    }
}
