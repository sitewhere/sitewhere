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
package com.sitewhere.web.rest.view;

import java.util.List;

import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.view.DeviceCommandInvocationSummary;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceCommandInvocation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;

/**
 * Used to build a {@link DeviceCommandInvocationSummary}.
 */
public class DeviceInvocationSummaryBuilder {

    /**
     * Creates a {@link DeviceCommandInvocationSummary} using data from a
     * {@link DeviceCommandInvocation} that has its command information populated.
     * 
     * @param invocation
     * @param responses
     * @param deviceManagement
     * @param deviceEventManagement
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommandInvocationSummary build(MarshaledDeviceCommandInvocation invocation,
	    List<IDeviceCommandResponse> responses, IDeviceManagement deviceManagement,
	    IDeviceEventManagement deviceEventManagement) throws SiteWhereException {
	DeviceCommandInvocationSummary summary = new DeviceCommandInvocationSummary();
	summary.setName(invocation.getCommand().getName());
	summary.setNamespace(invocation.getCommand().getNamespace());
	summary.setInvocationDate(invocation.getEventDate());
	for (ICommandParameter parameter : invocation.getCommand().getParameters()) {
	    DeviceCommandInvocationSummary.Parameter param = new DeviceCommandInvocationSummary.Parameter();
	    param.setName(parameter.getName());
	    param.setType(parameter.getType().name());
	    param.setRequired(parameter.isRequired());
	    param.setValue(invocation.getParameterValues().get(parameter.getName()));
	    summary.getParameters().add(param);
	}
	for (IDeviceCommandResponse response : responses) {
	    DeviceCommandInvocationSummary.Response rsp = new DeviceCommandInvocationSummary.Response();
	    rsp.setDate(response.getEventDate());
	    if (response.getResponseEventId() != null) {
		IDeviceEvent event = deviceEventManagement.getDeviceEventById(response.getResponseEventId());
		rsp.setDescription(getDeviceEventDescription(event));
	    } else if (response.getResponse() != null) {
		rsp.setDescription("Ack (\"" + response.getResponse() + "\")");
	    } else {
		rsp.setDescription("Response received.");
	    }
	    summary.getResponses().add(rsp);
	}
	MetadataProvider.copy(invocation, summary);
	return summary;
    }

    /**
     * Get a short description of a device event.
     * 
     * @param event
     * @return
     * @throws SiteWhereException
     */
    public static String getDeviceEventDescription(IDeviceEvent event) throws SiteWhereException {
	if (event instanceof IDeviceMeasurement) {
	    IDeviceMeasurement mx = (IDeviceMeasurement) event;
	    return "Measurement (" + mx.getName() + "/" + mx.getValue() + ")";
	} else if (event instanceof IDeviceLocation) {
	    IDeviceLocation l = (IDeviceLocation) event;
	    return "Location (" + l.getLatitude() + "/" + l.getLongitude() + "/" + l.getElevation() + ")";
	} else if (event instanceof IDeviceAlert) {
	    IDeviceAlert a = (IDeviceAlert) event;
	    return "Alert (\"" + a.getMessage() + "\")";
	}
	return "Unknown Event Type";
    }
}