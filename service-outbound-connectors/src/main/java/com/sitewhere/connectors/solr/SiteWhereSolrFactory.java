/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.solr;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;

/**
 * Factory that creates indexable Solr objects from SiteWhere objects.
 * 
 * @author Derek
 */
public class SiteWhereSolrFactory {

    /**
     * Create a {@link SolrInputDocument} based on information in
     * {@link IDeviceMeasurement}.
     * 
     * @param mx
     * @return
     * @throws SiteWhereException
     */
    public static SolrInputDocument createDocumentFromMeasurement(IDeviceMeasurement mx) throws SiteWhereException {
	SolrInputDocument document = new SolrInputDocument();
	document.addField(ISolrFields.EVENT_TYPE, SolrEventType.Measurements.name());
	addFieldsForEvent(document, mx);
	document.addField(ISolrFields.MX_NAME, mx.getName());
	document.addField(ISolrFields.MX_VALUE, mx.getValue());
	return document;
    }

    /**
     * Create a {@link SolrInputDocument} based on information in
     * {@link IDeviceLocation}.
     * 
     * @param location
     * @return
     * @throws SiteWhereException
     */
    public static SolrInputDocument createDocumentFromLocation(IDeviceLocation location) throws SiteWhereException {
	SolrInputDocument document = new SolrInputDocument();
	document.addField(ISolrFields.EVENT_TYPE, SolrEventType.Location.name());
	addFieldsForEvent(document, location);
	String latLong = "" + location.getLatitude() + ", " + location.getLongitude();
	document.addField(ISolrFields.LOCATION, latLong);
	document.addField(ISolrFields.ELEVATION, location.getElevation());
	return document;
    }

    /**
     * Create a {@link SolrInputDocument} based on information in
     * {@link IDeviceAlert}.
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
     * Parses a {@link SolrDocument} into a {@link IDeviceEvent}.
     * 
     * @param document
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceEvent parseDocument(SolrDocument document) throws SiteWhereException {
	String type = (String) document.getFieldValue(ISolrFields.EVENT_TYPE);
	if (type == null) {
	    throw new SiteWhereException("Solr event does not contain an event type indicator.");
	}
	SolrEventType eventType = SolrEventType.valueOf(type);
	switch (eventType) {
	case Location: {
	    return parseLocationFromDocument(document);
	}
	case Measurements: {
	    return parseMeasurementFromDocument(document);
	}
	case Alert: {
	    return parseAlertFromDocument(document);
	}
	default: {
	    throw new SiteWhereException("Solr docuemnt contained unknown device event type.");
	}
	}
    }

    /**
     * Parse an {@link IDeviceLocation} record from a {@link SolrDocument}.
     * 
     * @param document
     * @return
     * @throws SiteWhereException
     */
    @SuppressWarnings("unchecked")
    protected static IDeviceLocation parseLocationFromDocument(SolrDocument document) throws SiteWhereException {
	DeviceLocation location = new DeviceLocation();

	List<String> latLong = (List<String>) document.get(ISolrFields.LOCATION);
	if (latLong == null) {
	    throw new SiteWhereException("Invalid location document. No location data stored.");
	}
	String[] split = latLong.get(0).split("[,]");
	location.setLatitude(Double.parseDouble(split[0].trim()));
	location.setLongitude(Double.parseDouble(split[1].trim()));

	Double elevation = (Double) document.get(ISolrFields.ELEVATION);
	if (elevation != null) {
	    location.setElevation(elevation);
	}

	addFieldsFromEventDocument(document, location);
	return location;
    }

    /**
     * Parse an {@link IDeviceMeasurement} record from a {@link SolrDocument}.
     * 
     * @param document
     * @return
     * @throws SiteWhereException
     */
    protected static IDeviceMeasurement parseMeasurementFromDocument(SolrDocument document) throws SiteWhereException {
	DeviceMeasurement mx = new DeviceMeasurement();

	String name = (String) document.get(ISolrFields.MX_NAME);
	Double value = (Double) document.get(ISolrFields.MX_VALUE);

	mx.setName(name);
	mx.setValue(value);

	addFieldsFromEventDocument(document, mx);
	return mx;
    }

    /**
     * Parse and {@link IDeviceAlert} record from a {@link SolrDocument}.
     * 
     * @param document
     * @return
     * @throws SiteWhereException
     */
    protected static IDeviceAlert parseAlertFromDocument(SolrDocument document) throws SiteWhereException {
	DeviceAlert alert = new DeviceAlert();

	String alertType = (String) document.get(ISolrFields.ALERT_TYPE);
	String alertMessage = (String) document.get(ISolrFields.ALERT_MESSAGE);
	String alertLevelStr = (String) document.get(ISolrFields.ALERT_LEVEL);
	String alertSourceStr = (String) document.get(ISolrFields.ALERT_SOURCE);

	alert.setType(alertType);
	alert.setMessage(alertMessage);
	alert.setLevel(AlertLevel.valueOf(alertLevelStr));
	alert.setSource(AlertSource.valueOf(alertSourceStr));

	addFieldsFromEventDocument(document, alert);
	return alert;
    }

    /**
     * Add fields common to all device events.
     * 
     * @param document
     * @param event
     * @throws SiteWhereException
     */
    protected static void addFieldsFromEventDocument(SolrDocument document, DeviceEvent event)
	    throws SiteWhereException {
	UUID id = (UUID) document.get(ISolrFields.EVENT_ID);
	UUID deviceId = (UUID) document.get(ISolrFields.DEVICE_ID);
	UUID assignmentId = (UUID) document.get(ISolrFields.ASSIGNMENT_ID);
	UUID areaId = (UUID) document.get(ISolrFields.AREA_ID);
	UUID assetId = (UUID) document.get(ISolrFields.ASSET_ID);
	Date eventDate = (Date) document.get(ISolrFields.EVENT_DATE);
	Date receivedDate = (Date) document.get(ISolrFields.RECEIVED_DATE);

	event.setId(id);
	event.setDeviceId(deviceId);
	event.setDeviceAssignmentId(assignmentId);
	event.setAreaId(areaId);
	event.setAssetId(assetId);
	event.setEventDate(eventDate);
	event.setReceivedDate(receivedDate);

	Iterator<String> names = document.getFieldNames().iterator();
	int metaLength = ISolrFields.META_PREFIX.length();
	while (names.hasNext()) {
	    String name = names.next();
	    if (name.startsWith(ISolrFields.META_PREFIX)) {
		String metaName = name.substring(metaLength);
		String metaValue = (String) document.get(name);
		event.addOrReplaceMetadata(metaName, metaValue);
	    }
	}
    }

    /**
     * Adds common fields from base SiteWhere {@link IDeviceEvent} object.
     * 
     * @param document
     * @param event
     * @throws SiteWhereException
     */
    protected static void addFieldsForEvent(SolrInputDocument document, IDeviceEvent event) throws SiteWhereException {
	document.addField(ISolrFields.EVENT_ID, event.getId());
	document.addField(ISolrFields.DEVICE_ID, event.getDeviceId());
	document.addField(ISolrFields.ASSIGNMENT_ID, event.getDeviceAssignmentId());
	document.addField(ISolrFields.AREA_ID, event.getAreaId());
	document.addField(ISolrFields.ASSET_ID, event.getAssetId());
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