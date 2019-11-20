/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.warp10db;

import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.warp10.Warp10Converter;
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
        GTSInput gtsInput = GTSInput.builder();
        gtsInput.setValue(source.getValue());
        gtsInput.setName(source.getName());
        gtsInput.setTs(source.getEventDate().getTime());
        Warp10DeviceEvent.toGTS(source, gtsInput);
        return gtsInput;
    }

    @Override
    public IDeviceMeasurement convert(GTSOutput source) {
        DeviceMeasurement deviceMeasurement = new DeviceMeasurement();
        Warp10DeviceEvent.fromGTS(source, deviceMeasurement);
        return deviceMeasurement;
    }
}
