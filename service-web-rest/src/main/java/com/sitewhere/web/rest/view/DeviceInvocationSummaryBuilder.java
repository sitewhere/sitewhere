/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.view;

import java.util.List;

import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.view.DeviceCommandInvocationSummary;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceCommandInvocation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;

/**
 * Used to build a {@link DeviceCommandInvocationSummary}.
 * 
 * @author Derek
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
		IDevice device = deviceManagement.getDevice(response.getDeviceId());
		IDeviceEvent event = deviceEventManagement.getDeviceEventById(device.getId(),
			response.getResponseEventId());
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
	if (event instanceof IDeviceMeasurements) {
	    IDeviceMeasurements m = (IDeviceMeasurements) event;
	    return "Measurements (" + m.getMeasurementsSummary() + ")";
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