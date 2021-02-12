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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.warp10.Warp10Converter;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;

public class Warp10DeviceCommandInvocation implements Warp10Converter<IDeviceCommandInvocation> {

    /**
     * Property for initiator
     */
    public static final String PROP_INITIATOR = "init";

    /**
     * Property for initiator id
     */
    public static final String PROP_INITIATOR_ID = "inid";

    /**
     * Property for target
     */
    public static final String PROP_TARGET = "targ";

    /**
     * Property for target id
     */
    public static final String PROP_TARGET_ID = "tgid";

    /**
     * Property for command token
     */
    public static final String PROP_COMMAND_ID = "cmid";

    /**
     * Property for parameter values
     */
    public static final String PROP_PARAMETER_VALUES = "pmvl";

    @Override
    public GTSInput convert(IDeviceCommandInvocation source) {
	return Warp10DeviceCommandInvocation.toGTS(source);
    }

    @Override
    public IDeviceCommandInvocation convert(GTSOutput source) {
	return Warp10DeviceCommandInvocation.fromGTS(source);
    }

    public static GTSInput toGTS(IDeviceCommandInvocation source) {
	GTSInput gtsInput = GTSInput.builder();
	Warp10DeviceCommandInvocation.toGTS(source, gtsInput);
	return gtsInput;
    }

    public static void toGTS(IDeviceCommandInvocation source, GTSInput target) {
	Warp10DeviceEvent.toGTS(source, target, false);
	target.setName(source.getDeviceAssignmentId().toString());
	target.setTs(source.getReceivedDate().getTime());

	Map<String, String> labels = new HashMap<>();
	labels.put(PROP_INITIATOR, source.getInitiator().name());
	labels.put(PROP_INITIATOR_ID, source.getInitiatorId());
	labels.put(PROP_TARGET, source.getTarget().name());
	labels.put(PROP_TARGET_ID, source.getTargetId());
	labels.put(PROP_COMMAND_ID, source.getDeviceCommandId().toString());
	target.setLabels(labels);

	Map<String, String> attributes = new HashMap<>();
	for (String key : source.getParameterValues().keySet()) {
	    attributes.put(key, source.getParameterValues().get(key));
	}

	target.setAttributes(attributes);
    }

    public static DeviceCommandInvocation fromGTS(GTSOutput source) {
	DeviceCommandInvocation deviceCommandInvocation = new DeviceCommandInvocation();
	Warp10DeviceCommandInvocation.fromGTS(source, deviceCommandInvocation);
	return deviceCommandInvocation;
    }

    public static void fromGTS(GTSOutput source, DeviceCommandInvocation target) {
	Warp10DeviceEvent.fromGTS(source, target, false);

	String initiatorName = source.getLabels().get(PROP_INITIATOR);
	String initiatorId = source.getLabels().get(PROP_INITIATOR_ID);
	String targetName = source.getLabels().get(PROP_TARGET);
	String targetId = source.getLabels().get(PROP_TARGET_ID);
	UUID commandId = UUID.fromString(source.getLabels().get(PROP_COMMAND_ID));

	if (initiatorName != null) {
	    target.setInitiator(CommandInitiator.valueOf(initiatorName));
	}
	if (targetName != null) {
	    target.setTarget(CommandTarget.valueOf(targetName));
	}
	target.setInitiatorId(initiatorId);
	target.setTargetId(targetId);
	target.setDeviceCommandId(commandId);

	Map<String, String> params = new HashMap<String, String>();

	if (source.getAttributes() != null && source.getAttributes().size() > 0) {
	    for (String key : source.getAttributes().keySet()) {
		params.put(key, (String) source.getAttributes().get(key));
	    }
	}
	target.setParameterValues(params);
    }

}
