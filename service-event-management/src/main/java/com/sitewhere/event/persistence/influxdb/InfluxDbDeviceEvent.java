/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.event.persistence.influxdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.joda.time.format.ISODateTimeFormat;

import com.sitewhere.influxdb.InfluxDbClient;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventIndex;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * Common base class for saving device event data to InfluxDB.
 */
public class InfluxDbDeviceEvent {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(InfluxDbDeviceEvent.class);

    /** Collection for events */
    public static final String COLLECTION_EVENTS = "events";

    /** Event id tag */
    public static final String EVENT_ID = "eid";

    /** Alternate event id tag */
    public static final String ALTERNATE_ID = "altid";

    /** Event type tag */
    public static final String EVENT_TYPE = "type";

    /** Event device tag */
    public static final String EVENT_DEVICE = "device";

    /** Event assignment tag */
    public static final String EVENT_ASSIGNMENT = "assignment";

    /** Event customer tag */
    public static final String EVENT_CUSTOMER = "customer";

    /** Event area tag */
    public static final String EVENT_AREA = "area";

    /** Event asset tag */
    public static final String EVENT_ASSET = "asset";

    /** Event received date field */
    public static final String RECEIVED_DATE = "rcvdate";

    /** Event metadata field */
    public static final String EVENT_METADATA_PREFIX = "meta:";

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
    public static IDeviceEvent getEventById(UUID eventId, InfluxDbClient client) throws SiteWhereException {
	Query query = new Query(
		"SELECT * FROM " + InfluxDbDeviceEvent.COLLECTION_EVENTS + " where " + EVENT_ID + "='" + eventId + "'",
		client.getConfiguration().getDatabase());
	QueryResult response = client.getInflux().query(query, TimeUnit.MILLISECONDS);
	List<IDeviceEvent> results = InfluxDbDeviceEvent.eventsOfType(response, IDeviceEvent.class);
	if (results.size() > 0) {
	    return results.get(0);
	}
	return null;
    }

    /**
     * Get an event by alternate id.
     * 
     * @param alternateId
     * @param client
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceEvent getEventByAlternateId(String alternateId, InfluxDbClient client)
	    throws SiteWhereException {
	Query query = new Query("SELECT * FROM " + InfluxDbDeviceEvent.COLLECTION_EVENTS + " where " + ALTERNATE_ID
		+ "='" + alternateId + "'", client.getConfiguration().getDatabase());
	QueryResult response = client.getInflux().query(query, TimeUnit.MILLISECONDS);
	List<IDeviceEvent> results = InfluxDbDeviceEvent.eventsOfType(response, IDeviceEvent.class);
	if (results.size() > 0) {
	    return results.get(0);
	}
	return null;
    }

    /**
     * Search for of events of a given type associated with one or more entities for
     * a given index.
     * 
     * @param index
     * @param entityIds
     * @param type
     * @param criteria
     * @param client
     * @param clazz
     * @return
     * @throws SiteWhereException
     */
    public static <T> SearchResults<T> searchByIndex(DeviceEventIndex index, List<UUID> entityIds, DeviceEventType type,
	    ISearchCriteria criteria, InfluxDbClient client, Class<T> clazz) throws SiteWhereException {
	Query query = InfluxDbDeviceEvent.queryEventsOfTypeForIndex(index, type, entityIds, criteria,
		client.getConfiguration().getDatabase());
	LOGGER.debug("Query: " + query.getCommand());
	QueryResult response = client.getInflux().query(query, TimeUnit.MILLISECONDS);
	List<T> results = InfluxDbDeviceEvent.eventsOfType(response, clazz);

	Query countQuery = InfluxDbDeviceEvent.queryEventsOfTypeForIndexCount(index, type, entityIds, criteria,
		client.getConfiguration().getDatabase());
	LOGGER.debug("Count: " + countQuery.getCommand());
	QueryResult countResponse = client.getInflux().query(countQuery);
	long count = parseCount(countResponse);
	return new SearchResults<T>(results, count);
    }

    /**
     * Get a query for events of a given type associated with one or more entities
     * for a given index and that meet the search criteria.
     * 
     * @param index
     * @param type
     * @param entityIds
     * @param criteria
     * @param database
     * @return
     * @throws SiteWhereException
     */
    protected static Query queryEventsOfTypeForIndex(DeviceEventIndex index, DeviceEventType type, List<UUID> entityIds,
	    ISearchCriteria criteria, String database) throws SiteWhereException {
	return new Query("SELECT * FROM " + InfluxDbDeviceEvent.COLLECTION_EVENTS + " where type='" + type.name()
		+ "' and " + buildInClause(index, entityIds) + buildDateRangeCriteria(criteria) + " ORDER BY time DESC"
		+ buildPagingCriteria(criteria), database);
    }

