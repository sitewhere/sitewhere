/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.persistence.hbase;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.uid.UniqueIdCounterMap;
import com.sitewhere.hbase.uid.UniqueIdCounterMapRowKeyBuilder;
import com.sitewhere.rest.model.scheduling.ScheduledJob;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.schedule.persistence.ScheduleManagementPersistence;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.common.IFilter;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * HBase specifics for dealing with SiteWhere scheduled jobs.
 * 
 * @author Derek
 */
public class HBaseScheduledJob {

    /** Length of group identifier (subset of 8 byte long) */
    public static final int IDENTIFIER_LENGTH = 4;

    /** Used to look up row keys from tokens */
    public static UniqueIdCounterMapRowKeyBuilder KEY_BUILDER = new UniqueIdCounterMapRowKeyBuilder() {

	@Override
	public UniqueIdCounterMap getMap(IHBaseContext context) {
	    return context.getScheduleIdManager().getScheduledJobKeys();
	}

	@Override
	public byte getTypeIdentifier() {
	    return SchedulesRecordType.ScheduledJob.getType();
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
	    throw new SiteWhereSystemException(ErrorCode.InvalidScheduledJobToken, ErrorLevel.ERROR);
	}
    };

    /**
     * Create a new scheduled job.
     * 
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static ScheduledJob createScheduledJob(IHBaseContext context, IScheduledJobCreateRequest request)
	    throws SiteWhereException {
	String uuid = null;
	if (request.getToken() != null) {
	    if (getScheduledJobByToken(context, request.getToken()) != null) {
		throw new SiteWhereSystemException(ErrorCode.DuplicateScheduledJobToken, ErrorLevel.ERROR);
	    }
	    uuid = KEY_BUILDER.getMap(context).useExistingId(request.getToken());
	} else {
	    uuid = KEY_BUILDER.getMap(context).createUniqueId();
	}

	// Use common logic so all backend implementations work the same.
	ScheduledJob job = ScheduleManagementPersistence.scheduledJobCreateLogic(request, uuid);

	Map<byte[], byte[]> qualifiers = new HashMap<byte[], byte[]>();
	return HBaseUtils.createOrUpdate(context, context.getPayloadMarshaler(), ISiteWhereHBase.SCHEDULES_TABLE_NAME,
		job, uuid, KEY_BUILDER, qualifiers);
    }

    /**
     * Update information for an existing scheduled job.
     * 
     * @param context
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static IScheduledJob updateScheduledJob(IHBaseContext context, String token,
	    IScheduledJobCreateRequest request) throws SiteWhereException {
	ScheduledJob updated = assertScheduledJob(context, token);
	ScheduleManagementPersistence.scheduledJobUpdateLogic(updated, request);
	return HBaseUtils.put(context, context.getPayloadMarshaler(), ISiteWhereHBase.SCHEDULES_TABLE_NAME, updated,
		token, KEY_BUILDER);
    }

    /**
     * Get scheduled job by unique token.
     * 
     * @param context
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public static ScheduledJob getScheduledJobByToken(IHBaseContext context, String token) throws SiteWhereException {
	if (KEY_BUILDER.getMap(context).getValue(token) == null) {
	    return null;
	}
	return HBaseUtils.get(context, ISiteWhereHBase.SCHEDULES_TABLE_NAME, token, KEY_BUILDER, ScheduledJob.class);
    }

    /**
     * List scheduled jobs that meet the given criteria.
     * 
     * @param context
     * @param includeDeleted
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IScheduledJob> listScheduledJobs(IHBaseContext context, boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	Comparator<ScheduledJob> comparator = new Comparator<ScheduledJob>() {

	    public int compare(ScheduledJob a, ScheduledJob b) {
		return -1 * (a.getCreatedDate().compareTo(b.getCreatedDate()));
	    }

	};
	IFilter<ScheduledJob> filter = new IFilter<ScheduledJob>() {

	    public boolean isExcluded(ScheduledJob item) {
		return false;
	    }
	};
	return HBaseUtils.getFilteredList(context, ISiteWhereHBase.SCHEDULES_TABLE_NAME, KEY_BUILDER, includeDeleted,
		IScheduledJob.class, ScheduledJob.class, filter, criteria, comparator);
    }

    /**
     * Delete an existing scheduled job.
     * 
     * @param context
     * @param token
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public static IScheduledJob deleteScheduledJob(IHBaseContext context, String token, boolean force)
	    throws SiteWhereException {
	return HBaseUtils.delete(context, context.getPayloadMarshaler(), ISiteWhereHBase.SCHEDULES_TABLE_NAME, token,
		force, KEY_BUILDER, ScheduledJob.class);
    }

    /**
     * Get a {@link ScheduledJob} by token or throw an exception if token is not
     * valid.
     * 
     * @param context
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public static ScheduledJob assertScheduledJob(IHBaseContext context, String token) throws SiteWhereException {
	ScheduledJob existing = getScheduledJobByToken(context, token);
	if (existing == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidScheduledJobToken, ErrorLevel.ERROR);
	}
	return existing;
    }
}