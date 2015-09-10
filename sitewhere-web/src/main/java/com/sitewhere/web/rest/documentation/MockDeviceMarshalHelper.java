/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import com.sitewhere.device.marshaling.DeviceMarshalHelper;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.device.IDevice;

/**
 * Example marshaler that always returns the same device.
 * 
 * @author Derek
s */
public class MockDeviceMarshalHelper extends DeviceMarshalHelper {

	public MockDeviceMarshalHelper() {
		super(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.marshaling.DeviceMarshalHelper#convert(com.sitewhere.spi.device
	 * .IDevice, com.sitewhere.spi.asset.IAssetModuleManager)
	 */
	@Override
	public Device convert(IDevice source, IAssetModuleManager manager) throws SiteWhereException {
		return ExampleData.TRACKER1;
	}
}