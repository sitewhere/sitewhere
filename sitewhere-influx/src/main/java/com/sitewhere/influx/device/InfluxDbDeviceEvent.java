/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.influx.device;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.joda.time.format.ISODateTimeFormat;

import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * Common base class for saving device event data to InfluxDB.
 * 
 * @author Derek
 */
public class InfluxDbDeviceEvent {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Collection for events */
    public static final String COLLECTION_EVENTS = "events";

    /** Event id tag */
    public static final String EVENT_ID = "eid";

    /** Event type tag */
    public static final String EVENT_TYPE = "type";

    /** Event assignment tag */
    public static final String EVENT_ASSIGNMENT = "assignment";

    /** Event site tag */
    public static final String EVENT_SITE = "site";

    /** Event asset module tag */
    public static final String EVENT_ASSET_MODULE = "assetmodule";

    /** Event asset tag */
    public static final String EVENT_ASSET = "asset";

    /** Event received date field */
    public static final String RECEIVED_DATE = "rcvdate";

    /** Event metadata field */
    public static final String EVENT_METADATA_PREFIX = "meta:";

    /**
     * Return a builder for the events collection.
     * 
     * @return
     * @throws SiteWhereException
     */
    public static Point.Builder createBuilder() throws SiteWhereException {
	return Point.measurement(COLLECTION_EVENTS);
    }

    /**
     * Get an event by unique id.
     * 
     * @param eventId
     * @param influx
     * @param database
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceEvent getEventById(String eventId, InfluxDB influx, String database)
	    throws SiteWhereException {
	Query query = new Query(
		"SELECT * FROM " + InfluxDbDeviceEvent.COLLECTION_EVENTS + " where eid='" + eventId + "'", database);
	QueryResult response = influx.query(query, TimeUnit.MILLISECONDS);
	List<IDeviceEvent> results = InfluxDbDeviceEvent.eventsOfType(response, IDeviceEvent.class);
	if (results.size() > 0) {
	    return results.get(0);
	}
	return null;
    }

    /**
     * Search for of events of a given type associated with an assignment.
     * 
     * @param assignmentToken
     * @param type
     * @param criteria
     * @param influx
     * @param database
     * @param clazz
     * @return
     * @throws SiteWhereException
     */
    public static <T> SearchResults<T> searchByAssignment(String assignmentToken, DeviceEventType type,
	    ISearchCriteria criteria, InfluxDB influx, String database, Class<T> clazz) throws SiteWhereException {
	Query query = InfluxDbDeviceEvent.queryEventsOfTypeForAssignment(type, assignmentToken, criteria, database);
	LOGGER.debug("Query: " + query.getCommand());
	QueryResult response = influx.query(query, TimeUnit.MILLISECONDS);
	List<T> results = InfluxDbDeviceEvent.eventsOfType(response, clazz);

	Query countQuery = InfluxDbDeviceEvent.queryEventsOfTypeForAssignmentCount(type, assignmentToken, criteria,
		database);
	LOGGER.debug("Count: " + countQuery.getCommand());
	QueryResult countResponse = influx.query(countQuery);
	long count = parseCount(countResponse);
	return new SearchResults<T>(results, count);
    }

    /**
     * Search for of events of a given type associated with a site.
     * 
     * @param siteToken
     * @param type
     * @param criteria
     * @param influx
     * @param database
     * @param clazz
     * @return
     * @throws SiteWhereException
     */
    public static <T> SearchResults<T> searchBySite(String siteToken, DeviceEventType type, ISearchCriteria criteria,
	    InfluxDB influx, String database, Class<T> clazz) throws SiteWhereException {
	Query query = InfluxDbDeviceEvent.queryEventsOfTypeForSite(type, siteToken, criteria, database);
	LOGGER.debug("Query: " + query.getCommand());
	QueryResult response = influx.query(query, TimeUnit.MILLISECONDS);
	List<T> results = InfluxDbDeviceEvent.eventsOfType(response, clazz);

	Query countQuery = InfluxDbDeviceEvent.queryEventsOfTypeForSiteCount(type, siteToken, criteria, database);
	LOGGER.debug("Count: " + countQuery.getCommand());
	QueryResult countResponse = influx.query(countQuery);
	long count = parseCount(countResponse);
	return new SearchResults<T>(results, count);
    }

