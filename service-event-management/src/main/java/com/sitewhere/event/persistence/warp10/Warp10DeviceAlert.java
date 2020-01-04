/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.warp10;

import java.util.HashMap;
import java.util.Map;

import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.warp10.Warp10Converter;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;

public class Warp10DeviceAlert implements Warp10Converter<IDeviceAlert> {

    /** Property for source */
    public static final String PROP_SOURCE = "sorc";

    /** Property for alert level */
    public static final String PROP_LEVEL = "levl";

    /** Property for type */
    public static final String PROP_TYPE = "type";

    /** Property for message */
    public static final String PROP_MESSAGE = "mesg";

    @Override
    public GTSInput convert(IDeviceAlert source) {
	return Warp10DeviceAlert.toGTS(source, false);
    }

    @Override
    public IDeviceAlert convert(GTSOutput source) {
	return Warp10DeviceAlert.fromGTS(source, false);
    }

    public static GTSInput toGTS(IDeviceAlert source, boolean isNested) {
	GTSInput gtsInput = GTSInput.builder();
	Warp10DeviceAlert.toGTS(source, gtsInput, isNested);
	return gtsInput;
    }

    public static void toGTS(IDeviceAlert source, GTSInput target, boolean isNested) {
	Warp10DeviceEvent.toGTS(source, target, isNested);
	target.setName(source.getDeviceAssignmentId().toString());
	target.setTs(source.getReceivedDate().getTime());

	Map<String, String> labels = new HashMap<>();
	labels.put(PROP_SOURCE, source.getSource().name());
	labels.put(PROP_LEVEL, source.getLevel().name());
	labels.put(PROP_TYPE, source.getType());
	labels.put(PROP_MESSAGE, source.getMessage());
	target.setLabels(labels);
    }

    public static DeviceAlert fromGTS(GTSOutput source, boolean isNested) {
	DeviceAlert deviceAlert = new DeviceAlert();
	Warp10DeviceAlert.fromGTS(source, deviceAlert, isNested);
	return deviceAlert;
    }

    public static void fromGTS(GTSOutput source, DeviceAlert target, boolean isNested) {
	Warp10DeviceEvent.fromGTS(source, target, isNested);

	String sourceName = source.getLabels().get(PROP_SOURCE);
	String levelName = source.getLabels().get(PROP_LEVEL);
	String type = source.getLabels().get(PROP_TYPE);
	String message = source.getLabels().get(PROP_MESSAGE);

	if (sourceName != null) {
	    target.setSource(AlertSource.valueOf(sourceName));
	}
	if (levelName != null) {
	    target.setLevel(AlertLevel.valueOf(levelName));
	}
	target.setType(type);
	target.setMessage(message);
    }
}
