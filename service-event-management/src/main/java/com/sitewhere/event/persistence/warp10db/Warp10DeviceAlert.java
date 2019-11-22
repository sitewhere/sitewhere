/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.warp10db;

import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.warp10.Warp10Converter;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;

public class Warp10DeviceAlert implements Warp10Converter<IDeviceAlert> {
    @Override
    public GTSInput convert(IDeviceAlert source) {
        return Warp10DeviceAlert.toGTS(source);
    }

    @Override
    public IDeviceAlert convert(GTSOutput source) {
        return Warp10DeviceAlert.fromGTS(source);
    }

    public static GTSInput toGTS(IDeviceAlert source) {
        GTSInput gtsInput = GTSInput.builder();
        return gtsInput;
    }

    public static IDeviceAlert fromGTS(GTSOutput source){
        DeviceAlert deviceAlert = new DeviceAlert();
        return deviceAlert;
    }
}
