/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandStatus;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;

/**
 * Model object used to create a new {@link DeviceCommandInvocation} via REST APIs.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceCommandInvocationCreateRequest extends DeviceEventCreateRequest
		implements IDeviceCommandInvocationCreateRequest, Serializable {

	/** Serialization version identifier */
	private static final long serialVersionUID = 8638261326864469204L;

	/** Type of actor that initiated the command */
	private CommandInitiator initiator;

	/** Id of actor that initiated the command */
	private String initiatorId;

	/** Type of actor that will receive the command */
	private CommandTarget target;

	/** Id of actor that will receive the command */
	private String targetId;

	/** Token of command to be invoked */
	private String commandToken;

	/** Values to use for command parameters */
	private Map<String, String> parameterValues = new HashMap<String, String>();

	/** Current invocation status */
	private CommandStatus status;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest#
	 * getInitiator()
	 */
	public CommandInitiator getInitiator() {
		return initiator;
	}

	public void setInitiator(CommandInitiator initiator) {
		this.initiator = initiator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest#
	 * getInitiatorId()
	 */
	public String getInitiatorId() {
		return initiatorId;
	}

	public void setInitiatorId(String initiatorId) {
		this.initiatorId = initiatorId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest#
	 * getTarget ()
	 */
	public CommandTarget getTarget() {
		return target;
	}

	public void setTarget(CommandTarget target) {
		this.target = target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest#
	 * getTargetId()
	 */
	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest#
	 * getCommandToken()
	 */
	public String getCommandToken() {
		return commandToken;
	}

	public void setCommandToken(String commandToken) {
		this.commandToken = commandToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest#
	 * getParameterValues()
	 */
	public Map<String, String> getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(Map<String, String> parameterValues) {
		this.parameterValues = parameterValues;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest#
	 * getStatus ()
	 */
	public CommandStatus getStatus() {
		return status;
	}

	public void setStatus(CommandStatus status) {
		this.status = status;
	}

	public static class Builder
			extends DeviceEventCreateRequest.Builder<DeviceCommandInvocationCreateRequest> {

		private DeviceCommandInvocationCreateRequest request = new DeviceCommandInvocationCreateRequest();

		public Builder(String commandToken, String target) throws SiteWhereException {
			request.setCommandToken(commandToken);
			request.setInitiator(CommandInitiator.Script);
			request.setTarget(CommandTarget.Assignment);
			request.setTargetId(target);
		}

		public Builder parameterValue(String name, String value) {
			request.getParameterValues().put(name, value);
			return this;
		}

		public Builder metadata(String name, String value) {
			if (request.getMetadata() == null) {
				request.setMetadata(new HashMap<String, String>());
			}
			request.getMetadata().put(name, value);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sitewhere.rest.model.device.event.request.DeviceEventCreateRequest.Builder#
		 * getRequest()
		 */
		@Override
		public DeviceCommandInvocationCreateRequest getRequest() {
			return request;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sitewhere.rest.model.device.event.request.DeviceEventCreateRequest.Builder#
		 * build()
		 */
		@Override
		public DeviceCommandInvocationCreateRequest build() {
			return request;
		}
	}
}