    /**
     * Get a query for events of a given type associated with an assignment and
     * that meet the search criteria.
     * 
     * @param type
     * @param assignmentToken
     * @param criteria
     * @param database
     * @return
     * @throws SiteWhereException
     */
    public static Query queryEventsOfTypeForAssignment(DeviceEventType type, String assignmentToken,
	    ISearchCriteria criteria, String database) throws SiteWhereException {
	return new Query("SELECT * FROM " + InfluxDbDeviceEvent.COLLECTION_EVENTS + " where type='" + type.name()
		+ "' and " + InfluxDbDeviceEvent.EVENT_ASSIGNMENT + "='" + assignmentToken + "'"
		+ buildDateRangeCriteria(criteria) + " GROUP BY " + EVENT_ASSIGNMENT + " ORDER BY time DESC"
		+ buildPagingCriteria(criteria), database);
    }

    /**
     * Get a query for counting events of a given type associated with an
     * assignment and that meet the search criteria.
     * 
     * @param type
     * @param assignmentToken
     * @param criteria
     * @param database
     * @return
     * @throws SiteWhereException
     */
    public static Query queryEventsOfTypeForAssignmentCount(DeviceEventType type, String assignmentToken,
	    ISearchCriteria criteria, String database) throws SiteWhereException {
	return new Query("SELECT count(" + EVENT_ID + ") FROM " + InfluxDbDeviceEvent.COLLECTION_EVENTS
		+ " where type='" + type.name() + "' and " + InfluxDbDeviceEvent.EVENT_ASSIGNMENT + "='"
		+ assignmentToken + "'" + buildDateRangeCriteria(criteria) + " GROUP BY " + EVENT_ASSIGNMENT, database);
    }

    /**
     * Get a query for events of a given type associated with a site and meeting
     * the search criteria.
     * 
     * @param type
     * @param siteToken
     * @param criteria
     * @param database
     * @return
     * @throws SiteWhereException
     */
    public static Query queryEventsOfTypeForSite(DeviceEventType type, String siteToken, ISearchCriteria criteria,
	    String database) throws SiteWhereException {
	return new Query("SELECT * FROM " + COLLECTION_EVENTS + " where type='" + type.name() + "' and " + EVENT_SITE
		+ "='" + siteToken + "'" + buildDateRangeCriteria(criteria) + " GROUP BY " + EVENT_SITE
		+ " ORDER BY time DESC" + buildPagingCriteria(criteria), database);
    }

    /**
     * Get a query for counting events of a given type associated with a site
     * and meeting the search criteria.
     * 
     * @param type
     * @param siteToken
     * @param criteria
     * @param database
     * @return
     * @throws SiteWhereException
     */
    public static Query queryEventsOfTypeForSiteCount(DeviceEventType type, String siteToken, ISearchCriteria criteria,
	    String database) throws SiteWhereException {
	return new Query("SELECT count(" + EVENT_ID + ") FROM " + COLLECTION_EVENTS + " where type='" + type.name()
		+ "' and " + EVENT_SITE + "='" + siteToken + "'" + buildDateRangeCriteria(criteria) + " GROUP BY "
		+ EVENT_SITE, database);
    }

