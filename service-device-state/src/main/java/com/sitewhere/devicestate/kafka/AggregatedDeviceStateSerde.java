/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.kafka;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class AggregatedDeviceStateSerde implements Serde<AggregatedDeviceState> {

    /*
     * @see org.apache.kafka.common.serialization.Serde#serializer()
     */
    @Override
    public Serializer<AggregatedDeviceState> serializer() {
	return new AggregatedDeviceState.DeviceStateSerializer();
    }

    /*
     * @see org.apache.kafka.common.serialization.Serde#deserializer()
     */
    @Override
    public Deserializer<AggregatedDeviceState> deserializer() {
	return new AggregatedDeviceState.DeviceStateDeserializer();
    }
}
