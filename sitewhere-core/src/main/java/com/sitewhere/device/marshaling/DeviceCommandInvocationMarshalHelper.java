/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.marshaling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceCommandInvocation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Configurable helper class that allows {@link DeviceCommandInvocation} model
 * objects to be created from {@link IDeviceCommandInvocation} SPI objects.
 */
public class DeviceCommandInvocationMarshalHelper {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(DeviceCommandInvocationMarshalHelper.class);

    /** Device Management */
    private IDeviceManagement deviceManagement;

    /** Indicates whether to include command information */
    private boolean includeCommand = false;

    public DeviceCommandInvocationMarshalHelper(IDeviceManagement deviceManagement) {
	this(deviceManagement, false);
    }

    public DeviceCommandInvocationMarshalHelper(IDeviceManagement deviceManagement, boolean includeCommand) {
	this.deviceManagement = deviceManagement;
	this.includeCommand = includeCommand;
    }

    /**
     * Convert an {@link IDeviceCommandInvocation} to a
     * {@link DeviceCommandInvocation}, populating command information if requested
     * so the marshaled data includes it.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public MarshaledDeviceCommandInvocation convert(IDeviceCommandInvocation source) throws SiteWhereException {
	MarshaledDeviceCommandInvocation result = new MarshaledDeviceCommandInvocation();
	result.setInitiator(source.getInitiator());
	result.setInitiatorId(source.getInitiatorId());
	result.setTarget(source.getTarget());
	result.setTargetId(source.getTargetId());
	result.setDeviceCommandId(source.getDeviceCommandId());
	result.setParameterValues(source.getParameterValues());

	// Copy event fields.
	result.setId(source.getId());
	result.setAlternateId(source.getAlternateId());
	result.setEventType(source.getEventType());
	result.setDeviceId(source.getDeviceId());
	result.setDeviceAssignmentId(source.getDeviceAssignmentId());
	result.setAreaId(source.getAreaId());
	result.setAssetId(source.getAssetId());
	result.setEventDate(source.getEventDate());
	result.setReceivedDate(source.getReceivedDate());
	MetadataProvider.copy(source, result);

	if (isIncludeCommand()) {
	    if (source.getDeviceCommandId() == null) {
		LOGGER.warn("Device invocation is missing command id.");
		return result;
	    }
	    IDeviceCommand found = getDeviceManagement().getDeviceCommand(source.getDeviceCommandId());
	    if (found == null) {
		LOGGER.warn("Device invocation references a non-existent command token.");
		return result;
	    }
	    DeviceCommand command = DeviceCommand.copy(found);
	    if (command != null) {
		result.setCommand(command);
		result.setAsHtml(CommandHtmlHelper.getHtml(result));
	    }
	}
	return result;
    }

    public boolean isIncludeCommand() {
	return includeCommand;
    }

    public void setIncludeCommand(boolean includeCommand) {
	this.includeCommand = includeCommand;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }
}