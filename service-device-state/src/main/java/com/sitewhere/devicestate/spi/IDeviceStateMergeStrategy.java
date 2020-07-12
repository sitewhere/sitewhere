/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.spi;

import java.util.UUID;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.state.IDeviceState;
import com.sitewhere.spi.device.state.request.IDeviceStateEventMergeRequest;

/**
 * Determines strategy for merging events into device state.
 */
public interface IDeviceStateMergeStrategy<T extends IDeviceState> {

    /**
     * Merges event updates into device state.
     * 
     * @param deviceStateId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    T merge(UUID deviceStateId, IDeviceStateEventMergeRequest request) throws SiteWhereException;
}
