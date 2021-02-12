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

import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.warp10.Warp10Converter;
import com.sitewhere.warp10.common.Warp10MetadataProvider;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;

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
	target.setLat(source.getLatitude().doubleValue());
	target.setLon(source.getLongitude().doubleValue());
	target.setName(source.getDeviceAssignmentId().toString());

	if (source.getElevation() != null) {
	    target.setElev(source.getElevation().longValue());
	}
	Warp10MetadataProvider.toGTS(source, target);
    }

    public static DeviceLocation fromGTS(GTSOutput source, boolean isNested) {
	DeviceLocation deviceLocation = new DeviceLocation();
	Warp10DeviceEvent.fromGTS(source, deviceLocation, isNested);
	deviceLocation.setElevation(new BigDecimal(source.getPoints().get(0).getElevation().doubleValue()));
	deviceLocation.setLongitude(new BigDecimal(source.getPoints().get(0).getLongitude()));
	deviceLocation.setLatitude(new BigDecimal(source.getPoints().get(0).getLatitude()));
	return deviceLocation;
    }
}