    /**
     * Build search criteria clause.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    protected static String buildPagingCriteria(ISearchCriteria criteria) throws SiteWhereException {
	if (criteria == null) {
	    return "";
	}
	String clause = " ";
	if (criteria.getPageSize() != null) {
	    clause += " LIMIT " + criteria.getPageSize();
	}
	if (criteria.getPageNumber() != null) {
	    clause += " OFFSET " + ((criteria.getPageNumber() - 1) * criteria.getPageSize());
	}
	return clause;
    }

    /**
     * Build search criteria clause that handles date ranges specified for event
     * queries.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    protected static String buildDateRangeCriteria(ISearchCriteria criteria) throws SiteWhereException {
	String dateClause = "";
	if (criteria instanceof IDateRangeSearchCriteria) {
	    IDateRangeSearchCriteria dates = (IDateRangeSearchCriteria) criteria;
	    if (dates.getStartDate() != null) {
		dateClause += " and time >= '" + ISODateTimeFormat.dateTime().print(dates.getStartDate().getTime())
			+ "'";
	    }
	    if (dates.getEndDate() != null) {
		dateClause += " and time <= '" + ISODateTimeFormat.dateTime().print(dates.getEndDate().getTime()) + "'";
	    }
	}
	return dateClause;
    }

    /**
     * Parse events of the given type from the query result.
     * 
     * @param response
     * @param clazz
     * @return
     * @throws SiteWhereException
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> eventsOfType(QueryResult response, Class<T> clazz) throws SiteWhereException {
	List<T> results = new ArrayList<T>();
	List<IDeviceEvent> events = parse(response);
	for (IDeviceEvent event : events) {
	    if (clazz.isAssignableFrom(event.getClass())) {
		results.add((T) event);
	    }
	}
	return results;
    }

    /**
     * Parse results from a query.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static List<IDeviceEvent> parse(QueryResult response) throws SiteWhereException {
	handleError(response);

	List<IDeviceEvent> results = new ArrayList<IDeviceEvent>();
	for (Result result : response.getResults()) {
	    if (result.getSeries() != null) {
		for (Series series : result.getSeries()) {
		    for (List<Object> values : series.getValues()) {
			Map<String, Object> valueMap = getValueMap(series.getColumns(), values);
			String eventType = (String) valueMap.get(EVENT_TYPE);
			DeviceEventType type = DeviceEventType.valueOf(eventType);
			if (type == null) {
			    throw new SiteWhereException("Unknown event type: " + type);
			}
			switch (type) {
			case Location: {
			    results.add(InfluxDbDeviceLocation.parse(valueMap));
			    break;
			}
			case Measurements: {
			    results.add(InfluxDbDeviceMeasurements.parse(valueMap));
			    break;
			}
			case Alert: {
			    results.add(InfluxDbDeviceAlert.parse(valueMap));
			    break;
			}
			case CommandInvocation: {
			    results.add(InfluxDbDeviceCommandInvocation.parse(valueMap));
			    break;
			}
			case CommandResponse: {
			    results.add(InfluxDbDeviceCommandResponse.parse(valueMap));
			    break;
			}
			default: {
			    throw new SiteWhereException("No parser found for type: " + type);
			}
			}
		    }
		}
	    }
	}
	return results;
    }

    /**
     * Parse response from count query.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static long parseCount(QueryResult response) throws SiteWhereException {
	handleError(response);

	for (Result result : response.getResults()) {
	    if (result.getSeries() != null) {
		for (Series series : result.getSeries()) {
		    for (List<Object> values : series.getValues()) {
			Map<String, Object> valueMap = getValueMap(series.getColumns(), values);
			return ((Double) valueMap.get("count")).longValue();
		    }
		}
	    }
	}
	return 0;
    }

    /**
     * Finds String value and throws exception if null.
     * 
     * @param values
     * @param field
     * @return
     * @throws SiteWhereException
     */
    public static String find(Map<String, Object> values, String field) throws SiteWhereException {
	return find(values, field, false);
    }

