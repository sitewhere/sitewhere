/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.warp10db;

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
