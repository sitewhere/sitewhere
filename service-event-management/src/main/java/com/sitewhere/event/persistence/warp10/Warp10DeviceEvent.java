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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.warp10.common.Warp10MetadataProvider;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;

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
    public static void toGTS(IDeviceEvent source, GTSInput target, boolean isNested) {
	Map<String, String> labels = new HashMap<>();
	labels.put(PROP_EVENT_TYPE, source.getEventType().name());
	labels.put(PROP_DEVICE_ASSIGNMENT_ID, source.getDeviceAssignmentId().toString());
	labels.put(PROP_ALTERNATE_ID, source.getAlternateId() != null ? source.getAlternateId().toString() : "");
	labels.put(PROP_ASSET_ID, source.getAssetId() != null ? source.getAssetId().toString() : "");
	labels.put(PROP_DEVICE_ID, source.getDeviceId().toString());
	labels.put(PROP_CUSTOMER_ID, source.getCustomerId() != null ? source.getCustomerId().toString() : "");
	labels.put(PROP_AREA_ID, source.getAreaId().toString());
	labels.put(PROP_RECEIVED_DATE, String.valueOf(source.getReceivedDate().getTime()));
	labels.put(PROP_EVENT_DATE, String.valueOf(source.getEventDate().getTime()));
	labels.put(PROP_ID, source.getId().toString()); // TODO: ver con jorge, esta propiedad hace que sea un registro
							// por evento !!!!!
	target.setLabels(labels);
    }

    /**
     * Copy information from GTS to model object.
     *
     * @param source
     * @param target
     */
    public static void fromGTS(GTSOutput source, DeviceEvent target, boolean isNested) {
	Map<String, String> labels = source.getLabels();

	target.setId(UUID.fromString(labels.get(PROP_ID)));
	target.setAlternateId(labels.get(PROP_ALTERNATE_ID));
	target.setEventType(DeviceEventType.valueOf(labels.get(PROP_EVENT_TYPE)));
	target.setDeviceId(
		!StringUtils.isBlank(labels.get(PROP_DEVICE_ID)) ? UUID.fromString(labels.get(PROP_DEVICE_ID)) : null);
	target.setDeviceAssignmentId(!StringUtils.isBlank(labels.get(PROP_DEVICE_ASSIGNMENT_ID))
		? UUID.fromString(labels.get(PROP_DEVICE_ASSIGNMENT_ID))
		: null);
	target.setCustomerId(
		!StringUtils.isBlank(labels.get(PROP_CUSTOMER_ID)) ? UUID.fromString(labels.get(PROP_CUSTOMER_ID))
			: null);
	target.setAreaId(
		!StringUtils.isBlank(labels.get(PROP_AREA_ID)) ? UUID.fromString(labels.get(PROP_AREA_ID)) : null);
	target.setReceivedDate(new Date(Long.valueOf(labels.get(PROP_RECEIVED_DATE))));
	target.setEventDate(new Date(Long.valueOf(labels.get(PROP_EVENT_DATE))));

	Warp10MetadataProvider.fromGTS(source, target);
    }
}
