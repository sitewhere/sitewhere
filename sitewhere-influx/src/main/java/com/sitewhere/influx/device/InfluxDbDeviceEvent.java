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

import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.joda.time.format.ISODateTimeFormat;

import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Common base class for saving device event data to InfluxDB.
 * 
 * @author Derek
 */
public class InfluxDbDeviceEvent {

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

	/** Event date field */
	public static final String EVENT_DATE = "eventdate";

	/** Event metadata field */
	public static final String EVENT_METADATA_PREFIX = "meta:";

	/**
	 * Return a builder for the events collection.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public static Point.Builder createBuilder() throws SiteWhereException {
		return Point.measurement(COLLECTION_EVENTS).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
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
		event.setEventDate(parseDateField(values, EVENT_DATE));
		event.setReceivedDate(parseDateField(values, "time"));
	}

	/**
	 * Save common event fields to builder.
	 * 
	 * @param event
	 * @param builder
	 * @throws SiteWhereException
	 */
	public static void saveToBuilder(DeviceEvent event, Point.Builder builder) throws SiteWhereException {
		builder.tag(EVENT_ID, event.getId());
		builder.tag(EVENT_TYPE, event.getEventType().name());
		builder.tag(EVENT_ASSIGNMENT, event.getDeviceAssignmentToken());
		builder.tag(EVENT_SITE, event.getSiteToken());
		builder.tag(EVENT_ASSET_MODULE, event.getAssetModuleId());
		builder.tag(EVENT_ASSET, event.getAssetId());
		builder.field(EVENT_DATE, ISODateTimeFormat.dateTime().print(event.getEventDate().getTime()));

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
		String value = (String) values.get(tag);
		if (value != null) {
			return ISODateTimeFormat.dateTime().parseDateTime(value).toDate();
		}
		return null;
	}
}