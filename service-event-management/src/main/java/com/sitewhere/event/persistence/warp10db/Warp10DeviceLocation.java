/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.warp10db;

import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.warp10.Warp10Converter;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;

public class Warp10DeviceLocation implements Warp10Converter<IDeviceLocation> {
    @Override
    public GTSInput convert(IDeviceLocation source) {
        return Warp10DeviceLocation.toGTS(source);
    }

    @Override
    public IDeviceLocation convert(GTSOutput source) {
        return Warp10DeviceLocation.fromGTS(source);
    }

    public static GTSInput toGTS(IDeviceLocation source) {
        GTSInput gtsInput = GTSInput.builder();
        return gtsInput;
    }

    public static DeviceLocation fromGTS(GTSOutput source){
        DeviceLocation deviceLocation = new DeviceLocation();
        Warp10DeviceEvent.fromGTS(source, deviceLocation);
        return deviceLocation;
    }
}
