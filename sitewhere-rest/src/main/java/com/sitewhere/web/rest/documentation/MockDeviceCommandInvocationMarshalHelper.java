/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import com.sitewhere.device.marshaling.DeviceCommandInvocationMarshalHelper;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Mocks device command invocation marshaling logic for REST documentation.
 * 
 * @author Derek
 */
public class MockDeviceCommandInvocationMarshalHelper extends DeviceCommandInvocationMarshalHelper {

	/** Mocks device management data */
	private MockDeviceManagement deviceManagement = new MockDeviceManagement();

	public MockDeviceCommandInvocationMarshalHelper() {
		super(null);
	}

	public MockDeviceCommandInvocationMarshalHelper(ITenant tenant, boolean includeCommand) {
		super(null, includeCommand);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.marshaling.DeviceCommandInvocationMarshalHelper#
	 * getDeviceManagement(com.sitewhere.spi.user.ITenant)
	 */
	@Override
	protected IDeviceManagement getDeviceManagement(ITenant tenant) throws SiteWhereException {
		return deviceManagement;
	}
}