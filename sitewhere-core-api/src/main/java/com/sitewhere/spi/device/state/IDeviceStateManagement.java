/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.state;

import java.util.UUID;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IDeviceStateSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Interface for device state management operations.
 */
public interface IDeviceStateManagement extends ITenantEngineLifecycleComponent {

    /**
     * Create device state.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceState createDeviceState(IDeviceStateCreateRequest request) throws SiteWhereException;

    /**
     * Get device state by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceState getDeviceState(UUID id) throws SiteWhereException;

    /**
     * Get device state by device assignment id.
     * 
     * @param assignmentId
     * @return
     * @throws SiteWhereException
     */
    public IDeviceState getDeviceStateByDeviceAssignmentId(UUID assignmentId) throws SiteWhereException;

    /**
     * Search for device states that match the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceState> searchDeviceStates(IDeviceStateSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Update existing device state.
     * 
     * @param id
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceState updateDeviceState(UUID id, IDeviceStateCreateRequest request) throws SiteWhereException;

    /**
     * Delete existing device state.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceState deleteDeviceState(UUID id) throws SiteWhereException;
}
