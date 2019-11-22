/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.warp10db;

import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.warp10.Warp10Converter;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;

public class Warp10DeviceCommandResponse implements Warp10Converter<IDeviceCommandResponse> {
    @Override
    public GTSInput convert(IDeviceCommandResponse source) {
        return Warp10DeviceCommandResponse.toGTS(source);
    }

    @Override
    public IDeviceCommandResponse convert(GTSOutput source) {
        return Warp10DeviceCommandResponse.fromGTS(source);
    }

    public static GTSInput toGTS(IDeviceCommandResponse source) {
        GTSInput gtsInput = GTSInput.builder();
        return gtsInput;
    }

    public static DeviceCommandResponse fromGTS(GTSOutput source){
        DeviceCommandResponse deviceCommandResponse = new DeviceCommandResponse();
        return deviceCommandResponse;
    }
}
