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
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.user.ITenant;

/**
 * Example marshaler that always returns the same device.
 * 
 * @author Derek s
 */
public class MockDeviceMarshalHelper extends DeviceMarshalHelper {

	/** Mock device management imlementation that wraps sample data */
	private MockDeviceManagement deviceManagement = new MockDeviceManagement();

	/** Mock device assignment marshal helper */
	private MockDeviceAssignmentMarshalHelper assignmentHelper;

	public MockDeviceMarshalHelper() {
		super(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.marshaling.DeviceMarshalHelper#getDeviceManagement(com.sitewhere
	 * .spi.user.ITenant)
	 */
	@Override
	protected IDeviceManagement getDeviceManagement(ITenant tenant) throws SiteWhereException {
		return deviceManagement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.marshaling.DeviceMarshalHelper#getAssignmentHelper()
	 */
	@Override
	protected DeviceAssignmentMarshalHelper getAssignmentHelper() {
		if (assignmentHelper == null) {
			assignmentHelper = new MockDeviceAssignmentMarshalHelper();
			assignmentHelper.setIncludeAsset(false);
			assignmentHelper.setIncludeDevice(false);
			assignmentHelper.setIncludeSite(false);
		}
		return assignmentHelper;
	}
}