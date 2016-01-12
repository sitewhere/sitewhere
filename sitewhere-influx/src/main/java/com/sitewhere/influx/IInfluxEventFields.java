/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.influx;

/**
 * Constants used for fields in the InfluxDB event management implementation.
 * 
 * @author Derek
 */
public interface IInfluxEventFields {

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

	/** Measurement name field */
	public static final String MEASUREMENT_NAME = "mxname";

	/** Measurement value field */
	public static final String MEASUREMENT_VALUE = "value";

	/** Location latitude field */
	public static final String LOCATION_LATITUDE = "latitude";

	/** Location longitude field */
	public static final String LOCATION_LONGITUDE = "longitude";

	/** Location elevation field */
	public static final String LOCATION_ELEVATION = "elevation";

	/** Alert type tag */
	public static final String ALERT_TYPE = "alert";

	/** Alert source tag */
	public static final String ALERT_SOURCE = "source";

	/** Alert level tag */
	public static final String ALERT_LEVEL = "level";

	/** Alert message field */
	public static final String ALERT_MESSAGE = "message";
}