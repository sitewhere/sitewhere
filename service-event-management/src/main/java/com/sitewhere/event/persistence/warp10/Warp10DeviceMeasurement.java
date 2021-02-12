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
package com.sitewhere.event.persistence.warp10;

import java.math.BigDecimal;
import java.util.HashMap;

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
	target.setValue(source.getValue().doubleValue());
	target.setName(source.getName());
	target.setTs(source.getEventDate().getTime());
	target.setAttributes(new HashMap<>());
	Warp10DeviceEvent.toGTS(source, target, isNested);
	Warp10MetadataProvider.toGTS(source, target);
    }

    public static DeviceMeasurement fromGTS(GTSOutput source, boolean isNested) {
	DeviceMeasurement deviceMeasurement = new DeviceMeasurement();
	deviceMeasurement.setName(source.getClassName());
	deviceMeasurement.setValue(new BigDecimal(Double.valueOf(source.getPoints().get(0).getValue())));
	Warp10DeviceEvent.fromGTS(source, deviceMeasurement, isNested);
	return deviceMeasurement;
    }
}
