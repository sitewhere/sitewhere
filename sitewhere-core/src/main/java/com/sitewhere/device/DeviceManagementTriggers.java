/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device;

import com.sitewhere.SiteWhere;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.device.request.IBatchOperationCreateRequest;

/**
 * Adds triggers for processing related to device management API calls.
 * 
 * @author Derek
 */
public class DeviceManagementTriggers extends DeviceManagementDecorator {

	public DeviceManagementTriggers(IDeviceManagement delegate) {
		super(delegate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.DeviceManagementDecorator#createBatchOperation(com.sitewhere.
	 * spi.device.request.IBatchOperationCreateRequest)
	 */
	@Override
	public IBatchOperation createBatchOperation(IBatchOperationCreateRequest request)
			throws SiteWhereException {
		IBatchOperation operation = super.createBatchOperation(request);
		SiteWhere.getServer().getDeviceCommunication(getTenant()).getBatchOperationManager().process(
				operation);
		return operation;
	}
}