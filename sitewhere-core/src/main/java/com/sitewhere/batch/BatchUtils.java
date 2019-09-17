/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.batch.request.IInvocationByAssignmentCriteriaRequest;
import com.sitewhere.spi.batch.request.IInvocationByDeviceCriteriaRequest;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

/**
 * Utility methods for batch operations.
 */
public class BatchUtils {

    /**
     * Resolve device search criteria to a list of device tokens.
     * 
     * @param criteria
     * @param deviceManagement
     * @param assetManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<String> resolveDeviceTokensForDeviceCriteria(IInvocationByDeviceCriteriaRequest criteria,
	    IDeviceManagement deviceManagement, IAssetManagement assetManagement) throws SiteWhereException {
	if (criteria.getDeviceTypeToken() == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}

	DeviceSearchCriteria search = new DeviceSearchCriteria(1, 0, null, null);
	search.setDeviceTypeToken(criteria.getDeviceTypeToken());
	List<IDevice> matches = deviceManagement.listDevices(search).getResults();

	List<String> deviceTokens = new ArrayList<String>();
	for (IDevice match : matches) {
	    deviceTokens.add(match.getToken());
	}
	return deviceTokens;
    }

    /**
     * Resolve device assignment search criteria to a list of device tokens.
     * 
     * @param criteria
     * @param deviceManagement
     * @param assetManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<String> resolveDeviceTokensForAssignmentCriteria(IInvocationByAssignmentCriteriaRequest criteria,
	    IDeviceManagement deviceManagement, IAssetManagement assetManagement) throws SiteWhereException {
	if (criteria.getDeviceTypeToken() == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}

	DeviceAssignmentSearchCriteria search = new DeviceAssignmentSearchCriteria(1, 0);
	search.setDeviceTypeTokens(Collections.singletonList(criteria.getDeviceTypeToken()));
	search.setCustomerTokens(criteria.getCustomerTokens());
	search.setAreaTokens(criteria.getAreaTokens());
	search.setAssetTokens(criteria.getAssetTokens());
	List<IDeviceAssignment> matches = deviceManagement.listDeviceAssignments(search).getResults();

	List<String> deviceTokens = new ArrayList<String>();
	for (IDeviceAssignment match : matches) {
	    IDevice device = deviceManagement.getDevice(match.getDeviceId());
	    if (!deviceTokens.contains(device.getToken())) {
		deviceTokens.add(device.getToken());
	    }
	}
	return deviceTokens;
    }
}