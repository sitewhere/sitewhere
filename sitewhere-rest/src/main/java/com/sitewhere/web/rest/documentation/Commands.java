/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import com.sitewhere.rest.model.device.command.CommandParameter;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.command.ParameterType;
import com.sitewhere.web.rest.documentation.ExampleData.Command_SetReportInterval;

/**
 * Examples of REST payloads for various device command methods.
 * 
 * @author Derek
 */
public class Commands {

	public static class DeviceCommandUpdateRequest {

		public Object generate() throws SiteWhereException {
			DeviceCommandCreateRequest request = new DeviceCommandCreateRequest();
			request.setName("setReportIntervalUpd");
			request.setDescription("Set the device reporting interval (in seconds) (updated).");
			request.setNamespace("http://mycompany.com/devices");
			CommandParameter interval = new CommandParameter();
			interval.setName("interval");
			interval.setType(ParameterType.Int32);
			interval.setRequired(false);
			request.getParameters().add(interval);
			CommandParameter reboot = new CommandParameter();
			reboot.setName("reboot");
			reboot.setType(ParameterType.Bool);
			reboot.setRequired(false);
			request.getParameters().add(reboot);
			return request;
		}
	}

	public static class DeviceCommandUpdateResponse {

		public Object generate() throws SiteWhereException {
			Command_SetReportInterval command = new ExampleData.Command_SetReportInterval();
			command.setName("setReportIntervalUpd");
			command.setDescription("Set the device reporting interval (in seconds) (updated).");
			command.getParameters().clear();
			CommandParameter interval = new CommandParameter();
			interval.setName("interval");
			interval.setType(ParameterType.Int32);
			interval.setRequired(false);
			command.getParameters().add(interval);
			CommandParameter reboot = new CommandParameter();
			reboot.setName("reboot");
			reboot.setType(ParameterType.Bool);
			reboot.setRequired(false);
			command.getParameters().add(reboot);
			return command;
		}
	}

	public static class DeviceCommandByTokenResponse {

		public Object generate() throws SiteWhereException {
			return ExampleData.COMMAND_SET_RPT_INTV;
		}
	}
}