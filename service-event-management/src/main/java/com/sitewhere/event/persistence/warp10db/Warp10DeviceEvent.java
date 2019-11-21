/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.warp10db;

import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Warp10DeviceEvent {

    /** Property for id */
    public static final String PROP_ID = "_id";

    /** Alternate (external) id */
    public static final String PROP_ALTERNATE_ID = "alid";

    /** Event type indicator */
    public static final String PROP_EVENT_TYPE = "evty";

    /** Property for device id */
    public static final String PROP_DEVICE_ID = "dvid";

    /** Property for device assignment id */
    public static final String PROP_DEVICE_ASSIGNMENT_ID = "asid";

    /** Property for customer id */
    public static final String PROP_CUSTOMER_ID = "csid";

    /** Property for area id */
    public static final String PROP_AREA_ID = "arid";

    /** Property for asset id */
    public static final String PROP_ASSET_ID = "assd";

    /** Property for time measurements were taken */
    public static final String PROP_EVENT_DATE = "evdt";

    /** Property for time measurements were received */
    public static final String PROP_RECEIVED_DATE = "rcdt";

    /**
     * Copy information from SPI into GTS
     *
     * @param source
     * @param target
     */
    public static void toGTS(IDeviceEvent source, GTSInput target) {
        Map labels = new HashMap<String, String>();
        labels.put(PROP_EVENT_TYPE, source.getEventType().name());
        labels.put(PROP_DEVICE_ASSIGNMENT_ID, source.getDeviceAssignmentId().toString());
        labels.put(PROP_ALTERNATE_ID, source.getAlternateId() != null ? source.getAlternateId().toString() : "");
        labels.put(PROP_DEVICE_ID, source.getDeviceId().toString());
        labels.put(PROP_CUSTOMER_ID, source.getCustomerId() != null ? source.getCustomerId() : "");
        labels.put(PROP_AREA_ID, source.getAreaId().toString());
        labels.put(PROP_RECEIVED_DATE, String.valueOf(source.getReceivedDate().getTime()));
        labels.put(PROP_EVENT_DATE, String.valueOf(source.getEventDate().getTime()));
        target.setLabels(labels);

        Map attributes = new HashMap<String, String>();
        attributes.put(PROP_ID, source.getId().toString());
        target.setAttributes(attributes);

    }

    /**
     * Copy information from GTS to model object.
     *
     * @param source
     * @param target
     */
    public static void fromGTS(GTSOutput source, DeviceEvent target) {
        Map<String, String> labels = source.getLabels();
        Map<String, String> attributes = source.getAttributes();

        target.setId(UUID.fromString(attributes.get(PROP_ID)));

        target.setAlternateId(labels.get(PROP_ALTERNATE_ID));
        target.setEventType(DeviceEventType.valueOf(labels.get(PROP_EVENT_TYPE)));
        target.setDeviceId(UUID.fromString(labels.get(PROP_DEVICE_ID)));
        target.setDeviceAssignmentId(UUID.fromString(labels.get(PROP_DEVICE_ASSIGNMENT_ID)));
        target.setCustomerId(UUID.fromString(labels.get(PROP_CUSTOMER_ID)));
        target.setAreaId(UUID.fromString(labels.get(PROP_AREA_ID)));
        target.setReceivedDate(new Date(labels.get(PROP_RECEIVED_DATE)));
    }

}
