/*
 * IDeviceRegistrationCreateRequest.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.request;

/**
 * Interface for arguments needed to register a device.
 * 
 * @author Derek
 */
public interface IDeviceRegistrationCreateRequest extends IDeviceEventCreateRequest {

	/**
	 * Get hardware id to be registered.
	 * 
	 * @return
	 */
	public String getHardwareId();

	/**
	 * Get token for device hardware specification.
	 * 
	 * @return
	 */
	public String getSpecificationToken();

	/**
	 * Get 'reply to' address for commands.
	 * 
	 * @return
	 */
	public String getReplyTo();
}