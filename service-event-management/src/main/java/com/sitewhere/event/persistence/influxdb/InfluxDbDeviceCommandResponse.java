/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.influxdb;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;

/**
 * Class for saving device command response data to InfluxDB.
 */
public class InfluxDbDeviceCommandResponse {

    /** Tag for originating event id */
    public static final String RSP_ORIGINATING_EVENT_ID = "origEvent";

    /** Tag for response event id */
    public static final String RSP_RESPONSE_EVENT_ID = "respEvent";

    /** Field for response */
    public static final String RSP_RESPONSE = "response";

    /**
     * Parse domain object from a value map.
     * 
     * @param values
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommandResponse parse(Map<String, Object> values) throws SiteWhereException {
	DeviceCommandResponse cr = new DeviceCommandResponse();
	InfluxDbDeviceCommandResponse.loadFromMap(cr, values);
	return cr;
    }

    /**
     * Load fields from value map.
     * 
     * @param event
     * @param values
     * @throws SiteWhereException
     */
    public static void loadFromMap(DeviceCommandResponse event, Map<String, Object> values) throws SiteWhereException {
	event.setEventType(DeviceEventType.CommandResponse);
	event.setOriginatingEventId(InfluxDbDeviceEvent.convertUUID((String) values.get(RSP_ORIGINATING_EVENT_ID)));
	event.setResponseEventId(InfluxDbDeviceEvent.convertUUID((String) values.get(RSP_RESPONSE_EVENT_ID)));
	event.setResponse(InfluxDbDeviceEvent.find(values, RSP_RESPONSE, true));

	InfluxDbDeviceEvent.loadFromMap(event, values);
    }

    /**
     * Save fields to builder.
     * 
     * @param event
     * @param builder
     * @throws SiteWhereException
     */
    public static void saveToBuilder(DeviceCommandResponse event, Point.Builder builder) throws SiteWhereException {
	builder.tag(RSP_ORIGINATING_EVENT_ID, event.getOriginatingEventId().toString());
	if (event.getResponseEventId() != null) {
	    builder.addField(RSP_RESPONSE_EVENT_ID, event.getResponseEventId().toString());
	}
	if (event.getResponse() != null) {
	    builder.addField(RSP_RESPONSE, event.getResponse());
	}

	InfluxDbDeviceEvent.saveToBuilder(event, builder);
    }

    /**
     * Get responses for a command invocation.
     * 
     * @param originatingEventId
     * @param influx
     * @param database
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<IDeviceCommandResponse> getResponsesForInvocation(UUID originatingEventId,
	    InfluxDB influx, String database) throws SiteWhereException {
	Query query = queryResponsesForInvocation(originatingEventId, database);
	QueryResult response = influx.query(query, TimeUnit.MILLISECONDS);
	List<IDeviceCommandResponse> results = InfluxDbDeviceEvent.eventsOfType(response, IDeviceCommandResponse.class);

	Query countQuery = queryResponsesForInvocationCount(originatingEventId, database);
	QueryResult countResponse = influx.query(countQuery);
	long count = InfluxDbDeviceEvent.parseCount(countResponse);
	return new SearchResults<IDeviceCommandResponse>(results, count);
    }

    /**
     * Find the list of responses for a command invocation.
     * 
     * @param originatingEventId
     * @param database
     * @return
     * @throws SiteWhereException
     */
    public static Query queryResponsesForInvocation(UUID originatingEventId, String database)
	    throws SiteWhereException {
	return new Query("SELECT * FROM " + InfluxDbDeviceEvent.COLLECTION_EVENTS + " where type='"
		+ DeviceEventType.CommandResponse + "' and " + InfluxDbDeviceCommandResponse.RSP_ORIGINATING_EVENT_ID
		+ "='" + originatingEventId + "' GROUP BY " + InfluxDbDeviceEvent.EVENT_ASSIGNMENT
		+ " ORDER BY time DESC", database);
    }

    /**
     * Count number of response for a command invocation.
     * 
     * @param originatingEventId
     * @param database
     * @return
     * @throws SiteWhereException
     */
    public static Query queryResponsesForInvocationCount(UUID originatingEventId, String database)
	    throws SiteWhereException {
	return new Query("SELECT count(eid) FROM " + InfluxDbDeviceEvent.COLLECTION_EVENTS + " where type='"
		+ DeviceEventType.CommandResponse + "' and " + InfluxDbDeviceCommandResponse.RSP_ORIGINATING_EVENT_ID
		+ "='" + originatingEventId + "' GROUP BY " + InfluxDbDeviceEvent.EVENT_ASSIGNMENT, database);
    }
}