    /**
     * Get a query for counting events of a given type associated with one or more
     * entities for a given index and that meet the search criteria.
     * 
     * @param index
     * @param type
     * @param entityIds
     * @param criteria
     * @param database
     * @return
     * @throws SiteWhereException
     */
    protected static Query queryEventsOfTypeForIndexCount(DeviceEventIndex index, DeviceEventType type,
	    List<UUID> entityIds, ISearchCriteria criteria, String database) throws SiteWhereException {
	return new Query(
		"SELECT count(" + EVENT_ID + ") FROM " + InfluxDbDeviceEvent.COLLECTION_EVENTS + " where type='"
			+ type.name() + "' and " + buildInClause(index, entityIds) + buildDateRangeCriteria(criteria),
		database);
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
			    case Measurement: {
				results.add(InfluxDbDeviceMeasurement.parse(valueMap));
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
	if (series.getTags() != null) {
	    map.putAll(series.getTags());
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
    protected static void loadFromMap(DeviceEvent event, Map<String, Object> values) throws SiteWhereException {
	event.setId(convertUUID((String) values.get(EVENT_ID)));
	event.setAlternateId((String) values.get(ALTERNATE_ID));
	event.setDeviceId(convertUUID((String) values.get(EVENT_DEVICE)));
	event.setDeviceAssignmentId(convertUUID((String) values.get(EVENT_ASSIGNMENT)));
	event.setCustomerId(convertUUID((String) values.get(EVENT_CUSTOMER)));
	event.setAreaId(convertUUID((String) values.get(EVENT_AREA)));
	event.setAssetId(convertUUID((String) values.get(EVENT_ASSET)));
	event.setReceivedDate(parseDateField(values, RECEIVED_DATE));
	event.setEventDate(parseDateField(values, "time"));

	// Load metadata values.
	for (String key : values.keySet()) {
	    if (key.startsWith(EVENT_METADATA_PREFIX)) {
		String name = key.substring(EVENT_METADATA_PREFIX.length());
		String value = (String) values.get(key);
		event.getMetadata().put(name, value);
	    }
	}
    }

    public static UUID convertUUID(String value) throws SiteWhereException {
	if (value == null) {
	    return null;
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
	String timePrecision = event.getMetadata().get(EVENT_TIME_PRECISION_META_DATA_KEY);
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
		event.getMetadata().put(EVENT_TIME_PRECISION_META_DATA_KEY, "ms");
	    }
	    }
	} else {
	    event.getMetadata().put(EVENT_TIME_PRECISION_META_DATA_KEY, "ms");
	}

	builder.time(event.getEventDate().getTime(), precision);
	builder.addField(EVENT_ID, event.getId().toString());
	if (event.getAlternateId() != null) {
	    builder.addField(ALTERNATE_ID, event.getAlternateId());
	}
	builder.tag(EVENT_TYPE, event.getEventType().name());
	builder.tag(EVENT_DEVICE, event.getDeviceId().toString());
	builder.tag(EVENT_ASSIGNMENT, event.getDeviceAssignmentId().toString());
	if (event.getCustomerId() != null) {
	    builder.tag(EVENT_CUSTOMER, event.getCustomerId().toString());
	}
	if (event.getAreaId() != null) {
	    builder.tag(EVENT_AREA, event.getAreaId().toString());
	}
	if (event.getAssetId() != null) {
	    builder.tag(EVENT_ASSET, event.getAssetId().toString());
	}
	builder.addField(RECEIVED_DATE, ISODateTimeFormat.dateTime().print(event.getReceivedDate().getTime()));

	// Add field for each metadata value.
	for (String key : event.getMetadata().keySet()) {
	    builder.addField(EVENT_METADATA_PREFIX + key, event.getMetadata().get(key));
	}
    }

    /**
     * Get field name associated with index.
     * 
     * @param index
     * @return
     * @throws SiteWhereException
     */
    protected static String getFieldForIndex(DeviceEventIndex index) throws SiteWhereException {
	switch (index) {
	case Area: {
	    return EVENT_AREA;
	}
	case Asset: {
	    return EVENT_ASSET;
	}
	case Assignment: {
	    return EVENT_ASSIGNMENT;
	}
	case Customer: {
	    return EVENT_CUSTOMER;
	}
	}
	throw new SiteWhereException("Unknown index: " + index.name());
    }

    /**
     * Get "in" clause for matching on a list of entity ids for an index.
     * 
     * @param index
     * @param entityIds
     * @return
     * @throws SiteWhereException
     */
    protected static String buildInClause(DeviceEventIndex index, List<UUID> entityIds) throws SiteWhereException {
	String field = getFieldForIndex(index);
	List<String> clauses = new ArrayList<>();
	for (UUID entityId : entityIds) {
	    clauses.add(field + "='" + entityId.toString() + "'");
	}
	return "(" + String.join(" or ", clauses) + ")";
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