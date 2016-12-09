/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.scheduling;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.uid.UniqueIdCounterMap;
import com.sitewhere.hbase.uid.UniqueIdCounterMapRowKeyBuilder;
import com.sitewhere.rest.model.scheduling.Schedule;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.common.IFilter;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * HBase specifics for dealing with SiteWhere schedules.
 * 
 * @author Derek
 */
public class HBaseSchedule {

    /** Length of group identifier (subset of 8 byte long) */
    public static final int IDENTIFIER_LENGTH = 4;

    /** Used to look up row keys from tokens */
    public static UniqueIdCounterMapRowKeyBuilder KEY_BUILDER = new UniqueIdCounterMapRowKeyBuilder() {

	@Override
	public UniqueIdCounterMap getMap(IHBaseContext context) {
	    return context.getScheduleIdManager().getScheduleKeys();
	}

	@Override
	public byte getTypeIdentifier() {
	    return SchedulesRecordType.Schedule.getType();
	}

	@Override
	public byte getPrimaryIdentifier() {
	    return (byte) 0;
	}

	@Override
	public int getKeyIdLength() {
	    return IDENTIFIER_LENGTH;
	}

	@Override
	public void throwInvalidKey() throws SiteWhereException {
	    throw new SiteWhereSystemException(ErrorCode.InvalidScheduleToken, ErrorLevel.ERROR);
	}
    };

    /**
     * Create a new schedule.
     * 
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static Schedule createSchedule(IHBaseContext context, IScheduleCreateRequest request)
	    throws SiteWhereException {
	String uuid = null;
	if (request.getToken() != null) {
	    if (getScheduleByToken(context, request.getToken()) != null) {
		throw new SiteWhereSystemException(ErrorCode.DuplicateScheduleToken, ErrorLevel.ERROR,
			HttpServletResponse.SC_CONFLICT);
	    }
	    uuid = KEY_BUILDER.getMap(context).useExistingId(request.getToken());
	} else {
	    uuid = KEY_BUILDER.getMap(context).createUniqueId();
	}

	// Use common logic so all backend implementations work the same.
	Schedule schedule = SiteWherePersistence.scheduleCreateLogic(request, uuid);

	Map<byte[], byte[]> qualifiers = new HashMap<byte[], byte[]>();
	return HBaseUtils.createOrUpdate(context, context.getPayloadMarshaler(), ISiteWhereHBase.SCHEDULES_TABLE_NAME,
		schedule, uuid, KEY_BUILDER, qualifiers);
    }

    /**
     * Update information for an existing schedule.
     * 
     * @param context
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static ISchedule updateSchedule(IHBaseContext context, String token, IScheduleCreateRequest request)
	    throws SiteWhereException {
	Schedule updated = assertSchedule(context, token);
	SiteWherePersistence.scheduleUpdateLogic(updated, request);
	return HBaseUtils.put(context, context.getPayloadMarshaler(), ISiteWhereHBase.SCHEDULES_TABLE_NAME, updated,
		token, KEY_BUILDER);
    }

    /**
     * Get schedule by unique token.
     * 
     * @param context
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public static Schedule getScheduleByToken(IHBaseContext context, String token) throws SiteWhereException {
	if (KEY_BUILDER.getMap(context).getValue(token) == null) {
	    return null;
	}
	return HBaseUtils.get(context, ISiteWhereHBase.SCHEDULES_TABLE_NAME, token, KEY_BUILDER, Schedule.class);
    }

    /**
     * List schedules that meet the given criteria.
     * 
     * @param context
     * @param includeDeleted
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<ISchedule> listSchedules(IHBaseContext context, boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	Comparator<Schedule> comparator = new Comparator<Schedule>() {

	    public int compare(Schedule a, Schedule b) {
		return -1 * (a.getCreatedDate().compareTo(b.getCreatedDate()));
	    }

	};
	IFilter<Schedule> filter = new IFilter<Schedule>() {

	    public boolean isExcluded(Schedule item) {
		return false;
	    }
	};
	return HBaseUtils.getFilteredList(context, ISiteWhereHBase.SCHEDULES_TABLE_NAME, KEY_BUILDER, includeDeleted,
		ISchedule.class, Schedule.class, filter, criteria, comparator);
    }

    /**
     * Delete an existing schedule.
     * 
     * @param context
     * @param token
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public static ISchedule deleteSchedule(IHBaseContext context, String token, boolean force)
	    throws SiteWhereException {
	return HBaseUtils.delete(context, context.getPayloadMarshaler(), ISiteWhereHBase.SCHEDULES_TABLE_NAME, token,
		force, KEY_BUILDER, Schedule.class);
    }

    /**
     * Get a {@link Schedule} by token or throw an exception if token is not
     * valid.
     * 
     * @param context
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public static Schedule assertSchedule(IHBaseContext context, String token) throws SiteWhereException {
	Schedule existing = getScheduleByToken(context, token);
	if (existing == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidScheduleToken, ErrorLevel.ERROR);
	}
	return existing;
    }
}