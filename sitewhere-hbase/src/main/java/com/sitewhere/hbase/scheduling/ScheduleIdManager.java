/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.scheduling;

import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.uid.UniqueIdCounterMap;
import com.sitewhere.hbase.uid.UniqueIdType;
import com.sitewhere.spi.SiteWhereException;

/**
 * Wraps id management for scheduling subsystem.
 * 
 * @author Derek
 */
public class ScheduleIdManager implements IScheduleIdManager {

	/** Manager for site tokens */
	private UniqueIdCounterMap scheduleKeys;

	/** Manager for device ids */
	private UniqueIdCounterMap scheduledJobKeys;

	/**
	 * Load key managers from HBase.
	 * 
	 * @param hbase
	 * @throws SiteWhereException
	 */
	public void load(IHBaseContext context) throws SiteWhereException {
		scheduleKeys =
				new UniqueIdCounterMap(context, UniqueIdType.ScheduleKey.getIndicator(),
						UniqueIdType.ScheduleValue.getIndicator());
		scheduleKeys.refresh();

		scheduledJobKeys =
				new UniqueIdCounterMap(context, UniqueIdType.ScheduledJobKey.getIndicator(),
						UniqueIdType.ScheduledJobValue.getIndicator());
		scheduledJobKeys.refresh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.scheduling.IScheduleIdManager#getScheduleKeys()
	 */
	@Override
	public UniqueIdCounterMap getScheduleKeys() {
		return scheduleKeys;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.scheduling.IScheduleIdManager#getScheduledJobKeys()
	 */
	@Override
	public UniqueIdCounterMap getScheduledJobKeys() {
		return scheduledJobKeys;
	}
}