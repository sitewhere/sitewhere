/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.util;

import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceElementMapping;

/**
 * Helper methods for common operations on {@link IDevice} objects.
 * 
 * @author Derek
 */
public class DeviceUtils {

    /**
     * Find {@link IDeviceElementMapping} for the given nested device hardware
     * id.
     * 
     * @param device
     * @param nestedHardwareId
     * @return
     */
    public static IDeviceElementMapping findMappingFor(IDevice device, String nestedHardwareId) {
	if (device.getDeviceElementMappings() != null) {
	    for (IDeviceElementMapping mapping : device.getDeviceElementMappings()) {
		if (mapping.getHardwareId().equals(nestedHardwareId)) {
		    return mapping;
		}
	    }
	}
	return null;
    }
}