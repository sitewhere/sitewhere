/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.influxdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.joda.time.format.ISODateTimeFormat;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.influxdb.InfluxDbClient;
import com.sitewhere.rest.model.asset.DefaultAssetReferenceEncoder;
import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.IDeviceAssignment;
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

    /** Event area tag */
    public static final String EVENT_AREA = "area";

    /** Event asset reference tag */
    public static final String EVENT_ASSET_REFERENCE = "assetReference";

    /** Event received date field */
    public static final String RECEIVED_DATE = "rcvdate";

    /** Event metadata field */
    public static final String EVENT_METADATA_PREFIX = "meta:";

    /** Event assignmentType field */
    public static final String ASSIGNMENT_TYPE = "assignmenttype";

    /** The meta data field to check if user has specified a time precision */
    private static final String EVENT_TIME_PRECISION_META_DATA_KEY = "precision";

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
     * @param client
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceEvent getEventById(String eventId, InfluxDbClient client) throws SiteWhereException {
	Query query = new Query(
		"SELECT * FROM " + InfluxDbDeviceEvent.COLLECTION_EVENTS + " where eid='" + eventId + "'",
		client.getDatabase().getValue());
	QueryResult response = client.getInflux().query(query, TimeUnit.MILLISECONDS);
	List<IDeviceEvent> results = InfluxDbDeviceEvent.eventsOfType(response, IDeviceEvent.class);
	if (results.size() > 0) {
	    return results.get(0);
	}
	return null;
    }

    /**
     * Search for of events of a given type associated with an assignment.
     * 
     * @param assignment
     * @param type
     * @param criteria
     * @param client
     * @param clazz
     * @return
     * @throws SiteWhereException
     */
    public static <T> SearchResults<T> searchByAssignment(IDeviceAssignment assignment, DeviceEventType type,
	    ISearchCriteria criteria, InfluxDbClient client, Class<T> clazz) throws SiteWhereException {
	Query query = InfluxDbDeviceEvent.queryEventsOfTypeForAssignment(type, assignment, criteria,
		client.getDatabase().getValue());
	LOGGER.debug("Query: " + query.getCommand());
	QueryResult response = client.getInflux().query(query, TimeUnit.MILLISECONDS);
	List<T> results = InfluxDbDeviceEvent.eventsOfType(response, clazz);

	Query countQuery = InfluxDbDeviceEvent.queryEventsOfTypeForAssignmentCount(type, assignment, criteria,
		client.getDatabase().getValue());
	LOGGER.debug("Count: " + countQuery.getCommand());
	QueryResult countResponse = client.getInflux().query(countQuery);
	long count = parseCount(countResponse);
	return new SearchResults<T>(results, count);
    }

    /**
     * Search for of events of a given type associated with an area.
     * 
     * @param area
     * @param type
     * @param criteria
     * @param client
     * @param clazz
     * @return
     * @throws SiteWhereException
     */
    public static <T> SearchResults<T> searchByArea(IArea area, DeviceEventType type, ISearchCriteria criteria,
	    InfluxDbClient client, Class<T> clazz) throws SiteWhereException {
	Query query = InfluxDbDeviceEvent.queryEventsOfTypeForArea(type, area, criteria,
		client.getDatabase().getValue());
	LOGGER.debug("Query: " + query.getCommand());
	QueryResult response = client.getInflux().query(query, TimeUnit.MILLISECONDS);
	List<T> results = InfluxDbDeviceEvent.eventsOfType(response, clazz);

	Query countQuery = InfluxDbDeviceEvent.queryEventsOfTypeForAreaCount(type, area, criteria,
		client.getDatabase().getValue());
	LOGGER.debug("Count: " + countQuery.getCommand());
	QueryResult countResponse = client.getInflux().query(countQuery);
	long count = parseCount(countResponse);
	return new SearchResults<T>(results, count);
    }

    /**
     * Get a query for events of a given type associated with an assignment and that
     * meet the search criteria.
     * 
     * @param type
     * @param assignment
     * @param criteria
     * @param database
     * @return
     * @throws SiteWhereException
     */
    protected static Query queryEventsOfTypeForAssignment(DeviceEventType type, IDeviceAssignment assignment,
	    ISearchCriteria criteria, String database) throws SiteWhereException {
	return new Query("SELECT * FROM " + InfluxDbDeviceEvent.COLLECTION_EVENTS + " where type='" + type.name()
		+ "' and " + InfluxDbDeviceEvent.EVENT_ASSIGNMENT + "='" + assignment.getId() + "'"
		+ buildDateRangeCriteria(criteria) + " GROUP BY " + EVENT_ASSIGNMENT + " ORDER BY time DESC"
		+ buildPagingCriteria(criteria), database);
    }

    /**
     * Get a query for counting events of a given type associated with an assignment
     * and that meet the search criteria.
     * 
     * @param type
     * @param assignment
     * @param criteria
     * @param database
     * @return
     * @throws SiteWhereException
     */
    protected static Query queryEventsOfTypeForAssignmentCount(DeviceEventType type, IDeviceAssignment assignment,
	    ISearchCriteria criteria, String database) throws SiteWhereException {
	return new Query("SELECT count(" + EVENT_ID + ") FROM " + InfluxDbDeviceEvent.COLLECTION_EVENTS
		+ " where type='" + type.name() + "' and " + InfluxDbDeviceEvent.EVENT_ASSIGNMENT + "='"
		+ assignment.getId() + "'" + buildDateRangeCriteria(criteria) + " GROUP BY " + EVENT_ASSIGNMENT,
		database);
    }

    /**
     * Get a query for events of a given type associated with an area and meeting
     * the search criteria.
     * 
     * @param type
     * @param area
     * @param criteria
     * @param database
     * @return
     * @throws SiteWhereException
     */
    protected static Query queryEventsOfTypeForArea(DeviceEventType type, IArea area, ISearchCriteria criteria,
	    String database) throws SiteWhereException {
	return new Query("SELECT * FROM " + COLLECTION_EVENTS + " where type='" + type.name() + "' and " + EVENT_AREA
		+ "='" + area.getId() + "'" + buildDateRangeCriteria(criteria) + " GROUP BY " + EVENT_AREA
		+ " ORDER BY time DESC" + buildPagingCriteria(criteria), database);
    }

    /**
     * Get a query for counting events of a given type associated with an area and
     * meeting the search criteria.
     * 
     * @param type
     * @param area
     * @param criteria
     * @param database
     * @return
     * @throws SiteWhereException
     */
    protected static Query queryEventsOfTypeForAreaCount(DeviceEventType type, IArea area, ISearchCriteria criteria,
	    String database) throws SiteWhereException {
	return new Query("SELECT count(" + EVENT_ID + ") FROM " + COLLECTION_EVENTS + " where type='" + type.name()
		+ "' and " + EVENT_AREA + "='" + area.getId() + "'" + buildDateRangeCriteria(criteria) + " GROUP BY "
		+ EVENT_AREA, database);
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
    protected static <T> List<T> eventsOfType(QueryResult response, Class<T> clazz) throws SiteWhereException {
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
    protected static List<IDeviceEvent> parse(QueryResult response) throws SiteWhereException {
	handleError(response);

	List<IDeviceEvent> results = new ArrayList<IDeviceEvent>();
	for (Result result : response.getResults()) {
	    if (result.getSeries() != null) {
		for (Series series : result.getSeries()) {
		    for (List<Object> values : series.getValues()) {
			Map<String, Object> valueMap = getValueMap(series, values);
			String eventType = (String) valueMap.get(EVENT_TYPE);
			DeviceEventType type = DeviceEventType.valueOf(eventType);
			if (type == null) {
			    throw new SiteWhereException("Unknown event type: " + type);
			}
			try {
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
			    case StateChange: {
				results.add(InfluxDbDeviceStateChange.parse(valueMap));
				break;
			    }
			    default: {
				throw new SiteWhereException("No parser found for type: " + type);
			    }
			    }
			} catch (SiteWhereException e) {
			    LOGGER.error("Unable to parse value map. (" + e.getMessage() + ").\n\n"
				    + MarshalUtils.marshalJsonAsPrettyString(valueMap));
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
    protected static long parseCount(QueryResult response) throws SiteWhereException {
	handleError(response);

	for (Result result : response.getResults()) {
	    if (result.getSeries() != null) {
		for (Series series : result.getSeries()) {
		    for (List<Object> values : series.getValues()) {
			Map<String, Object> valueMap = getValueMap(series, values);
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
    protected static String find(Map<String, Object> values, String field) throws SiteWhereException {
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
    protected static String find(Map<String, Object> values, String field, boolean allowNull)
	    throws SiteWhereException {
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
    protected static Map<String, Object> getValueMap(Series series, List<Object> values) {
	List<String> columns = series.getColumns();
	Map<String, Object> map = new HashMap<String, Object>();
	for (int i = 0; i < columns.size(); i++) {
	    String key = columns.get(i);
	    Object value = values.get(i);
	    if (value != null) {
		map.put(key, value);
	    }
	}
	map.putAll(series.getTags());
	return map;
    }

    /**
     * Load common event fields from value map.
     * 
     * @param event
     * @param values
     * @throws SiteWhereException
     */
    protected static void loadFromMap(DeviceEvent event, Map<String, Object> values) throws SiteWhereException {
	event.setId((String) values.get(EVENT_ID));
	event.setDeviceAssignmentId(validateUUID((String) values.get(EVENT_ASSIGNMENT)));
	event.setAreaId(validateUUID((String) values.get(EVENT_AREA)));
	event.setAssetReference(
		new DefaultAssetReferenceEncoder().decode(((String) values.get(EVENT_ASSET_REFERENCE))));

	event.setAssignmentType(DeviceAssignmentType.valueOf((String) values.get(ASSIGNMENT_TYPE)));
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

    protected static UUID validateUUID(String value) throws SiteWhereException {
	if (value == null) {
	    throw new SiteWhereException("Invalid UUID in map.");
	}
	return UUID.fromString(value);
    }

    /**
     * Save common event fields to builder. If a precision field is specified in the
     * meta data, the eventDate must be sent as an appropriate time stamp rather
     * than a human readable string.
     * 
     * Valid precisions : s - seconds ms - milliseconds mu - microseconds ns -
     * nanoseconds
     * 
     * Default precision is ms - milliseconds if a precision is not specified.
     * 
     * @param event
     * @param builder
     * @throws SiteWhereException
     */
    protected static void saveToBuilder(DeviceEvent event, Point.Builder builder) throws SiteWhereException {
	String timePrecision = event.getMetadata(EVENT_TIME_PRECISION_META_DATA_KEY);
	TimeUnit precision = TimeUnit.MILLISECONDS;

	if (timePrecision != null) {
	    switch (timePrecision) {
	    case ("s"): {
		precision = TimeUnit.SECONDS;
		break;
	    }
	    case ("ms"): {
		precision = TimeUnit.MILLISECONDS;
		break;
	    }
	    case ("mu"): {
		precision = TimeUnit.MICROSECONDS;
		break;
	    }
	    case ("ns"): {
		precision = TimeUnit.NANOSECONDS;
		break;
	    }
	    default: {
		event.addOrReplaceMetadata(EVENT_TIME_PRECISION_META_DATA_KEY, "ms");
	    }
	    }
	} else {
	    event.addOrReplaceMetadata(EVENT_TIME_PRECISION_META_DATA_KEY, "ms");
	}

	builder.time(event.getEventDate().getTime(), precision);
	builder.addField(EVENT_ID, event.getId());
	builder.tag(EVENT_TYPE, event.getEventType().name());
	builder.tag(EVENT_ASSIGNMENT, event.getDeviceAssignmentId().toString());
	builder.tag(ASSIGNMENT_TYPE, String.valueOf(event.getAssignmentType()));
	builder.tag(EVENT_AREA, event.getAreaId().toString());
	if (event.getAssetReference() != null) {
	    builder.tag(EVENT_ASSET_REFERENCE, new DefaultAssetReferenceEncoder().encode(event.getAssetReference()));
	}
	builder.addField(RECEIVED_DATE, ISODateTimeFormat.dateTime().print(event.getReceivedDate().getTime()));

	// Add field for each metadata value.
	for (String key : event.getMetadata().keySet()) {
	    builder.addField(EVENT_METADATA_PREFIX + key, event.getMetadata(key));
	}
    }

    /**
     * Add a tag to an existing object
     * 
     * @param tagName
     * @param tagValue
     * @param builder
     */
    protected static void addUserDefinedTag(String tagName, String tagValue, Point.Builder builder) {
	builder.tag(tagName, tagValue);
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