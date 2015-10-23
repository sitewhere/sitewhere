/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.scheduling;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;

/**
 * Trigger actions based on schedule management API calls.
 * 
 * @author Derek
 */
public class ScheduleManagementTriggers extends ScheduleManagementDecorator {

	public ScheduleManagementTriggers(IScheduleManagement delegate) {
		super(delegate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.scheduling.ScheduleManagementDecorator#createScheduledJob(
	 * com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest)
	 */
	@Override
	public IScheduledJob createScheduledJob(IScheduledJobCreateRequest request) throws SiteWhereException {
		IScheduledJob job = super.createScheduledJob(request);
		return job;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.scheduling.ScheduleManagementDecorator#deleteScheduledJob(
	 * java.lang.String, boolean)
	 */
	@Override
	public IScheduledJob deleteScheduledJob(String token, boolean force) throws SiteWhereException {
		return super.deleteScheduledJob(token, force);
	}
}