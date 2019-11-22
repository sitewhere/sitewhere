/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.warp10db;

import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.warp10.Warp10Converter;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;

public class Warp10DeviceCommandInvocation implements Warp10Converter<IDeviceCommandInvocation> {
    @Override
    public GTSInput convert(IDeviceCommandInvocation source) {
        return Warp10DeviceCommandInvocation.toGTS(source);
    }

    @Override
    public IDeviceCommandInvocation convert(GTSOutput source) {
        return Warp10DeviceCommandInvocation.fromGTS(source);
    }

    public static GTSInput toGTS(IDeviceCommandInvocation source) {
        GTSInput gtsInput = GTSInput.builder();
        return gtsInput;
    }

    public static DeviceCommandInvocation fromGTS(GTSOutput source) {
        DeviceCommandInvocation deviceCommandInvocation = new DeviceCommandInvocation();
        return deviceCommandInvocation;
    }
}
