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
package com.sitewhere.devicestate.kafka;

import java.util.UUID;

import org.apache.kafka.streams.kstream.Aggregator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.grpc.event.EventModelConverter;
import com.sitewhere.grpc.model.DeviceEventModel.GAnyDeviceEvent.EventCase;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceAlert;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceLocation;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceMeasurement;
import com.sitewhere.grpc.model.DeviceEventModel.GProcessedEventPayload;
import com.sitewhere.spi.SiteWhereException;

/**
 * Aggregates processed device event data into an object that retains only the
 * data that should be updated in the device state master schema at the end of
 * the processing window.
 */
public class DeviceStateAggregator implements Aggregator<UUID, GProcessedEventPayload, AggregatedDeviceState> {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(DeviceStateAggregator.class);

    /*
     * @see org.apache.kafka.streams.kstream.Aggregator#apply(java.lang.Object,
     * java.lang.Object, java.lang.Object)
     */
    @Override
    public AggregatedDeviceState apply(UUID key, GProcessedEventPayload payload, AggregatedDeviceState aggregate) {
	AggregatedDeviceState updated = AggregatedDeviceState.copy(aggregate);

	try {
	    EventCase type = payload.getEvent().getEventCase();
	    switch (type) {
	    case LOCATION: {
		GDeviceLocation loc = payload.getEvent().getLocation();
		updated.updateFromLocation(EventModelConverter.asApiDeviceLocation(loc));
		break;
	    }
	    case MEASUREMENT: {
		GDeviceMeasurement mx = payload.getEvent().getMeasurement();
		updated.updateFromMeasurement(EventModelConverter.asApiDeviceMeasurement(mx));
		break;
	    }
	    case ALERT: {
		GDeviceAlert alert = payload.getEvent().getAlert();
		updated.updateFromAlert(EventModelConverter.asApiDeviceAlert(alert));
		break;
	    }
	    default: {
	    }
	    }
	} catch (SiteWhereException e) {
	    LOGGER.error("Unable to convert event for aggregation.", e);
	}

	return updated;
    }
}
