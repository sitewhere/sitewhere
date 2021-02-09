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

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.warp10.Warp10Persistence;
import com.sitewhere.warp10.rest.GTSOutput;

public class Warp10DeviceEventManagementPersistence extends Warp10Persistence {

    /**
     * Given a {@link GTSOutput} that contains event information, unmarhal it to the
     * correct type.
     *
     * @param found
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceEvent unmarshalEvent(GTSOutput found) throws SiteWhereException {
        String type = found.getLabels().get(Warp10DeviceEvent.PROP_EVENT_TYPE);
        if (type == null) {
            throw new SiteWhereException("Event matched but did not contain event type field.");
        }
        DeviceEventType eventType = DeviceEventType.valueOf(type);
        if (eventType == null) {
            throw new SiteWhereException("Event type not recognized: " + type);
        }

        switch (eventType) {
            case Measurement: {
                return Warp10DeviceMeasurement.fromGTS(found, false);
            }
            case Location: {
                return Warp10DeviceLocation.fromGTS(found, false);
            }
            case Alert: {
                return Warp10DeviceAlert.fromGTS(found, false);
            }

            case CommandInvocation: {
                return Warp10DeviceCommandInvocation.fromGTS(found);
            }
            case CommandResponse: {
                return Warp10DeviceCommandResponse.fromGTS(found);
            }

            case StateChange: {
                return Warp10DeviceStateChange.fromGTS(found);
            }
            default: {
                throw new SiteWhereException("Event type not handled: " + type);
            }
        }
    }
}