    /**
     * Finds String value.
     * 
     * @param values
     * @param field
     * @param allowNull
     * @return
     * @throws SiteWhereException
     */
    public static String find(Map<String, Object> values, String field, boolean allowNull) throws SiteWhereException {
	Object value = values.get(field);
	if (value == null) {
	    if (allowNull) {
		return null;
	    }
	    throw new SiteWhereException("Field value missing: " + field);
	}
	if (!(value instanceof String)) {
	    throw new SiteWhereException("Expected String field but found: " + field.getClass().getName());
	}
	return (String) value;
    }

    /**
     * Handle error condition in query.
     * 
     * @param result
     * @throws SiteWhereException
     */
    protected static void handleError(QueryResult result) throws SiteWhereException {
	if (result.getError() != null) {
	    throw new SiteWhereException("Error performing query: " + result.getError());
	}
    }

    /**
     * Create a map of values that are present.
     * 
     * @param columns
     * @param values
     * @return
     */
    protected static Map<String, Object> getValueMap(List<String> columns, List<Object> values) {
	Map<String, Object> map = new HashMap<String, Object>();
	for (int i = 0; i < columns.size(); i++) {
	    String key = columns.get(i);
	    Object value = values.get(i);
	    if (value != null) {
		map.put(key, value);
	    }
	}
	return map;
    }

    /**
     * Load common event fields from value map.
     * 
     * @param event
     * @param values
     * @throws SiteWhereException
     */
    public static void loadFromMap(DeviceEvent event, Map<String, Object> values) throws SiteWhereException {
	event.setId((String) values.get(EVENT_ID));
	event.setDeviceAssignmentToken((String) values.get(EVENT_ASSIGNMENT));
	event.setSiteToken(((String) values.get(EVENT_SITE)));
	event.setAssetModuleId(((String) values.get(EVENT_ASSET_MODULE)));
	event.setAssetId(((String) values.get(EVENT_ASSET)));
	event.setAssignmentType(DeviceAssignmentType.Associated);
	event.setReceivedDate(parseDateField(values, RECEIVED_DATE));
	event.setEventDate(parseDateField(values, "time"));

	// Load metadata values.
	for (String key : values.keySet()) {
	    if (key.startsWith(EVENT_METADATA_PREFIX)) {
		String name = key.substring(EVENT_METADATA_PREFIX.length());
		String value = (String) values.get(key);
		event.addOrReplaceMetadata(name, value);
	    }
	}
    }

    /**
     * Save common event fields to builder.
     * 
     * @param event
     * @param builder
     * @throws SiteWhereException
     */
    public static void saveToBuilder(DeviceEvent event, Point.Builder builder) throws SiteWhereException {
	builder.time(event.getEventDate().getTime(), TimeUnit.MILLISECONDS);
	builder.field(EVENT_ID, event.getId());
	builder.tag(EVENT_TYPE, event.getEventType().name());
	builder.tag(EVENT_ASSIGNMENT, event.getDeviceAssignmentToken());
	builder.tag(EVENT_SITE, event.getSiteToken());
	if (event.getAssetModuleId() != null) {
	    builder.tag(EVENT_ASSET_MODULE, event.getAssetModuleId());
	}
	if (event.getAssetId() != null) {
	    builder.tag(EVENT_ASSET, event.getAssetId());
	}
	builder.field(RECEIVED_DATE, ISODateTimeFormat.dateTime().print(event.getReceivedDate().getTime()));

	// Add field for each metadata value.
	for (String key : event.getMetadata().keySet()) {
	    builder.field(EVENT_METADATA_PREFIX + key, event.getMetadata(key));
	}
    }

    /**
     * Parse a date field.
     * 
     * @param values
     * @param tag
     * @return
     */
    protected static Date parseDateField(Map<String, Object> values, String tag) {
	Object value = (Object) values.get(tag);
	if (value instanceof String) {
	    return ISODateTimeFormat.dateTime().parseDateTime((String) value).toDate();
	} else if (value instanceof Double) {
	    return new Date(((Double) value).longValue());
	}
	return null;
    }
}