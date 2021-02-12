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
package com.sitewhere.device.kafka;

import java.util.UUID;

import org.apache.kafka.common.serialization.Serdes;

import com.sitewhere.device.spi.kafka.IDeviceInteractionEventsProducer;
import com.sitewhere.microservice.kafka.MicroserviceKafkaProducer;
import com.sitewhere.spi.SiteWhereException;

/**
 * Kafka producer that sends events triggered by device management interactions.
 */
public class DeviceInteractionEventsProducer extends MicroserviceKafkaProducer<UUID, byte[]>
	implements IDeviceInteractionEventsProducer {

    /*
     * @see
     * com.sitewhere.microservice.kafka.MicroserviceKafkaProducer#getKeySerializer()
     */
    @Override
    public Class<?> getKeySerializer() {
	return Serdes.UUID().serializer().getClass();
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#
     * getTargetTopicName()
     */
    @Override
    public String getTargetTopicName() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming().getInboundEventsTopic(getTenantEngine().getTenantResource());
    }
}
