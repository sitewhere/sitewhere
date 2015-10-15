/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.scheduling;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Management interface for persistent scheduling implementations.
 * 
 * @author Derek
 */
public interface IScheduleManagement extends ITenantLifecycleComponent {

	/**
	 * Create a new schedule.
	 * 
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public ISchedule createSchedule(IScheduleCreateRequest request) throws SiteWhereException;

	/**
	 * Update an existing schedule.
	 * 
	 * @param token
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public ISchedule updateSchedule(String token, IScheduleCreateRequest request) throws SiteWhereException;
}