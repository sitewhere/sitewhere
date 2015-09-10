/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import com.sitewhere.device.marshaling.DeviceAssignmentMarshalHelper;
import com.sitewhere.device.marshaling.DeviceMarshalHelper;
import com.sitewhere.spi.user.ITenant;

/**
 * Marshals device assignment information while mocking underlying API calls.
 * 
 * @author Derek
 */
public class MockDeviceAssignmentMarshalHelper extends DeviceAssignmentMarshalHelper {

	/** Mock helper that returns a hard-coded device */
	private MockDeviceMarshalHelper helper = new MockDeviceMarshalHelper();

	public MockDeviceAssignmentMarshalHelper(ITenant tenant) {
		super(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.marshaling.DeviceAssignmentMarshalHelper#getDeviceHelper()
	 */
	@Override
	protected DeviceMarshalHelper getDeviceHelper() {
		return helper;
	}
}