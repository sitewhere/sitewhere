/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.warp10;

import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.warp10.Warp10Converter;
import com.sitewhere.warp10.common.Warp10MetadataProvider;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;

public class Warp10DeviceMeasurement implements Warp10Converter<IDeviceMeasurement> {

    /**
     * Attribute name for measurement name
     */
    public static final String PROP_NAME = "mxnm";

    /**
     * Attribute name for measurement value
     */
    public static final String PROP_VALUE = "mxvl";

    @Override
    public GTSInput convert(IDeviceMeasurement source) {
       return Warp10DeviceMeasurement.toGTS(source, false);
    }

    @Override
    public IDeviceMeasurement convert(GTSOutput source) {
        return Warp10DeviceMeasurement.fromGTS(source, false);
    }

    public static GTSInput toGTS(IDeviceMeasurement source, boolean isNested) {
        GTSInput gtsInput = GTSInput.builder();
        Warp10DeviceMeasurement.toGTS(source, gtsInput, isNested);
        return gtsInput;
    }

    public static void toGTS(IDeviceMeasurement source, GTSInput target, boolean isNested) {
        target.setValue(source.getValue());
        target.setName(source.getName());
        target.setTs(source.getEventDate().getTime());
        Warp10DeviceEvent.toGTS(source, target, isNested);
        Warp10MetadataProvider.toGTS(source, target);
    }

    public static DeviceMeasurement fromGTS(GTSOutput source,  boolean isNested){
        DeviceMeasurement deviceMeasurement = new DeviceMeasurement();
        deviceMeasurement.setName(source.getClassName());
        deviceMeasurement.setValue(Double.valueOf(source.getPoints().get(0).getValue()));
        Warp10DeviceEvent.fromGTS(source, deviceMeasurement, isNested);
        return deviceMeasurement;
    }
}
