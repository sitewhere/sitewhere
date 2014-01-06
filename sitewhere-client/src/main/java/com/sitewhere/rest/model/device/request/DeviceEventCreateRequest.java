/*
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.datatype.JsonDateSerializer;
import com.sitewhere.rest.model.device.DeviceEvent;
import com.sitewhere.spi.device.request.IDeviceEventCreateRequest;

/**
 * Holds common fields for creating {@link DeviceEvent} subclasses.
 * 
 * @author Derek
 */
public class DeviceEventCreateRequest extends MetadataProvider implements IDeviceEventCreateRequest {

	/** Date event occurred */
	private Date eventDate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IDeviceEventCreateRequest#getEventDate()
	 */
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
}