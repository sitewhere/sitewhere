/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.warp10db;

import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.warp10.Warp10Converter;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;

public class Warp10DeviceStateChange implements Warp10Converter<IDeviceStateChange> {
    @Override
    public GTSInput convert(IDeviceStateChange source) {
        return Warp10DeviceStateChange.toGTS(source);
    }

    @Override
    public IDeviceStateChange convert(GTSOutput source) {
        return Warp10DeviceStateChange.fromGTS(source);
    }

    public static GTSInput toGTS(IDeviceStateChange source) {
        GTSInput gtsInput = GTSInput.builder();
        return gtsInput;
    }

    public static DeviceStateChange fromGTS(GTSOutput source){
        DeviceStateChange deviceStateChange = new DeviceStateChange();
        return deviceStateChange;
    }
}
