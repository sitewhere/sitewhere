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
