/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
