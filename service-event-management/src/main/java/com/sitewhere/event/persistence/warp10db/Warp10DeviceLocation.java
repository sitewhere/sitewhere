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
import com.sitewhere.warp10.common.Warp10MetadataProvider;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;

import java.util.Date;

public class Warp10DeviceLocation implements Warp10Converter<IDeviceLocation> {

    @Override
    public GTSInput convert(IDeviceLocation source) {
        return Warp10DeviceLocation.toGTS(source, false);
    }

    @Override
    public IDeviceLocation convert(GTSOutput source) {
        return Warp10DeviceLocation.fromGTS(source, false);
    }

    public static GTSInput toGTS(IDeviceLocation source, boolean isNested) {
        GTSInput gtsInput = GTSInput.builder();
       Warp10DeviceLocation.toGTS(source, gtsInput, isNested);
        return gtsInput;
    }

    public static void toGTS(IDeviceLocation source, GTSInput target, boolean isNested) {
        Warp10DeviceEvent.toGTS(source, target, isNested);
        target.setTs(source.getReceivedDate().getTime());
        target.setLat(source.getLatitude());
        target.setLon(source.getLongitude());
        target.setName(source.getDeviceAssignmentId().toString());

        if (source.getElevation() != null) {
            target.setElev(source.getElevation().longValue());
        }
        Warp10MetadataProvider.toGTS(source, target);
    }

    public static DeviceLocation fromGTS(GTSOutput source, boolean isNested) {
        DeviceLocation deviceLocation = new DeviceLocation();
        Warp10DeviceEvent.fromGTS(source, deviceLocation, isNested);
        deviceLocation.setElevation(source.getPoints().get(0).getElevation().doubleValue());
        deviceLocation.setLongitude(source.getPoints().get(0).getLongitude());
        deviceLocation.setLatitude(source.getPoints().get(0).getLatitude());
        return deviceLocation;
    }
}
