/*
 * SiteWhereSolrFactory.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.solr;

import java.util.Map;

import org.apache.solr.common.SolrInputDocument;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAlert;
import com.sitewhere.spi.device.IDeviceEvent;
import com.sitewhere.spi.device.IDeviceLocation;
import com.sitewhere.spi.device.IDeviceMeasurements;

/**
 * Factory that creates indexable Solr objects from SiteWhere objects.
 * 
 * @author Derek
 */
public class SiteWhereSolrFactory {

	/**
	 * Create a {@link SolrInputDocument} based on information in
	 * {@link IDeviceMeasurements}.
	 * 
	 * @param measurements
	 * @return
	 * @throws SiteWhereException
	 */
	public static SolrInputDocument createDocumentFromMeasurements(IDeviceMeasurements measurements)
			throws SiteWhereException {
		SolrInputDocument document = new SolrInputDocument();
		document.addField(ISolrFields.EVENT_TYPE, SolrEventType.Measurements.name());
		addFieldsForEvent(document, measurements);
		for (String key : measurements.getMeasurements().keySet()) {
			document.addField(ISolrFields.MEASUREMENT_PREFIX + key, measurements.getMeasurement(key));
		}
		return document;
	}

	/**
	 * Create a {@link SolrInputDocument} based on information in {@link IDeviceLocation}.
	 * 
	 * @param location
	 * @return
	 * @throws SiteWhereException
	 */
	public static SolrInputDocument createDocumentFromLocation(IDeviceLocation location)
			throws SiteWhereException {
		SolrInputDocument document = new SolrInputDocument();
		document.addField(ISolrFields.EVENT_TYPE, SolrEventType.Location.name());
		addFieldsForEvent(document, location);
		String latLong = "" + location.getLatitude() + ", " + location.getLongitude();
		document.addField(ISolrFields.LOCATION, latLong);
		document.addField(ISolrFields.ELEVATION, location.getElevation());
		return document;
	}

	/**
	 * Create a {@link SolrInputDocument} based on information in {@link IDeviceAlert}.
	 * 
	 * @param alert
	 * @return
	 * @throws SiteWhereException
	 */
	public static SolrInputDocument createDocumentFromAlert(IDeviceAlert alert) throws SiteWhereException {
		SolrInputDocument document = new SolrInputDocument();
		document.addField(ISolrFields.EVENT_TYPE, SolrEventType.Alert.name());
		addFieldsForEvent(document, alert);
		document.addField(ISolrFields.ALERT_TYPE, alert.getType());
		document.addField(ISolrFields.ALERT_MESSAGE, alert.getMessage());
		document.addField(ISolrFields.ALERT_LEVEL, alert.getLevel().name());
		document.addField(ISolrFields.ALERT_SOURCE, alert.getSource().name());
		return document;
	}

	/**
	 * Adds common fields from base SiteWhere {@link IDeviceEvent} object.
	 * 
	 * @param document
	 * @param event
	 * @throws SiteWhereException
	 */
	protected static void addFieldsForEvent(SolrInputDocument document, IDeviceEvent event)
			throws SiteWhereException {
		document.addField(ISolrFields.EVENT_ID, event.getId());
		document.addField(ISolrFields.ASSIGNMENT_TOKEN, event.getDeviceAssignmentToken());
		document.addField(ISolrFields.ASSIGNMENT_TYPE, event.getAssignmentType().name());
		document.addField(ISolrFields.ASSET_ID, event.getAssetId());
		document.addField(ISolrFields.SITE_TOKEN, event.getSiteToken());
		document.addField(ISolrFields.EVENT_DATE, event.getEventDate());
		document.addField(ISolrFields.RECEIVED_DATE, event.getReceivedDate());
		addMetadata(document, event.getMetadata());
	}

	/**
	 * Add metadata to the document.
	 * 
	 * @param document
	 * @param metadata
	 * @throws SiteWhereException
	 */
	protected static void addMetadata(SolrInputDocument document, Map<String, String> metadata)
			throws SiteWhereException {
		for (String key : metadata.keySet()) {
			document.addField(ISolrFields.META_PREFIX + key, metadata.get(key));
		}
	}
}