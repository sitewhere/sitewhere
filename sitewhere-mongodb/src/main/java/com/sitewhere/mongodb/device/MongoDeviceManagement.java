/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.mongodb.device;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.mongodb.SiteWhereMongoClient;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAlert;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceAssignmentState;
import com.sitewhere.rest.model.device.DeviceEventBatchResponse;
import com.sitewhere.rest.model.device.DeviceLocation;
import com.sitewhere.rest.model.device.DeviceMeasurements;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.common.IMetadataProvider;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAlert;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceEventBatch;
import com.sitewhere.spi.device.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.IDeviceLocation;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceMeasurements;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.device.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.request.ISiteCreateRequest;
import com.sitewhere.spi.device.request.IZoneCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * Device management implementation that uses MongoDB for persistence.
 * 
 * @author dadams
 */
public class MongoDeviceManagement implements IDeviceManagement {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(MongoDeviceManagement.class);

	/** Injected with global SiteWhere Mongo client */
	private SiteWhereMongoClient mongoClient;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#start()
	 */
	public void start() throws SiteWhereException {
		LOGGER.info("Mongo device management started.");

		/** Ensure that collection indexes exist */
		ensureIndexes();
	}

	/**
	 * Ensure that expected collection indexes exist.
	 * 
	 * @throws SiteWhereException
	 */
	protected void ensureIndexes() throws SiteWhereException {
		getMongoClient().getSitesCollection().ensureIndex(new BasicDBObject("token", 1),
				new BasicDBObject("unique", true));
		getMongoClient().getDevicesCollection().ensureIndex(new BasicDBObject("hardwareId", 1),
				new BasicDBObject("unique", true));
		getMongoClient().getDeviceAssignmentsCollection().ensureIndex(new BasicDBObject("token", 1),
				new BasicDBObject("unique", true));
		getMongoClient().getDeviceAssignmentsCollection().ensureIndex(
				new BasicDBObject("lastLocation.latLong", "2d").append("lastLocation.eventDate", -1));
		getMongoClient().getLocationsCollection().ensureIndex(
				new BasicDBObject("latLong", "2d").append("eventDate", -1));
		getMongoClient().getLocationsCollection().ensureIndex(
				new BasicDBObject("deviceAssignmentToken", 1).append("eventDate", -1));
		getMongoClient().getMeasurementsCollection().ensureIndex(
				new BasicDBObject("deviceAssignmentToken", 1).append("eventDate", -1));
		getMongoClient().getAlertsCollection().ensureIndex(
				new BasicDBObject("deviceAssignmentToken", 1).append("eventDate", -1));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#stop()
	 */
	public void stop() throws SiteWhereException {
		LOGGER.info("Mongo device management stopped.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createDevice(com.sitewhere.spi.device
	 * .request. IDeviceCreateRequest)
	 */
	public IDevice createDevice(IDeviceCreateRequest request) throws SiteWhereException {
		IDevice existing = getDeviceByHardwareId(request.getHardwareId());
		if (existing != null) {
			throw new SiteWhereSystemException(ErrorCode.DuplicateHardwareId, ErrorLevel.ERROR,
					HttpServletResponse.SC_CONFLICT);
		}
		Device newDevice = new Device();
		newDevice.setAssetId(request.getAssetId());
		newDevice.setHardwareId(request.getHardwareId());
		newDevice.setComments(request.getComments());

		MetadataProvider.copy(request, newDevice);
		SiteWherePersistence.initializeEntityMetadata(newDevice);

		DBCollection devices = getMongoClient().getDevicesCollection();
		DBObject created = MongoDevice.toDBObject(newDevice);
		MongoPersistence.insert(devices, created);
		return newDevice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#updateDevice(java.lang.String,
	 * com.sitewhere.spi.device.request.IDeviceCreateRequest)
	 */
	public IDevice updateDevice(String hardwareId, IDeviceCreateRequest request) throws SiteWhereException {
		DBObject existing = assertDevice(hardwareId);

		// Can not update the hardware id on a device.
		if ((request.getHardwareId() != null) && (!request.getHardwareId().equals(hardwareId))) {
			throw new SiteWhereSystemException(ErrorCode.DeviceHardwareIdCanNotBeChanged, ErrorLevel.ERROR,
					HttpServletResponse.SC_BAD_REQUEST);
		}

		// Copy any non-null fields.
		Device updatedDevice = MongoDevice.fromDBObject(existing);
		if (request.getAssetId() != null) {
			updatedDevice.setAssetId(request.getAssetId());
		}
		if (request.getComments() != null) {
			updatedDevice.setComments(request.getComments());
		}
		if ((request.getMetadata() != null) && (request.getMetadata().size() > 0)) {
			updatedDevice.getMetadata().clear();
			MetadataProvider.copy(request, updatedDevice);
		}
		SiteWherePersistence.setUpdatedEntityMetadata(updatedDevice);
		DBObject updated = MongoDevice.toDBObject(updatedDevice);

		DBCollection devices = getMongoClient().getDevicesCollection();
		BasicDBObject query = new BasicDBObject(MongoDevice.PROP_HARDWARE_ID, hardwareId);
		MongoPersistence.update(devices, query, updated);
		return MongoDevice.fromDBObject(updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceByHardwareId(java
	 * .lang.String)
	 */
	public IDevice getDeviceByHardwareId(String hardwareId) throws SiteWhereException {
		DBObject dbDevice = getDeviceDBObjectByHardwareId(hardwareId);
		if (dbDevice != null) {
			return MongoDevice.fromDBObject(dbDevice);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getCurrentDeviceAssignment
	 * (com.sitewhere.spi.device .IDevice)
	 */
	public IDeviceAssignment getCurrentDeviceAssignment(IDevice device) throws SiteWhereException {
		if (device.getAssignmentToken() == null) {
			return null;
		}
		DBObject match = assertDeviceAssignment(device.getAssignmentToken());
		return MongoDeviceAssignment.fromDBObject(match);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listDevices(boolean,
	 * com.sitewhere.spi.common.ISearchCriteria)
	 */
	public SearchResults<IDevice> listDevices(boolean includeDeleted, ISearchCriteria criteria)
			throws SiteWhereException {
		DBCollection devices = getMongoClient().getDevicesCollection();
		DBObject dbCriteria = new BasicDBObject();
		if (!includeDeleted) {
			MongoSiteWhereEntity.setDeleted(dbCriteria, false);
		}
		BasicDBObject sort = new BasicDBObject(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
		return MongoPersistence.search(IDevice.class, devices, dbCriteria, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listUnassignedDevices(com.sitewhere.
	 * spi.common.ISearchCriteria)
	 */
	public SearchResults<IDevice> listUnassignedDevices(ISearchCriteria criteria) throws SiteWhereException {
		DBCollection devices = getMongoClient().getDevicesCollection();
		BasicDBObject query = new BasicDBObject(MongoDevice.PROP_ASSIGNMENT_TOKEN, null);
		BasicDBObject sort = new BasicDBObject(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
		return MongoPersistence.search(IDevice.class, devices, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#deleteDevice(java.lang.String,
	 * boolean)
	 */
	public IDevice deleteDevice(String hardwareId, boolean force) throws SiteWhereException {
		DBObject existing = assertDevice(hardwareId);
		Device device = MongoDevice.fromDBObject(existing);
		IDeviceAssignment assignment = getCurrentDeviceAssignment(device);
		if (assignment != null) {
			throw new SiteWhereSystemException(ErrorCode.DeviceCanNotBeDeletedIfAssigned, ErrorLevel.ERROR);
		}
		if (force) {
			DBCollection devices = getMongoClient().getDevicesCollection();
			MongoPersistence.delete(devices, existing);
			return MongoDevice.fromDBObject(existing);
		} else {
			MongoSiteWhereEntity.setDeleted(existing, true);
			BasicDBObject query = new BasicDBObject(MongoDevice.PROP_HARDWARE_ID, hardwareId);
			DBCollection devices = getMongoClient().getDevicesCollection();
			MongoPersistence.update(devices, query, existing);
			return MongoDevice.fromDBObject(existing);
		}
	}

	/**
	 * Get the DBObject containing site information that matches the given token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getDeviceDBObjectByHardwareId(String hardwareId) throws SiteWhereException {
		DBCollection devices = getMongoClient().getDevicesCollection();
		BasicDBObject query = new BasicDBObject(MongoDevice.PROP_HARDWARE_ID, hardwareId);
		DBObject result = devices.findOne(query);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createDeviceAssignment(com.sitewhere
	 * .spi.device.request. IDeviceAssignmentCreateRequest)
	 */
	public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request)
			throws SiteWhereException {
		// Verify foreign references.
		DBObject site = getSiteDBObjectByToken(request.getSiteToken());
		if (site == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
		}
		DBObject device = assertDevice(request.getDeviceHardwareId());
		if (device.get(MongoDevice.PROP_ASSIGNMENT_TOKEN) != null) {
			throw new SiteWhereSystemException(ErrorCode.DeviceAlreadyAssigned, ErrorLevel.ERROR);
		}

		// Use common logic to load assignment from request.
		DeviceAssignment newAssignment =
				SiteWherePersistence.deviceAssignmentCreateLogic(request, request.getSiteToken(),
						UUID.randomUUID().toString());

		DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
		DBObject created = MongoDeviceAssignment.toDBObject(newAssignment);
		MongoPersistence.insert(assignments, created);

		// Update device to point to created assignment.
		DBCollection devices = getMongoClient().getDevicesCollection();
		BasicDBObject query = new BasicDBObject(MongoDevice.PROP_HARDWARE_ID, request.getDeviceHardwareId());
		device.put(MongoDevice.PROP_ASSIGNMENT_TOKEN, newAssignment.getToken());
		MongoPersistence.update(devices, query, device);
		return newAssignment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentByToken
	 * (java.lang.String)
	 */
	public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
		DBObject match = getDeviceAssignmentDBObjectByToken(token);
		if (match != null) {
			return MongoDeviceAssignment.fromDBObject(match);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceAssignment(java.lang.String,
	 * boolean)
	 */
	public IDeviceAssignment deleteDeviceAssignment(String token, boolean force) throws SiteWhereException {
		DBObject existing = assertDeviceAssignment(token);
		if (force) {
			DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
			MongoPersistence.delete(assignments, existing);
			return MongoDeviceAssignment.fromDBObject(existing);
		} else {
			MongoSiteWhereEntity.setDeleted(existing, true);
			BasicDBObject query = new BasicDBObject(MongoDeviceAssignment.PROP_TOKEN, token);
			DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
			MongoPersistence.update(assignments, query, existing);
			return MongoDeviceAssignment.fromDBObject(existing);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceForAssignment(com
	 * .sitewhere.spi.device .IDeviceAssignment)
	 */
	public IDevice getDeviceForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
		DBObject device = getDeviceDBObjectByHardwareId(assignment.getDeviceHardwareId());
		if (device != null) {
			return MongoDevice.fromDBObject(device);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getSiteForAssignment(com.sitewhere
	 * .spi.device. IDeviceAssignment)
	 */
	public ISite getSiteForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
		DBObject site = getSiteDBObjectByToken(assignment.getSiteToken());
		if (site != null) {
			return MongoSite.fromDBObject(site);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentMetadata
	 * (java.lang.String, com.sitewhere.spi.device.IMetadataProvider)
	 */
	public IDeviceAssignment updateDeviceAssignmentMetadata(String token, IMetadataProvider metadata)
			throws SiteWhereException {
		DBObject match = assertDeviceAssignment(token);
		MongoMetadataProvider.toDBObject(metadata, match);
		DeviceAssignment assignment = MongoDeviceAssignment.fromDBObject(match);
		SiteWherePersistence.setUpdatedEntityMetadata(assignment);
		BasicDBObject query = new BasicDBObject(MongoDeviceAssignment.PROP_TOKEN, token);
		DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
		MongoPersistence.update(assignments, query, MongoDeviceAssignment.toDBObject(assignment));
		return MongoDeviceAssignment.fromDBObject(match);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentStatus
	 * (java.lang.String, com.sitewhere.spi.device.DeviceAssignmentStatus)
	 */
	public IDeviceAssignment updateDeviceAssignmentStatus(String token, DeviceAssignmentStatus status)
			throws SiteWhereException {
		DBObject match = assertDeviceAssignment(token);
		match.put(MongoDeviceAssignment.PROP_STATUS, status.name());
		DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
		BasicDBObject query = new BasicDBObject(MongoDeviceAssignment.PROP_TOKEN, token);
		MongoPersistence.update(assignments, query, match);
		DeviceAssignment assignment = MongoDeviceAssignment.fromDBObject(match);
		return assignment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentState(java.lang
	 * .String, com.sitewhere.spi.device.IDeviceEventBatch)
	 */
	public IDeviceAssignment updateDeviceAssignmentState(String token, IDeviceEventBatch batch)
			throws SiteWhereException {
		DBObject match = assertDeviceAssignment(token);
		DeviceAssignment assignment = MongoDeviceAssignment.fromDBObject(match);
		DeviceAssignmentState state = SiteWherePersistence.assignmentStateUpdateLogic(assignment, batch);
		MongoDeviceAssignment.setState(state, match);
		DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
		BasicDBObject query = new BasicDBObject(MongoDeviceAssignment.PROP_TOKEN, token);
		MongoPersistence.update(assignments, query, match);
		DeviceAssignment updated = MongoDeviceAssignment.fromDBObject(match);
		return updated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceEventBatch(java.lang.String,
	 * com.sitewhere.spi.device.IDeviceEventBatch)
	 */
	public IDeviceEventBatchResponse addDeviceEventBatch(String assignmentToken, IDeviceEventBatch batch)
			throws SiteWhereException {
		DeviceEventBatchResponse response = new DeviceEventBatchResponse();
		DBObject match = assertDeviceAssignment(assignmentToken);
		DeviceAssignment assignment = MongoDeviceAssignment.fromDBObject(match);
		for (IDeviceMeasurementsCreateRequest measurements : batch.getMeasurements()) {
			response.getCreatedMeasurements().add(addDeviceMeasurements(assignment, measurements));
		}
		for (IDeviceLocationCreateRequest location : batch.getLocations()) {
			response.getCreatedLocations().add(addDeviceLocation(assignment, location));
		}
		for (IDeviceAlertCreateRequest alert : batch.getAlerts()) {
			response.getCreatedAlerts().add(addDeviceAlert(assignment, alert));
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#endDeviceAssignment(java.lang
	 * .String)
	 */
	public IDeviceAssignment endDeviceAssignment(String token) throws SiteWhereException {
		DBObject match = assertDeviceAssignment(token);
		match.put(MongoDeviceAssignment.PROP_RELEASED_DATE, Calendar.getInstance().getTime());
		match.put(MongoDeviceAssignment.PROP_STATUS, DeviceAssignmentStatus.Released.name());
		DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
		BasicDBObject query = new BasicDBObject(MongoDeviceAssignment.PROP_TOKEN, token);
		MongoPersistence.update(assignments, query, match);

		// Remove device assignment reference.
		DBCollection devices = getMongoClient().getDevicesCollection();
		String hardwareId = (String) match.get(MongoDeviceAssignment.PROP_DEVICE_HARDWARE_ID);
		DBObject deviceMatch = getDeviceDBObjectByHardwareId(hardwareId);
		deviceMatch.removeField(MongoDevice.PROP_ASSIGNMENT_TOKEN);
		query = new BasicDBObject(MongoDevice.PROP_HARDWARE_ID, hardwareId);
		MongoPersistence.update(devices, query, deviceMatch);

		DeviceAssignment assignment = MongoDeviceAssignment.fromDBObject(match);
		return assignment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentHistory(java.lang
	 * .String, com.sitewhere.spi.common.ISearchCriteria)
	 */
	public SearchResults<IDeviceAssignment> getDeviceAssignmentHistory(String hardwareId,
			ISearchCriteria criteria) throws SiteWhereException {
		DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
		BasicDBObject query = new BasicDBObject(MongoDeviceAssignment.PROP_DEVICE_HARDWARE_ID, hardwareId);
		BasicDBObject sort = new BasicDBObject(MongoDeviceAssignment.PROP_ACTIVE_DATE, -1);
		return MongoPersistence.search(IDeviceAssignment.class, assignments, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentsNear(double,
	 * double, double, com.sitewhere.spi.common.ISearchCriteria)
	 */
	public SearchResults<IDeviceAssignment> getDeviceAssignmentsNear(double latitude, double longitude,
			double maxDistance, ISearchCriteria criteria) throws SiteWhereException {
		DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
		BasicDBObject filter = new BasicDBObject("$nearSphere", new double[] { longitude, latitude });
		filter.put("$maxDistance", maxDistance / 3963192);
		BasicDBObject query =
				new BasicDBObject(MongoDeviceAssignment.PROP_STATE + "."
						+ MongoDeviceAssignmentState.PROP_LAST_LOCATION + "."
						+ MongoDeviceLocation.PROP_LATLONG, filter);
		BasicDBObject sort = new BasicDBObject();
		return MongoPersistence.search(IDeviceAssignment.class, assignments, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentsForSite(java.lang
	 * .String, com.sitewhere.spi.common.ISearchCriteria)
	 */
	public SearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(String siteToken,
			ISearchCriteria criteria) throws SiteWhereException {
		DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
		BasicDBObject query = new BasicDBObject(MongoDeviceAssignment.PROP_SITE_TOKEN, siteToken);
		BasicDBObject sort = new BasicDBObject(MongoDeviceAssignment.PROP_ACTIVE_DATE, -1);
		return MongoPersistence.search(IDeviceAssignment.class, assignments, query, sort, criteria);
	}

	/**
	 * Find the DBObject for a device assignment based on unique token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getDeviceAssignmentDBObjectByToken(String token) throws SiteWhereException {
		DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
		BasicDBObject query = new BasicDBObject(MongoDeviceAssignment.PROP_TOKEN, token);
		DBObject result = assignments.findOne(query);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceMeasurements(com.sitewhere.
	 * spi.device.IDeviceAssignment ,
	 * com.sitewhere.spi.device.request.IDeviceMeasurementsCreateRequest)
	 */
	public IDeviceMeasurements addDeviceMeasurements(IDeviceAssignment assignment,
			IDeviceMeasurementsCreateRequest request) throws SiteWhereException {
		DeviceMeasurements measurements =
				SiteWherePersistence.deviceMeasurementsCreateLogic(request, assignment);

		DBCollection measurementColl = getMongoClient().getMeasurementsCollection();
		DBObject mObject = MongoDeviceMeasurements.toDBObject(measurements);
		MongoPersistence.insert(measurementColl, mObject);
		return MongoDeviceMeasurements.fromDBObject(mObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceMeasurements(java.lang.String,
	 * com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	public SearchResults<IDeviceMeasurements> listDeviceMeasurements(String token,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection measurementColl = getMongoClient().getMeasurementsCollection();
		BasicDBObject query = new BasicDBObject(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, token);
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceMeasurements.class, measurementColl, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceMeasurementsForSite(java.lang
	 * .String, com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	public SearchResults<IDeviceMeasurements> listDeviceMeasurementsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection measurements = getMongoClient().getMeasurementsCollection();
		BasicDBObject query = new BasicDBObject(MongoDeviceEvent.PROP_SITE_TOKEN, siteToken);
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceMeasurements.class, measurements, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceLocation(com.sitewhere.spi.
	 * device.IDeviceAssignment ,
	 * com.sitewhere.spi.device.request.IDeviceLocationCreateRequest)
	 */
	public IDeviceLocation addDeviceLocation(IDeviceAssignment assignment,
			IDeviceLocationCreateRequest request) throws SiteWhereException {
		DeviceLocation location = SiteWherePersistence.deviceLocationCreateLogic(assignment, request);

		DBCollection locationsColl = getMongoClient().getLocationsCollection();
		DBObject locObject = MongoDeviceLocation.toDBObject(location);
		MongoPersistence.insert(locationsColl, locObject);
		return MongoDeviceLocation.fromDBObject(locObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceLocations(java.lang.String,
	 * com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	public SearchResults<IDeviceLocation> listDeviceLocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection locationsColl = getMongoClient().getLocationsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken);
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceLocation.class, locationsColl, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceLocationsForSite(java.lang
	 * .String, com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	public SearchResults<IDeviceLocation> listDeviceLocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection locationsColl = getMongoClient().getLocationsCollection();
		BasicDBObject query = new BasicDBObject(MongoDeviceEvent.PROP_SITE_TOKEN, siteToken);
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceLocation.class, locationsColl, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceLocations(java.util.List,
	 * com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	public SearchResults<IDeviceLocation> listDeviceLocations(List<String> assignmentTokens,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection locationsColl = getMongoClient().getLocationsCollection();
		BasicDBObject query = new BasicDBObject();
		query.put(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, new BasicDBObject("$in", assignmentTokens));
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceLocation.class, locationsColl, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceAlert(com.sitewhere.spi.device
	 * .IDeviceAssignment, com.sitewhere.spi.device.request.IDeviceAlertCreateRequest)
	 */
	public IDeviceAlert addDeviceAlert(IDeviceAssignment assignment, IDeviceAlertCreateRequest request)
			throws SiteWhereException {
		DeviceAlert alert = SiteWherePersistence.deviceAlertCreateLogic(assignment, request);

		DBCollection alertsColl = getMongoClient().getAlertsCollection();
		DBObject alertObject = MongoDeviceAlert.toDBObject(alert);
		MongoPersistence.insert(alertsColl, alertObject);
		return MongoDeviceAlert.fromDBObject(alertObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceAlerts(java.lang.String,
	 * com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	public SearchResults<IDeviceAlert> listDeviceAlerts(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection alerts = getMongoClient().getAlertsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken);
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceAlert.class, alerts, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceAlertsForSite(java.lang.String
	 * , com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	public SearchResults<IDeviceAlert> listDeviceAlertsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection alerts = getMongoClient().getAlertsCollection();
		BasicDBObject query = new BasicDBObject(MongoDeviceEvent.PROP_SITE_TOKEN, siteToken);
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceAlert.class, alerts, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createSite(com.sitewhere.spi.device.
	 * request.ISiteCreateRequest )
	 */
	public ISite createSite(ISiteCreateRequest request) throws SiteWhereException {
		// Use common logic so all backend implementations work the same.
		Site site = SiteWherePersistence.siteCreateLogic(request, UUID.randomUUID().toString());

		DBCollection sites = getMongoClient().getSitesCollection();
		DBObject created = MongoSite.toDBObject(site);
		MongoPersistence.insert(sites, created);
		return MongoSite.fromDBObject(created);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#updateSite(java.lang.String,
	 * com.sitewhere.spi.device.request.ISiteCreateRequest)
	 */
	public ISite updateSite(String token, ISiteCreateRequest request) throws SiteWhereException {
		DBCollection sites = getMongoClient().getSitesCollection();
		DBObject match = getSiteDBObjectByToken(token);
		if (match == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
		}
		Site site = MongoSite.fromDBObject(match);

		// Use common update logic so that backend implemetations act the same way.
		SiteWherePersistence.siteUpdateLogic(request, site);

		DBObject updated = MongoSite.toDBObject(site);

		BasicDBObject query = new BasicDBObject(MongoSite.PROP_TOKEN, token);
		MongoPersistence.update(sites, query, updated);
		return MongoSite.fromDBObject(updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getSiteByToken(java.lang.String )
	 */
	public ISite getSiteByToken(String token) throws SiteWhereException {
		DBObject result = getSiteDBObjectByToken(token);
		if (result != null) {
			return MongoSite.fromDBObject(result);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#deleteSite(java.lang.String,
	 * boolean)
	 */
	public ISite deleteSite(String siteToken, boolean force) throws SiteWhereException {
		DBObject existing = assertSite(siteToken);
		if (force) {
			DBCollection sites = getMongoClient().getSitesCollection();
			MongoPersistence.delete(sites, existing);
			return MongoSite.fromDBObject(existing);
		} else {
			MongoSiteWhereEntity.setDeleted(existing, true);
			BasicDBObject query = new BasicDBObject(MongoSite.PROP_TOKEN, siteToken);
			DBCollection sites = getMongoClient().getSitesCollection();
			MongoPersistence.update(sites, query, existing);
			return MongoSite.fromDBObject(existing);
		}
	}

	/**
	 * Get the DBObject containing site information that matches the given token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getSiteDBObjectByToken(String token) throws SiteWhereException {
		DBCollection sites = getMongoClient().getSitesCollection();
		BasicDBObject query = new BasicDBObject(MongoSite.PROP_TOKEN, token);
		DBObject result = sites.findOne(query);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listSites(com.sitewhere.spi.common.
	 * ISearchCriteria)
	 */
	public SearchResults<ISite> listSites(ISearchCriteria criteria) throws SiteWhereException {
		DBCollection sites = getMongoClient().getSitesCollection();
		BasicDBObject query = new BasicDBObject();
		BasicDBObject sort = new BasicDBObject(MongoSite.PROP_NAME, 1);
		return MongoPersistence.search(ISite.class, sites, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createZone(com.sitewhere.spi.device.
	 * ISite, com.sitewhere.spi.device.request.IZoneCreateRequest)
	 */
	public IZone createZone(ISite site, IZoneCreateRequest request) throws SiteWhereException {
		Zone zone =
				SiteWherePersistence.zoneCreateLogic(request, site.getToken(), UUID.randomUUID().toString());

		DBCollection zones = getMongoClient().getZonesCollection();
		DBObject created = MongoZone.toDBObject(zone);
		MongoPersistence.insert(zones, created);
		return MongoZone.fromDBObject(created);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#updateZone(java.lang.String,
	 * com.sitewhere.spi.device.request.IZoneCreateRequest)
	 */
	public IZone updateZone(String token, IZoneCreateRequest request) throws SiteWhereException {
		DBCollection zones = getMongoClient().getZonesCollection();
		DBObject match = assertZone(token);

		Zone zone = MongoZone.fromDBObject(match);
		SiteWherePersistence.zoneUpdateLogic(request, zone);

		DBObject updated = MongoZone.toDBObject(zone);

		BasicDBObject query = new BasicDBObject(MongoSite.PROP_TOKEN, token);
		MongoPersistence.update(zones, query, updated);
		return MongoZone.fromDBObject(updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getZone(java.lang.String)
	 */
	public IZone getZone(String zoneToken) throws SiteWhereException {
		DBObject found = assertZone(zoneToken);
		return MongoZone.fromDBObject(found);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listZones(java.lang.String,
	 * com.sitewhere.spi.common.ISearchCriteria)
	 */
	public SearchResults<IZone> listZones(String siteToken, ISearchCriteria criteria)
			throws SiteWhereException {
		DBCollection zones = getMongoClient().getZonesCollection();
		BasicDBObject query = new BasicDBObject(MongoZone.PROP_SITE_TOKEN, siteToken);
		BasicDBObject sort = new BasicDBObject(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
		return MongoPersistence.search(IZone.class, zones, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#deleteZone(java.lang.String,
	 * boolean)
	 */
	public IZone deleteZone(String zoneToken, boolean force) throws SiteWhereException {
		DBObject existing = assertZone(zoneToken);
		if (force) {
			DBCollection zones = getMongoClient().getZonesCollection();
			MongoPersistence.delete(zones, existing);
			return MongoZone.fromDBObject(existing);
		} else {
			MongoSiteWhereEntity.setDeleted(existing, true);
			BasicDBObject query = new BasicDBObject(MongoZone.PROP_TOKEN, zoneToken);
			DBCollection zones = getMongoClient().getZonesCollection();
			MongoPersistence.update(zones, query, existing);
			return MongoZone.fromDBObject(existing);
		}
	}

	/**
	 * Return the {@link DBObject} for the site with the given token. Throws an exception
	 * if the token is not found.
	 * 
	 * @param hardwareId
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject assertSite(String token) throws SiteWhereException {
		DBObject match = getSiteDBObjectByToken(token);
		if (match == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.INFO);
		}
		return match;
	}

	/**
	 * Return the {@link DBObject} for the device with the given hardware id. Throws an
	 * exception if the hardware id is not found.
	 * 
	 * @param hardwareId
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject assertDevice(String hardwareId) throws SiteWhereException {
		DBObject match = getDeviceDBObjectByHardwareId(hardwareId);
		if (match == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.INFO);
		}
		return match;
	}

	/**
	 * Return the {@link DBObject} for the assignment with the given token. Throws an
	 * exception if the token is not valid.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject assertDeviceAssignment(String token) throws SiteWhereException {
		DBObject match = getDeviceAssignmentDBObjectByToken(token);
		if (match == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
		}
		return match;
	}

	/**
	 * Return the {@link DBObject} for the zone with the given token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getZoneDBObjectByToken(String token) throws SiteWhereException {
		DBCollection zones = getMongoClient().getZonesCollection();
		BasicDBObject query = new BasicDBObject(MongoZone.PROP_TOKEN, token);
		DBObject result = zones.findOne(query);
		return result;
	}

	/**
	 * Return the {@link DBObject} for the zone with the given token. Throws an exception
	 * if the token is not valid.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject assertZone(String token) throws SiteWhereException {
		DBObject match = getZoneDBObjectByToken(token);
		if (match == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidZoneToken, ErrorLevel.ERROR);
		}
		return match;
	}

	public SiteWhereMongoClient getMongoClient() {
		return mongoClient;
	}

	public void setMongoClient(SiteWhereMongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}
}