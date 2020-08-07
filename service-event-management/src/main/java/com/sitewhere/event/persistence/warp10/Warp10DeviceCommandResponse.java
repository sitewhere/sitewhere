/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.warp10;

import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.warp10.Warp10Converter;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;

import java.util.UUID;

public class Warp10DeviceCommandResponse implements Warp10Converter<IDeviceCommandResponse> {

    /** Property for originating event id */
    public static final String PROP_ORIGINATING_EVENT_ID = "orig";

    /** Property for response event id */
    public static final String PROP_RESPONSE_EVENT_ID = "rsid";

    /** Property for response */
    public static final String PROP_RESPONSE = "resp";

    @Override
    public GTSInput convert(IDeviceCommandResponse source) {
        return Warp10DeviceCommandResponse.toGTS(source);
    }

    @Override
    public IDeviceCommandResponse convert(GTSOutput source) {
        return Warp10DeviceCommandResponse.fromGTS(source);
    }

    public static GTSInput toGTS(IDeviceCommandResponse source) {
        GTSInput gtsInput = GTSInput.builder();
        Warp10DeviceCommandResponse.toGTS(source, gtsInput);
        return gtsInput;
    }

    public static void toGTS(IDeviceCommandResponse source, GTSInput target) {
        Warp10DeviceEvent.toGTS(source, target, false);
        target.setName(source.getDeviceAssignmentId().toString());
        target.setTs(source.getReceivedDate().getTime());
        target.getLabels().put(PROP_ORIGINATING_EVENT_ID, source.getOriginatingEventId().toString());
        target.getLabels().put(PROP_RESPONSE_EVENT_ID, source.getResponseEventId().toString());
        target.getLabels().put(PROP_RESPONSE, source.getResponse());
    }

    public static DeviceCommandResponse fromGTS(GTSOutput source){
        DeviceCommandResponse deviceCommandResponse = new DeviceCommandResponse();
        Warp10DeviceCommandResponse.fromGTS(source, deviceCommandResponse);
        return deviceCommandResponse;
    }

    public static void fromGTS(GTSOutput source, DeviceCommandResponse target){
        Warp10DeviceEvent.fromGTS(source, target, false);

        UUID originator = UUID.fromString(source.getLabels().get(PROP_ORIGINATING_EVENT_ID));
        UUID responder =  UUID.fromString(source.getLabels().get(PROP_RESPONSE_EVENT_ID));
        String response = source.getLabels().get(PROP_RESPONSE);

        target.setOriginatingEventId(originator);
        target.setResponseEventId(responder);
        target.setResponse(response);
    }

}
