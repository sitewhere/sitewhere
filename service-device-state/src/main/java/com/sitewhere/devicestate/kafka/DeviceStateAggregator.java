/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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
