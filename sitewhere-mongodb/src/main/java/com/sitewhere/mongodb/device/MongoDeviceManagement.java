/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.device;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoTimeoutException;
import com.mongodb.WriteResult;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.mongodb.IDeviceManagementMongoClient;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceAssignmentState;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.device.batch.BatchElement;
import com.sitewhere.rest.model.device.batch.BatchOperation;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.rest.model.device.event.DeviceStreamData;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.common.IMetadataProvider;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.ICachingDeviceManagement;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceAssignmentState;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.device.batch.IBatchElement;
import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.device.request.IBatchElementUpdateRequest;
import com.sitewhere.spi.device.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.device.request.IBatchOperationUpdateRequest;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCommandCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest;
import com.sitewhere.spi.device.request.ISiteCreateRequest;
import com.sitewhere.spi.device.request.IZoneCreateRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Device management implementation that uses MongoDB for persistence.
 * 
 * @author dadams
 */
public class MongoDeviceManagement extends LifecycleComponent implements IDeviceManagement,
		ICachingDeviceManagement {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(MongoDeviceManagement.class);

	/** Injected with global SiteWhere Mongo client */
	private IDeviceManagementMongoClient mongoClient;

	/** Provides caching for device management entities */
	private IDeviceManagementCacheProvider cacheProvider;

	/** Device event buffer implementation */
	private IDeviceEventBuffer eventBuffer;

	/** Indicates whether bulk inserts should be used for adding events */
	private boolean useBulkEventInserts = false;

	/** Maximum number of records to write in a chunk */
	private int bulkInsertMaxChunkSize = 1000;

	public MongoDeviceManagement() {
		super(LifecycleComponentType.DataStore);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	public void start() throws SiteWhereException {
		// Ensure that collection indexes exist.
		ensureIndexes();

		// Support bulk inserts for events.
		if (isUseBulkEventInserts()) {
			this.eventBuffer =
					new DeviceEventBuffer(getMongoClient().getEventsCollection(), getBulkInsertMaxChunkSize());
			getEventBuffer().start();
			LOGGER.info("MongoDB device management is using bulk inserts for events.");
		} else {
			LOGGER.info("MongoDB device management is not using bulk inserts for events.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.ICachingDeviceManagement#setCacheProvider(com.sitewhere
	 * .spi.device.IDeviceManagementCacheProvider)
	 */
	public void setCacheProvider(IDeviceManagementCacheProvider cacheProvider) {
		this.cacheProvider = cacheProvider;
	}

	public IDeviceManagementCacheProvider getCacheProvider() {
		return cacheProvider;
	}

	/**
	 * Ensure that expected collection indexes exist.
	 * 
	 * @throws SiteWhereException
	 */
	protected void ensureIndexes() throws SiteWhereException {
		getMongoClient().getSitesCollection().createIndex(new BasicDBObject(MongoSite.PROP_TOKEN, 1),
				new BasicDBObject("unique", true));
		getMongoClient().getDeviceSpecificationsCollection().createIndex(
				new BasicDBObject(MongoDeviceSpecification.PROP_TOKEN, 1), new BasicDBObject("unique", true));
		getMongoClient().getDevicesCollection().createIndex(
				new BasicDBObject(MongoDevice.PROP_HARDWARE_ID, 1), new BasicDBObject("unique", true));
		getMongoClient().getDeviceAssignmentsCollection().createIndex(
				new BasicDBObject(MongoDeviceAssignment.PROP_TOKEN, 1), new BasicDBObject("unique", true));
		getMongoClient().getDeviceAssignmentsCollection().createIndex(
				new BasicDBObject(MongoDeviceAssignment.PROP_SITE_TOKEN, 1).append(
						MongoDeviceAssignment.PROP_ASSET_MODULE_ID, 1).append(
						MongoDeviceAssignment.PROP_ASSET_ID, 1).append(MongoDeviceAssignment.PROP_STATUS, 1));
		getMongoClient().getEventsCollection().createIndex(
				new BasicDBObject(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, 1).append(
						MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_EVENT_TYPE, 1));
		getMongoClient().getEventsCollection().createIndex(
				new BasicDBObject(MongoDeviceEvent.PROP_SITE_TOKEN, 1).append(
						MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_EVENT_TYPE, 1));
		getMongoClient().getDeviceGroupsCollection().createIndex(
				new BasicDBObject(MongoDeviceGroup.PROP_TOKEN, 1), new BasicDBObject("unique", true));
		getMongoClient().getDeviceGroupsCollection().createIndex(
				new BasicDBObject(MongoDeviceGroup.PROP_ROLES, 1));
		getMongoClient().getGroupElementsCollection().createIndex(
				new BasicDBObject(MongoDeviceGroupElement.PROP_GROUP_TOKEN, 1).append(
						MongoDeviceGroupElement.PROP_TYPE, 1).append(MongoDeviceGroupElement.PROP_ELEMENT_ID,
						1));
		getMongoClient().getGroupElementsCollection().createIndex(
				new BasicDBObject(MongoDeviceGroupElement.PROP_GROUP_TOKEN, 1).append(
						MongoDeviceGroupElement.PROP_ROLES, 1));
		getMongoClient().getBatchOperationsCollection().createIndex(
				new BasicDBObject(MongoBatchOperation.PROP_TOKEN, 1), new BasicDBObject("unique", true));
		getMongoClient().getBatchOperationElementsCollection().createIndex(
				new BasicDBObject(MongoBatchElement.PROP_BATCH_OPERATION_TOKEN, 1));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	public void stop() throws SiteWhereException {
		LOGGER.info("Mongo device management stopped.");

		// Stop the event buffer if used.
		if (getEventBuffer() != null) {
			getEventBuffer().stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createDeviceSpecification(com.sitewhere
	 * .spi.device.request.IDeviceSpecificationCreateRequest)
	 */
	@Override
	public IDeviceSpecification createDeviceSpecification(IDeviceSpecificationCreateRequest request)
			throws SiteWhereException {
		String uuid = null;
		if (request.getToken() != null) {
			uuid = request.getToken();
		} else {
			uuid = UUID.randomUUID().toString();
		}

		// Use common logic so all backend implementations work the same.
		DeviceSpecification spec = SiteWherePersistence.deviceSpecificationCreateLogic(request, uuid);

		DBCollection specs = getMongoClient().getDeviceSpecificationsCollection();
		DBObject created = MongoDeviceSpecification.toDBObject(spec);
		MongoPersistence.insert(specs, created);

		// Update cache with new data.
		if (getCacheProvider() != null) {
			getCacheProvider().getDeviceSpecificationCache().put(uuid, spec);
		}
		return MongoDeviceSpecification.fromDBObject(created);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceSpecificationByToken(java.lang
	 * .String)
	 */
	@Override
	public IDeviceSpecification getDeviceSpecificationByToken(String token) throws SiteWhereException {
		if (getCacheProvider() != null) {
			IDeviceSpecification cached = getCacheProvider().getDeviceSpecificationCache().get(token);
			if (cached != null) {
				return cached;
			}
		}
		DBObject dbSpecification = getDeviceSpecificationDBObjectByToken(token);
		if (dbSpecification != null) {
			IDeviceSpecification result = MongoDeviceSpecification.fromDBObject(dbSpecification);
			if ((getCacheProvider() != null) && (result != null)) {
				getCacheProvider().getDeviceSpecificationCache().put(token, result);
			}
			return result;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#updateDeviceSpecification(java.lang.
	 * String, com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest)
	 */
	@Override
	public IDeviceSpecification updateDeviceSpecification(String token,
			IDeviceSpecificationCreateRequest request) throws SiteWhereException {
		DBObject match = assertDeviceSpecification(token);
		DeviceSpecification spec = MongoDeviceSpecification.fromDBObject(match);

		// Use common update logic so that backend implemetations act the same way.
		SiteWherePersistence.deviceSpecificationUpdateLogic(request, spec);
		DBObject updated = MongoDeviceSpecification.toDBObject(spec);

		BasicDBObject query = new BasicDBObject(MongoDeviceSpecification.PROP_TOKEN, token);
		DBCollection specs = getMongoClient().getDeviceSpecificationsCollection();
		MongoPersistence.update(specs, query, updated);

		// Update cache with new data.
		if (getCacheProvider() != null) {
			getCacheProvider().getDeviceSpecificationCache().put(token, spec);
		}
		return MongoDeviceSpecification.fromDBObject(updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceSpecifications(boolean,
	 * com.sitewhere.spi.search.ISearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceSpecification> listDeviceSpecifications(boolean includeDeleted,
			ISearchCriteria criteria) throws SiteWhereException {
		DBCollection specs = getMongoClient().getDeviceSpecificationsCollection();
		DBObject dbCriteria = new BasicDBObject();
		if (!includeDeleted) {
			MongoSiteWhereEntity.setDeleted(dbCriteria, false);
		}
		BasicDBObject sort = new BasicDBObject(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
		return MongoPersistence.search(IDeviceSpecification.class, specs, dbCriteria, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceSpecification(java.lang.
	 * String, boolean)
	 */
	@Override
	public IDeviceSpecification deleteDeviceSpecification(String token, boolean force)
			throws SiteWhereException {
		DBObject existing = assertDeviceSpecification(token);
		DBCollection specs = getMongoClient().getDeviceSpecificationsCollection();
		if (force) {
			MongoPersistence.delete(specs, existing);
			return MongoDeviceSpecification.fromDBObject(existing);
		} else {
			MongoSiteWhereEntity.setDeleted(existing, true);
			BasicDBObject query = new BasicDBObject(MongoDeviceSpecification.PROP_TOKEN, token);
			MongoPersistence.update(specs, query, existing);
			return MongoDeviceSpecification.fromDBObject(existing);
		}
	}

	/**
	 * Return the {@link DBObject} for the device specification with the given token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getDeviceSpecificationDBObjectByToken(String token) throws SiteWhereException {
		DBCollection specs = getMongoClient().getDeviceSpecificationsCollection();
		BasicDBObject query = new BasicDBObject(MongoDeviceSpecification.PROP_TOKEN, token);
		DBObject result = specs.findOne(query);
		return result;
	}

	/**
	 * Return the {@link DBObject} for the device specification with the given token.
	 * Throws an exception if the token is not valid.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject assertDeviceSpecification(String token) throws SiteWhereException {
		DBObject match = getDeviceSpecificationDBObjectByToken(token);
		if (match == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceSpecificationToken, ErrorLevel.ERROR);
		}
		return match;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createDeviceCommand(com.sitewhere.spi
	 * .device.IDeviceSpecification,
	 * com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
	 */
	@Override
	public IDeviceCommand createDeviceCommand(IDeviceSpecification spec, IDeviceCommandCreateRequest request)
			throws SiteWhereException {
		// Note: This allows duplicates if duplicate was marked deleted.
		List<IDeviceCommand> existing = listDeviceCommands(spec.getToken(), false);

		// Use common logic so all backend implementations work the same.
		String uuid = ((request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString());
		DeviceCommand command = SiteWherePersistence.deviceCommandCreateLogic(spec, request, uuid, existing);

		DBCollection commands = getMongoClient().getDeviceCommandsCollection();
		DBObject created = MongoDeviceCommand.toDBObject(command);
		MongoPersistence.insert(commands, created);
		return MongoDeviceCommand.fromDBObject(created);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceCommandByToken(java.lang.String
	 * )
	 */
	@Override
	public IDeviceCommand getDeviceCommandByToken(String token) throws SiteWhereException {
		DBObject result = getDeviceCommandDBObjectByToken(token);
		if (result != null) {
			return MongoDeviceCommand.fromDBObject(result);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#updateDeviceCommand(java.lang.String,
	 * com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
	 */
	@Override
	public IDeviceCommand updateDeviceCommand(String token, IDeviceCommandCreateRequest request)
			throws SiteWhereException {
		DBObject match = assertDeviceCommand(token);
		DeviceCommand command = MongoDeviceCommand.fromDBObject(match);

		// Note: This allows duplicates if duplicate was marked deleted.
		List<IDeviceCommand> existing = listDeviceCommands(token, false);

		// Use common update logic so that backend implemetations act the same way.
		SiteWherePersistence.deviceCommandUpdateLogic(request, command, existing);
		DBObject updated = MongoDeviceCommand.toDBObject(command);

		BasicDBObject query = new BasicDBObject(MongoDeviceCommand.PROP_TOKEN, token);
		DBCollection commands = getMongoClient().getDeviceCommandsCollection();
		MongoPersistence.update(commands, query, updated);
		return MongoDeviceCommand.fromDBObject(updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommands(java.lang.String,
	 * boolean)
	 */
	@Override
	public List<IDeviceCommand> listDeviceCommands(String token, boolean includeDeleted)
			throws SiteWhereException {
		DBCollection commands = getMongoClient().getDeviceCommandsCollection();
		DBObject dbCriteria = new BasicDBObject();
		dbCriteria.put(MongoDeviceCommand.PROP_SPEC_TOKEN, token);
		if (!includeDeleted) {
			MongoSiteWhereEntity.setDeleted(dbCriteria, false);
		}
		BasicDBObject sort = new BasicDBObject(MongoDeviceCommand.PROP_NAME, 1);
		return MongoPersistence.list(IDeviceCommand.class, commands, dbCriteria, sort);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceCommand(java.lang.String,
	 * boolean)
	 */
	@Override
	public IDeviceCommand deleteDeviceCommand(String token, boolean force) throws SiteWhereException {
		DBObject existing = assertDeviceCommand(token);
		DBCollection commands = getMongoClient().getDeviceCommandsCollection();
		if (force) {
			MongoPersistence.delete(commands, existing);
			return MongoDeviceCommand.fromDBObject(existing);
		} else {
			MongoSiteWhereEntity.setDeleted(existing, true);
			BasicDBObject query = new BasicDBObject(MongoDeviceCommand.PROP_TOKEN, token);
			MongoPersistence.update(commands, query, existing);
			return MongoDeviceCommand.fromDBObject(existing);
		}
	}

	/**
	 * Return the {@link DBObject} for the device command with the given token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getDeviceCommandDBObjectByToken(String token) throws SiteWhereException {
		DBCollection specs = getMongoClient().getDeviceCommandsCollection();
		BasicDBObject query = new BasicDBObject(MongoDeviceCommand.PROP_TOKEN, token);
		DBObject result = specs.findOne(query);
		return result;
	}

	/**
	 * Return the {@link DBObject} for the device command with the given token. Throws an
	 * exception if the token is not valid.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject assertDeviceCommand(String token) throws SiteWhereException {
		DBObject match = getDeviceCommandDBObjectByToken(token);
		if (match == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceCommandToken, ErrorLevel.ERROR);
		}
		return match;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createDevice(com.sitewhere.spi.device
	 * .request. IDeviceCreateRequest)
	 */
	@Override
	public IDevice createDevice(IDeviceCreateRequest request) throws SiteWhereException {
		IDevice existing = getDeviceByHardwareId(request.getHardwareId());
		if (existing != null) {
			throw new SiteWhereSystemException(ErrorCode.DuplicateHardwareId, ErrorLevel.ERROR,
					HttpServletResponse.SC_CONFLICT);
		}
		Device newDevice = SiteWherePersistence.deviceCreateLogic(request);

		// Convert and save device data.
		DBCollection devices = getMongoClient().getDevicesCollection();
		DBObject created = MongoDevice.toDBObject(newDevice);
		MongoPersistence.insert(devices, created);

		// Update cache with new data.
		if (getCacheProvider() != null) {
			getCacheProvider().getDeviceCache().put(request.getHardwareId(), newDevice);
		}
		return newDevice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#updateDevice(java.lang.String,
	 * com.sitewhere.spi.device.request.IDeviceCreateRequest)
	 */
	@Override
	public IDevice updateDevice(String hardwareId, IDeviceCreateRequest request) throws SiteWhereException {
		DBObject existing = assertDevice(hardwareId);
		Device updatedDevice = MongoDevice.fromDBObject(existing);

		SiteWherePersistence.deviceUpdateLogic(request, updatedDevice);
		DBObject updated = MongoDevice.toDBObject(updatedDevice);

		DBCollection devices = getMongoClient().getDevicesCollection();
		BasicDBObject query = new BasicDBObject(MongoDevice.PROP_HARDWARE_ID, hardwareId);
		MongoPersistence.update(devices, query, updated);

		// Update cache with new data.
		if (getCacheProvider() != null) {
			getCacheProvider().getDeviceCache().put(hardwareId, updatedDevice);
		}
		return MongoDevice.fromDBObject(updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceByHardwareId(java
	 * .lang.String)
	 */
	@Override
	public IDevice getDeviceByHardwareId(String hardwareId) throws SiteWhereException {
		if (getCacheProvider() != null) {
			IDevice cached = getCacheProvider().getDeviceCache().get(hardwareId);
			if (cached != null) {
				return cached;
			}
		}
		DBObject dbDevice = getDeviceDBObjectByHardwareId(hardwareId);
		if (dbDevice != null) {
			IDevice result = MongoDevice.fromDBObject(dbDevice);
			if ((getCacheProvider() != null) && (result != null)) {
				getCacheProvider().getDeviceCache().put(hardwareId, result);
			}
			return result;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getCurrentDeviceAssignment
	 * (com.sitewhere.spi.device .IDevice)
	 */
	@Override
	public IDeviceAssignment getCurrentDeviceAssignment(IDevice device) throws SiteWhereException {
		if (device.getAssignmentToken() == null) {
			return null;
		}
		return assertApiDeviceAssignment(device.getAssignmentToken());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listDevices(boolean,
	 * com.sitewhere.spi.search.device.IDeviceSearchCriteria)
	 */
	@Override
	public SearchResults<IDevice> listDevices(boolean includeDeleted, IDeviceSearchCriteria criteria)
			throws SiteWhereException {
		DBCollection devices = getMongoClient().getDevicesCollection();
		BasicDBObject dbCriteria = new BasicDBObject();
		if (!includeDeleted) {
			MongoSiteWhereEntity.setDeleted(dbCriteria, false);
		}
		if (criteria.isExcludeAssigned()) {
			dbCriteria.put(MongoDevice.PROP_ASSIGNMENT_TOKEN, null);
		}
		MongoPersistence.addDateSearchCriteria(dbCriteria, MongoSiteWhereEntity.PROP_CREATED_DATE, criteria);
		switch (criteria.getSearchType()) {
		case All: {
			break;
		}
		case UsesSpecification: {
			if (criteria.getDeviceBySpecificationParameters() != null) {
				String token = criteria.getDeviceBySpecificationParameters().getSpecificationToken();
				if (token == null) {
					throw new SiteWhereException("Invalid device search. No specification token passed.");
				}
				dbCriteria.put(MongoDevice.PROP_SPECIFICATION_TOKEN, token);
			}
			break;
		}
		}
		BasicDBObject sort = new BasicDBObject(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
		return MongoPersistence.search(IDevice.class, devices, dbCriteria, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createDeviceElementMapping(java.lang
	 * .String, com.sitewhere.spi.device.IDeviceElementMapping)
	 */
	@Override
	public IDevice createDeviceElementMapping(String hardwareId, IDeviceElementMapping mapping)
			throws SiteWhereException {
		return SiteWherePersistence.deviceElementMappingCreateLogic(this, hardwareId, mapping);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceElementMapping(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public IDevice deleteDeviceElementMapping(String hardwareId, String path) throws SiteWhereException {
		return SiteWherePersistence.deviceElementMappingDeleteLogic(this, hardwareId, path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#deleteDevice(java.lang.String,
	 * boolean)
	 */
	@Override
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
	@Override
	public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request)
			throws SiteWhereException {
		DBObject deviceDb = assertDevice(request.getDeviceHardwareId());
		if (deviceDb.get(MongoDevice.PROP_ASSIGNMENT_TOKEN) != null) {
			throw new SiteWhereSystemException(ErrorCode.DeviceAlreadyAssigned, ErrorLevel.ERROR);
		}
		Device device = MongoDevice.fromDBObject(deviceDb);

		// Use common logic to load assignment from request.
		DeviceAssignment newAssignment =
				SiteWherePersistence.deviceAssignmentCreateLogic(request, device,
						UUID.randomUUID().toString());

		DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
		DBObject created = MongoDeviceAssignment.toDBObject(newAssignment);
		MongoPersistence.insert(assignments, created);

		// Update cache with new assignment data.
		if (getCacheProvider() != null) {
			getCacheProvider().getDeviceAssignmentCache().put(newAssignment.getToken(), newAssignment);
		}

		// Update device to point to created assignment.
		DBCollection devices = getMongoClient().getDevicesCollection();
		BasicDBObject query = new BasicDBObject(MongoDevice.PROP_HARDWARE_ID, request.getDeviceHardwareId());
		deviceDb.put(MongoDevice.PROP_ASSIGNMENT_TOKEN, newAssignment.getToken());
		MongoPersistence.update(devices, query, deviceDb);

		// Update cache with new device data.
		if (getCacheProvider() != null) {
			Device updated = MongoDevice.fromDBObject(deviceDb);
			getCacheProvider().getDeviceCache().put(updated.getHardwareId(), updated);
		}
		return newAssignment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentByToken
	 * (java.lang.String)
	 */
	@Override
	public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
		if (getCacheProvider() != null) {
			IDeviceAssignment cached = getCacheProvider().getDeviceAssignmentCache().get(token);
			if (cached != null) {
				return cached;
			}
		}
		DBObject dbAssignment = getDeviceAssignmentDBObjectByToken(token);
		if (dbAssignment != null) {
			IDeviceAssignment result = MongoDeviceAssignment.fromDBObject(dbAssignment);
			if ((getCacheProvider() != null) && (result != null)) {
				getCacheProvider().getDeviceAssignmentCache().put(token, result);
			}
			return result;
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
	@Override
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
	@Override
	public IDevice getDeviceForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
		return getDeviceByHardwareId(assignment.getDeviceHardwareId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getSiteForAssignment(com.sitewhere
	 * .spi.device. IDeviceAssignment)
	 */
	@Override
	public ISite getSiteForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
		return getSiteByToken(assignment.getSiteToken());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentMetadata
	 * (java.lang.String, com.sitewhere.spi.device.IMetadataProvider)
	 */
	@Override
	public IDeviceAssignment updateDeviceAssignmentMetadata(String token, IMetadataProvider metadata)
			throws SiteWhereException {
		DBObject match = assertDeviceAssignment(token);
		MongoMetadataProvider.toDBObject(metadata, match);
		DeviceAssignment assignment = MongoDeviceAssignment.fromDBObject(match);
		SiteWherePersistence.setUpdatedEntityMetadata(assignment);
		BasicDBObject query = new BasicDBObject(MongoDeviceAssignment.PROP_TOKEN, token);
		DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
		MongoPersistence.update(assignments, query, MongoDeviceAssignment.toDBObject(assignment));

		// Update cache with new assignment data.
		if (getCacheProvider() != null) {
			getCacheProvider().getDeviceAssignmentCache().put(assignment.getToken(), assignment);
		}

		return MongoDeviceAssignment.fromDBObject(match);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentStatus
	 * (java.lang.String, com.sitewhere.spi.device.DeviceAssignmentStatus)
	 */
	@Override
	public IDeviceAssignment updateDeviceAssignmentStatus(String token, DeviceAssignmentStatus status)
			throws SiteWhereException {
		DBObject match = assertDeviceAssignment(token);
		match.put(MongoDeviceAssignment.PROP_STATUS, status.name());
		DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
		BasicDBObject query = new BasicDBObject(MongoDeviceAssignment.PROP_TOKEN, token);
		MongoPersistence.update(assignments, query, match);
		DeviceAssignment updated = MongoDeviceAssignment.fromDBObject(match);

		// Update cache with new assignment data.
		if (getCacheProvider() != null) {
			getCacheProvider().getDeviceAssignmentCache().put(updated.getToken(), updated);
		}

		return updated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentState(java.lang
	 * .String, com.sitewhere.spi.device.IDeviceEventBatch)
	 */
	@Override
	public IDeviceAssignment updateDeviceAssignmentState(String token, IDeviceAssignmentState state)
			throws SiteWhereException {
		DBObject match = assertDeviceAssignment(token);
		MongoDeviceAssignment.setState(state, match);
		DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
		BasicDBObject query = new BasicDBObject(MongoDeviceAssignment.PROP_TOKEN, token);
		MongoPersistence.update(assignments, query, match);
		DeviceAssignment updated = MongoDeviceAssignment.fromDBObject(match);

		// Update cache with new assignment data.
		if (getCacheProvider() != null) {
			getCacheProvider().getDeviceAssignmentCache().put(updated.getToken(), updated);
		}

		return updated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceEventBatch(java.lang.String,
	 * com.sitewhere.spi.device.IDeviceEventBatch)
	 */
	@Override
	public IDeviceEventBatchResponse addDeviceEventBatch(String assignmentToken, IDeviceEventBatch batch)
			throws SiteWhereException {
		return SiteWherePersistence.deviceEventBatchLogic(assignmentToken, batch, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#endDeviceAssignment(java.lang
	 * .String)
	 */
	@Override
	public IDeviceAssignment endDeviceAssignment(String token) throws SiteWhereException {
		DBObject match = assertDeviceAssignment(token);
		match.put(MongoDeviceAssignment.PROP_RELEASED_DATE, Calendar.getInstance().getTime());
		match.put(MongoDeviceAssignment.PROP_STATUS, DeviceAssignmentStatus.Released.name());
		DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
		BasicDBObject query = new BasicDBObject(MongoDeviceAssignment.PROP_TOKEN, token);
		MongoPersistence.update(assignments, query, match);

		// Update cache with new assignment data.
		if (getCacheProvider() != null) {
			DeviceAssignment updated = MongoDeviceAssignment.fromDBObject(match);
			getCacheProvider().getDeviceAssignmentCache().put(updated.getToken(), updated);
		}

		// Remove device assignment reference.
		DBCollection devices = getMongoClient().getDevicesCollection();
		String hardwareId = (String) match.get(MongoDeviceAssignment.PROP_DEVICE_HARDWARE_ID);
		DBObject deviceMatch = getDeviceDBObjectByHardwareId(hardwareId);
		deviceMatch.removeField(MongoDevice.PROP_ASSIGNMENT_TOKEN);
		query = new BasicDBObject(MongoDevice.PROP_HARDWARE_ID, hardwareId);
		MongoPersistence.update(devices, query, deviceMatch);

		// Update cache with new device data.
		if (getCacheProvider() != null) {
			Device updated = MongoDevice.fromDBObject(deviceMatch);
			getCacheProvider().getDeviceCache().put(updated.getHardwareId(), updated);
		}

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
	@Override
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
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentsForSite(java.lang
	 * .String, com.sitewhere.spi.common.ISearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(String siteToken,
			ISearchCriteria criteria) throws SiteWhereException {
		DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
		BasicDBObject query = new BasicDBObject(MongoDeviceAssignment.PROP_SITE_TOKEN, siteToken);
		BasicDBObject sort = new BasicDBObject(MongoDeviceAssignment.PROP_ACTIVE_DATE, -1);
		return MongoPersistence.search(IDeviceAssignment.class, assignments, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentsForAsset(java.lang
	 * .String, java.lang.String, java.lang.String,
	 * com.sitewhere.spi.device.DeviceAssignmentStatus,
	 * com.sitewhere.spi.search.ISearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForAsset(String siteToken,
			String assetModuleId, String assetId, DeviceAssignmentStatus status, ISearchCriteria criteria)
			throws SiteWhereException {
		DBCollection assignments = getMongoClient().getDeviceAssignmentsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceAssignment.PROP_SITE_TOKEN, siteToken).append(
						MongoDeviceAssignment.PROP_ASSET_MODULE_ID, assetModuleId).append(
						MongoDeviceAssignment.PROP_ASSET_ID, assetId);
		if (status != null) {
			query.append(MongoDeviceAssignment.PROP_STATUS, status.name());
		}
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
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceEventById(java.lang.String)
	 */
	@Override
	public IDeviceEvent getDeviceEventById(String id) throws SiteWhereException {
		DBObject searchById = new BasicDBObject("_id", new ObjectId(id));
		DBObject found = getMongoClient().getEventsCollection().findOne(searchById);
		if (found == null) {
			return null;
		}
		return MongoPersistence.unmarshalEvent(found);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceEvents(java.lang.String,
	 * com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceEvent> listDeviceEvents(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection events = getMongoClient().getEventsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken);
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);

		int offset = Math.max(0, criteria.getPageNumber() - 1) * criteria.getPageSize();
		DBCursor cursor = events.find(query).skip(offset).limit(criteria.getPageSize()).sort(sort);
		List<IDeviceEvent> matches = new ArrayList<IDeviceEvent>();
		SearchResults<IDeviceEvent> results = new SearchResults<IDeviceEvent>(matches);
		try {
			results.setNumResults(cursor.count());
			while (cursor.hasNext()) {
				DBObject match = cursor.next();
				matches.add(MongoPersistence.unmarshalEvent(match));
			}
		} finally {
			cursor.close();
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceMeasurements(java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
	 */
	@Override
	public IDeviceMeasurements addDeviceMeasurements(String assignmentToken,
			IDeviceMeasurementsCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentToken);
		DeviceMeasurements measurements =
				SiteWherePersistence.deviceMeasurementsCreateLogic(request, assignment);

		DBCollection events = getMongoClient().getEventsCollection();
		DBObject mObject = MongoDeviceMeasurements.toDBObject(measurements, false);
		MongoPersistence.insertEvent(events, mObject, isUseBulkEventInserts(), getEventBuffer());

		// Update assignment state if requested.
		measurements = MongoDeviceMeasurements.fromDBObject(mObject, false);
		if (request.isUpdateState()) {
			DeviceAssignmentState updated =
					SiteWherePersistence.assignmentStateMeasurementsUpdateLogic(assignment, measurements);
			updateDeviceAssignmentState(assignmentToken, updated);
		}

		return measurements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceMeasurements(java.lang.String,
	 * com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceMeasurements> listDeviceMeasurements(String token,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection events = getMongoClient().getEventsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, token).append(
						MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Measurements.name());
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceMeasurements.class, events, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceMeasurementsForSite(java.lang
	 * .String, com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceMeasurements> listDeviceMeasurementsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection events = getMongoClient().getEventsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceEvent.PROP_SITE_TOKEN, siteToken).append(
						MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Measurements.name());
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceMeasurements.class, events, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#addDeviceLocation(java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
	 */
	@Override
	public IDeviceLocation addDeviceLocation(String assignmentToken, IDeviceLocationCreateRequest request)
			throws SiteWhereException {
		IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentToken);
		DeviceLocation location = SiteWherePersistence.deviceLocationCreateLogic(assignment, request);

		DBCollection events = getMongoClient().getEventsCollection();
		DBObject locObject = MongoDeviceLocation.toDBObject(location, false);
		MongoPersistence.insertEvent(events, locObject, isUseBulkEventInserts(), getEventBuffer());

		// Update assignment state if requested.
		location = MongoDeviceLocation.fromDBObject(locObject, false);
		if (request.isUpdateState()) {
			DeviceAssignmentState updated =
					SiteWherePersistence.assignmentStateLocationUpdateLogic(assignment, location);
			updateDeviceAssignmentState(assignment.getToken(), updated);
		}

		return location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceLocations(java.lang.String,
	 * com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceLocation> listDeviceLocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection events = getMongoClient().getEventsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken).append(
						MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Location.name());
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceLocation.class, events, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceLocationsForSite(java.lang
	 * .String, com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceLocation> listDeviceLocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection events = getMongoClient().getEventsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceEvent.PROP_SITE_TOKEN, siteToken).append(
						MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Location.name());
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceLocation.class, events, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceLocations(java.util.List,
	 * com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceLocation> listDeviceLocations(List<String> assignmentTokens,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection events = getMongoClient().getEventsCollection();
		BasicDBObject query = new BasicDBObject();
		query.put(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, new BasicDBObject("$in", assignmentTokens));
		query.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Location.name());
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceLocation.class, events, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#addDeviceAlert(java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
	 */
	@Override
	public IDeviceAlert addDeviceAlert(String assignmentToken, IDeviceAlertCreateRequest request)
			throws SiteWhereException {
		IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentToken);
		DeviceAlert alert = SiteWherePersistence.deviceAlertCreateLogic(assignment, request);

		DBCollection events = getMongoClient().getEventsCollection();
		DBObject alertObject = MongoDeviceAlert.toDBObject(alert, false);
		MongoPersistence.insertEvent(events, alertObject, isUseBulkEventInserts(), getEventBuffer());

		// Update assignment state if requested.
		alert = MongoDeviceAlert.fromDBObject(alertObject, false);
		if (request.isUpdateState()) {
			DeviceAssignmentState updated =
					SiteWherePersistence.assignmentStateAlertUpdateLogic(assignment, alert);
			updateDeviceAssignmentState(assignment.getToken(), updated);
		}

		return alert;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceAlerts(java.lang.String,
	 * com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceAlert> listDeviceAlerts(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection events = getMongoClient().getEventsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken).append(
						MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Alert.name());
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceAlert.class, events, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceAlertsForSite(java.lang.String
	 * , com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceAlert> listDeviceAlertsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection events = getMongoClient().getEventsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceEvent.PROP_SITE_TOKEN, siteToken).append(
						MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Alert.name());
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceAlert.class, events, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createDeviceStream(java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest)
	 */
	@Override
	public IDeviceStream createDeviceStream(String assignmentToken, IDeviceStreamCreateRequest request)
			throws SiteWhereException {
		// Use common logic so all backend implementations work the same.
		IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentToken);
		DeviceStream stream = SiteWherePersistence.deviceStreamCreateLogic(assignment, request);

		// Verify that another stream with the given id does not exist.
		if (getDeviceStream(stream.getAssignmentToken(), stream.getStreamId()) != null) {
			throw new SiteWhereSystemException(ErrorCode.DuplicateStreamId, ErrorLevel.ERROR);
		}

		DBCollection streams = getMongoClient().getStreamsCollection();
		DBObject created = MongoDeviceStream.toDBObject(stream);
		MongoPersistence.insert(streams, created);
		return MongoDeviceStream.fromDBObject(created);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceStream(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public IDeviceStream getDeviceStream(String assignmentToken, String streamId) throws SiteWhereException {
		DBObject dbStream = getDeviceStreamDBObject(assignmentToken, streamId);
		if (dbStream == null) {
			return null;
		}
		return MongoDeviceStream.fromDBObject(dbStream);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceStreams(java.lang.String,
	 * com.sitewhere.spi.search.ISearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceStream> listDeviceStreams(String assignmentToken, ISearchCriteria criteria)
			throws SiteWhereException {
		DBCollection streams = getMongoClient().getStreamsCollection();
		BasicDBObject query = new BasicDBObject(MongoDeviceStream.PROP_ASSIGNMENT_TOKEN, assignmentToken);
		BasicDBObject sort = new BasicDBObject(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
		return MongoPersistence.search(IDeviceStream.class, streams, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceStreamData(java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest)
	 */
	@Override
	public IDeviceStreamData addDeviceStreamData(String assignmentToken,
			IDeviceStreamDataCreateRequest request) throws SiteWhereException {
		// Use common logic so all backend implementations work the same.
		IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentToken);
		DeviceStreamData streamData = SiteWherePersistence.deviceStreamDataCreateLogic(assignment, request);

		// Verify that a stream with the given id exists for the assignment.
		if (getDeviceStream(assignmentToken, request.getStreamId()) == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidStreamId, ErrorLevel.ERROR);
		}

		DBCollection events = getMongoClient().getEventsCollection();
		DBObject streamDataObject = MongoDeviceStreamData.toDBObject(streamData, false);
		MongoPersistence.insertEvent(events, streamDataObject, isUseBulkEventInserts(), getEventBuffer());

		return MongoDeviceStreamData.fromDBObject(streamDataObject, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceStreamData(java.lang.String,
	 * java.lang.String, long)
	 */
	@Override
	public IDeviceStreamData getDeviceStreamData(String assignmentToken, String streamId, long sequenceNumber)
			throws SiteWhereException {
		DBObject dbData = getDeviceStreamDataDBObject(assignmentToken, streamId, sequenceNumber);
		if (dbData == null) {
			return null;
		}
		return MongoDeviceStreamData.fromDBObject(dbData, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceStreamData(java.lang.String,
	 * java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceStreamData> listDeviceStreamData(String assignmentToken, String streamId,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection events = getMongoClient().getEventsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken).append(
						MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.StreamData.name()).append(
						MongoDeviceStreamData.PROP_STREAM_ID, streamId);
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort = new BasicDBObject(MongoDeviceStreamData.PROP_SEQUENCE_NUMBER, 1);
		return MongoPersistence.search(IDeviceStreamData.class, events, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceCommandInvocation(java.lang
	 * .String, com.sitewhere.spi.device.command.IDeviceCommand,
	 * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest)
	 */
	@Override
	public IDeviceCommandInvocation addDeviceCommandInvocation(String assignmentToken,
			IDeviceCommand command, IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentToken);
		DeviceCommandInvocation ci =
				SiteWherePersistence.deviceCommandInvocationCreateLogic(assignment, command, request);

		DBCollection events = getMongoClient().getEventsCollection();
		DBObject ciObject = MongoDeviceCommandInvocation.toDBObject(ci);
		MongoPersistence.insertEvent(events, ciObject, isUseBulkEventInserts(), getEventBuffer());

		return MongoDeviceCommandInvocation.fromDBObject(ciObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommandInvocations(java.lang
	 * .String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection events = getMongoClient().getEventsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken).append(
						MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandInvocation.name());
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceCommandInvocation.class, events, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommandInvocationsForSite(
	 * java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection events = getMongoClient().getEventsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceEvent.PROP_SITE_TOKEN, siteToken).append(
						MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandInvocation.name());
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceCommandInvocation.class, events, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommandInvocationResponses
	 * (java.lang.String)
	 */
	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(String invocationId)
			throws SiteWhereException {
		DBCollection events = getMongoClient().getEventsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandResponse.name()).append(
						MongoDeviceCommandResponse.PROP_ORIGINATING_EVENT_ID, invocationId);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceCommandResponse.class, events, query, sort);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceCommandResponse(java.lang.String
	 * , com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest)
	 */
	@Override
	public IDeviceCommandResponse addDeviceCommandResponse(String assignmentToken,
			IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentToken);
		DeviceCommandResponse response =
				SiteWherePersistence.deviceCommandResponseCreateLogic(assignment, request);

		DBCollection events = getMongoClient().getEventsCollection();
		DBObject dbresponse = MongoDeviceCommandResponse.toDBObject(response);
		MongoPersistence.insertEvent(events, dbresponse, isUseBulkEventInserts(), getEventBuffer());

		return MongoDeviceCommandResponse.fromDBObject(dbresponse);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommandResponses(java.lang
	 * .String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponses(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection events = getMongoClient().getEventsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken).append(
						MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandResponse.name());
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceCommandResponse.class, events, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommandResponsesForSite(java
	 * .lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection events = getMongoClient().getEventsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceEvent.PROP_SITE_TOKEN, siteToken).append(
						MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandResponse.name());
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceCommandResponse.class, events, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceStateChange(java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest)
	 */
	@Override
	public IDeviceStateChange addDeviceStateChange(String assignmentToken,
			IDeviceStateChangeCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentToken);
		DeviceStateChange state = SiteWherePersistence.deviceStateChangeCreateLogic(assignment, request);

		DBCollection events = getMongoClient().getEventsCollection();
		DBObject dbstate = MongoDeviceStateChange.toDBObject(state);
		MongoPersistence.insertEvent(events, dbstate, isUseBulkEventInserts(), getEventBuffer());

		return MongoDeviceStateChange.fromDBObject(dbstate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceStateChanges(java.lang.String,
	 * com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceStateChange> listDeviceStateChanges(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection events = getMongoClient().getEventsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken).append(
						MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.StateChange.name());
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceStateChange.class, events, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceStateChangesForSite(java.lang
	 * .String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceStateChange> listDeviceStateChangesForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		DBCollection events = getMongoClient().getEventsCollection();
		BasicDBObject query =
				new BasicDBObject(MongoDeviceEvent.PROP_SITE_TOKEN, siteToken).append(
						MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.StateChange.name());
		MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
		BasicDBObject sort =
				new BasicDBObject(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(
						MongoDeviceEvent.PROP_RECEIVED_DATE, -1);
		return MongoPersistence.search(IDeviceStateChange.class, events, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createSite(com.sitewhere.spi.device.
	 * request.ISiteCreateRequest )
	 */
	@Override
	public ISite createSite(ISiteCreateRequest request) throws SiteWhereException {
		// Use common logic so all backend implementations work the same.
		Site site = SiteWherePersistence.siteCreateLogic(request);

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
	@Override
	public ISite updateSite(String token, ISiteCreateRequest request) throws SiteWhereException {
		DBObject match = getSiteDBObjectByToken(token);
		if (match == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
		}
		Site site = MongoSite.fromDBObject(match);

		// Use common update logic so that backend implemetations act the same way.
		SiteWherePersistence.siteUpdateLogic(request, site);

		DBObject updated = MongoSite.toDBObject(site);

		DBCollection sites = getMongoClient().getSitesCollection();
		BasicDBObject query = new BasicDBObject(MongoSite.PROP_TOKEN, token);
		MongoPersistence.update(sites, query, updated);
		if (getCacheProvider() != null) {
			getCacheProvider().getSiteCache().put(token, site);
		}
		return MongoSite.fromDBObject(updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getSiteByToken(java.lang.String )
	 */
	@Override
	public ISite getSiteByToken(String token) throws SiteWhereException {
		DBObject dbSite = getSiteDBObjectByToken(token);
		if (dbSite != null) {
			return MongoSite.fromDBObject(dbSite);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#deleteSite(java.lang.String,
	 * boolean)
	 */
	@Override
	public ISite deleteSite(String siteToken, boolean force) throws SiteWhereException {
		DBObject existing = assertSite(siteToken);
		if (force) {
			DBCollection sites = getMongoClient().getSitesCollection();
			MongoPersistence.delete(sites, existing);
			if (getCacheProvider() != null) {
				getCacheProvider().getSiteCache().remove(siteToken);
			}
			return MongoSite.fromDBObject(existing);
		} else {
			MongoSiteWhereEntity.setDeleted(existing, true);
			BasicDBObject query = new BasicDBObject(MongoSite.PROP_TOKEN, siteToken);
			DBCollection sites = getMongoClient().getSitesCollection();
			MongoPersistence.update(sites, query, existing);
			if (getCacheProvider() != null) {
				getCacheProvider().getSiteCache().remove(siteToken);
			}
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
		if (getCacheProvider() != null) {
			ISite cached = getCacheProvider().getSiteCache().get(token);
			if (cached != null) {
				return MongoSite.toDBObject(cached);
			}
		}
		DBCollection sites = getMongoClient().getSitesCollection();
		BasicDBObject query = new BasicDBObject(MongoSite.PROP_TOKEN, token);
		DBObject result = sites.findOne(query);
		if ((getCacheProvider() != null) && (result != null)) {
			ISite site = MongoSite.fromDBObject(result);
			getCacheProvider().getSiteCache().put(token, site);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listSites(com.sitewhere.spi.common.
	 * ISearchCriteria)
	 */
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createDeviceGroup(com.sitewhere.spi.
	 * device.request.IDeviceGroupCreateRequest)
	 */
	@Override
	public IDeviceGroup createDeviceGroup(IDeviceGroupCreateRequest request) throws SiteWhereException {
		String uuid = ((request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString());
		DeviceGroup group = SiteWherePersistence.deviceGroupCreateLogic(request, uuid);

		DBCollection groups = getMongoClient().getDeviceGroupsCollection();
		DBObject created = MongoDeviceGroup.toDBObject(group);
		created.put(MongoDeviceGroup.PROP_LAST_INDEX, new Long(0));

		MongoPersistence.insert(groups, created);
		return MongoDeviceGroup.fromDBObject(created);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#updateDeviceGroup(java.lang.String,
	 * com.sitewhere.spi.device.request.IDeviceGroupCreateRequest)
	 */
	@Override
	public IDeviceGroup updateDeviceGroup(String token, IDeviceGroupCreateRequest request)
			throws SiteWhereException {
		DBCollection groups = getMongoClient().getDeviceGroupsCollection();
		DBObject match = assertDeviceGroup(token);

		DeviceGroup group = MongoDeviceGroup.fromDBObject(match);
		SiteWherePersistence.deviceGroupUpdateLogic(request, group);

		DBObject updated = MongoDeviceGroup.toDBObject(group);

		// Manually copy last index since it's not copied by default.
		updated.put(MongoDeviceGroup.PROP_LAST_INDEX, match.get(MongoDeviceGroup.PROP_LAST_INDEX));

		BasicDBObject query = new BasicDBObject(MongoDeviceGroup.PROP_TOKEN, token);
		MongoPersistence.update(groups, query, updated);
		return MongoDeviceGroup.fromDBObject(updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceGroup(java.lang.String)
	 */
	@Override
	public IDeviceGroup getDeviceGroup(String token) throws SiteWhereException {
		DBObject found = assertDeviceGroup(token);
		return MongoDeviceGroup.fromDBObject(found);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceGroups(boolean,
	 * com.sitewhere.spi.search.ISearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceGroup> listDeviceGroups(boolean includeDeleted, ISearchCriteria criteria)
			throws SiteWhereException {
		DBCollection groups = getMongoClient().getDeviceGroupsCollection();
		DBObject dbCriteria = new BasicDBObject();
		if (!includeDeleted) {
			MongoSiteWhereEntity.setDeleted(dbCriteria, false);
		}
		BasicDBObject sort = new BasicDBObject(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
		return MongoPersistence.search(IDeviceGroup.class, groups, dbCriteria, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroupsWithRole(java.lang.String
	 * , boolean, com.sitewhere.spi.search.ISearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceGroup> listDeviceGroupsWithRole(String role, boolean includeDeleted,
			ISearchCriteria criteria) throws SiteWhereException {
		DBCollection groups = getMongoClient().getDeviceGroupsCollection();
		DBObject dbCriteria = new BasicDBObject(MongoDeviceGroup.PROP_ROLES, role);
		if (!includeDeleted) {
			MongoSiteWhereEntity.setDeleted(dbCriteria, false);
		}
		BasicDBObject sort = new BasicDBObject(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
		return MongoPersistence.search(IDeviceGroup.class, groups, dbCriteria, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#deleteDeviceGroup(java.lang.String,
	 * boolean)
	 */
	@Override
	public IDeviceGroup deleteDeviceGroup(String token, boolean force) throws SiteWhereException {
		DBObject existing = assertDeviceGroup(token);
		if (force) {
			DBCollection groups = getMongoClient().getDeviceGroupsCollection();
			MongoPersistence.delete(groups, existing);

			// Delete group elements as well.
			DBCollection elements = getMongoClient().getGroupElementsCollection();
			BasicDBObject match = new BasicDBObject(MongoDeviceGroupElement.PROP_GROUP_TOKEN, token);
			MongoPersistence.delete(elements, match);

			return MongoDeviceGroup.fromDBObject(existing);
		} else {
			MongoSiteWhereEntity.setDeleted(existing, true);
			BasicDBObject query = new BasicDBObject(MongoDeviceGroup.PROP_TOKEN, token);
			DBCollection groups = getMongoClient().getDeviceGroupsCollection();
			MongoPersistence.update(groups, query, existing);
			return MongoDeviceGroup.fromDBObject(existing);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceGroupElements(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public List<IDeviceGroupElement> addDeviceGroupElements(String groupToken,
			List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
		List<IDeviceGroupElement> results = new ArrayList<IDeviceGroupElement>();
		for (IDeviceGroupElementCreateRequest request : elements) {
			long index = MongoDeviceGroup.getNextGroupIndex(getMongoClient(), groupToken);
			DeviceGroupElement element =
					SiteWherePersistence.deviceGroupElementCreateLogic(request, groupToken, index);
			DBObject created = MongoDeviceGroupElement.toDBObject(element);
			MongoPersistence.insert(getMongoClient().getGroupElementsCollection(), created);
			results.add(MongoDeviceGroupElement.fromDBObject(created));
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#removeDeviceGroupElements(java.lang.
	 * String, java.util.List)
	 */
	@Override
	public List<IDeviceGroupElement> removeDeviceGroupElements(String groupToken,
			List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
		List<IDeviceGroupElement> deleted = new ArrayList<IDeviceGroupElement>();
		for (IDeviceGroupElementCreateRequest request : elements) {
			BasicDBObject match =
					new BasicDBObject(MongoDeviceGroupElement.PROP_GROUP_TOKEN, groupToken).append(
							MongoDeviceGroupElement.PROP_TYPE, request.getType().name()).append(
							MongoDeviceGroupElement.PROP_ELEMENT_ID, request.getElementId());
			DBCursor found = getMongoClient().getGroupElementsCollection().find(match);
			while (found.hasNext()) {
				DBObject current = found.next();
				WriteResult result =
						MongoPersistence.delete(getMongoClient().getGroupElementsCollection(), current);
				if (result.getN() > 0) {
					deleted.add(MongoDeviceGroupElement.fromDBObject(current));
				}
			}
		}
		return deleted;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroupElements(java.lang.String
	 * , com.sitewhere.spi.search.ISearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceGroupElement> listDeviceGroupElements(String groupToken,
			ISearchCriteria criteria) throws SiteWhereException {
		BasicDBObject match = new BasicDBObject(MongoDeviceGroupElement.PROP_GROUP_TOKEN, groupToken);
		BasicDBObject sort = new BasicDBObject(MongoDeviceGroupElement.PROP_INDEX, 1);
		return MongoPersistence.search(IDeviceGroupElement.class,
				getMongoClient().getGroupElementsCollection(), match, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createBatchOperation(com.sitewhere.spi
	 * .device.request.IBatchOperationCreateRequest)
	 */
	@Override
	public IBatchOperation createBatchOperation(IBatchOperationCreateRequest request)
			throws SiteWhereException {
		String uuid = ((request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString());
		BatchOperation batch = SiteWherePersistence.batchOperationCreateLogic(request, uuid);

		DBCollection batches = getMongoClient().getBatchOperationsCollection();
		DBObject created = MongoBatchOperation.toDBObject(batch);
		MongoPersistence.insert(batches, created);

		// Insert element for each hardware id.
		long index = 0;
		DBCollection elements = getMongoClient().getBatchOperationElementsCollection();
		for (String hardwareId : request.getHardwareIds()) {
			BatchElement element =
					SiteWherePersistence.batchElementCreateLogic(batch.getToken(), hardwareId, ++index);
			DBObject dbElement = MongoBatchElement.toDBObject(element);
			MongoPersistence.insert(elements, dbElement);
		}

		return MongoBatchOperation.fromDBObject(created);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#updateBatchOperation(java.lang.String,
	 * com.sitewhere.spi.device.request.IBatchOperationUpdateRequest)
	 */
	@Override
	public IBatchOperation updateBatchOperation(String token, IBatchOperationUpdateRequest request)
			throws SiteWhereException {
		DBCollection batchops = getMongoClient().getBatchOperationsCollection();
		DBObject match = assertBatchOperation(token);

		BatchOperation operation = MongoBatchOperation.fromDBObject(match);
		SiteWherePersistence.batchOperationUpdateLogic(request, operation);

		DBObject updated = MongoBatchOperation.toDBObject(operation);

		BasicDBObject query = new BasicDBObject(MongoBatchOperation.PROP_TOKEN, token);
		MongoPersistence.update(batchops, query, updated);
		return MongoBatchOperation.fromDBObject(updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getBatchOperation(java.lang.String)
	 */
	@Override
	public IBatchOperation getBatchOperation(String token) throws SiteWhereException {
		DBObject found = assertBatchOperation(token);
		return MongoBatchOperation.fromDBObject(found);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listBatchOperations(boolean,
	 * com.sitewhere.spi.search.ISearchCriteria)
	 */
	@Override
	public ISearchResults<IBatchOperation> listBatchOperations(boolean includeDeleted,
			ISearchCriteria criteria) throws SiteWhereException {
		DBCollection ops = getMongoClient().getBatchOperationsCollection();
		DBObject dbCriteria = new BasicDBObject();
		if (!includeDeleted) {
			MongoSiteWhereEntity.setDeleted(dbCriteria, false);
		}
		BasicDBObject sort = new BasicDBObject(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
		return MongoPersistence.search(IBatchOperation.class, ops, dbCriteria, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#deleteBatchOperation(java.lang.String,
	 * boolean)
	 */
	@Override
	public IBatchOperation deleteBatchOperation(String token, boolean force) throws SiteWhereException {
		DBObject existing = assertBatchOperation(token);
		if (force) {
			DBCollection ops = getMongoClient().getBatchOperationsCollection();
			MongoPersistence.delete(ops, existing);

			// Delete operation elements as well.
			DBCollection elements = getMongoClient().getBatchOperationElementsCollection();
			BasicDBObject match = new BasicDBObject(MongoBatchElement.PROP_BATCH_OPERATION_TOKEN, token);
			MongoPersistence.delete(elements, match);

			return MongoBatchOperation.fromDBObject(existing);
		} else {
			MongoSiteWhereEntity.setDeleted(existing, true);
			BasicDBObject query = new BasicDBObject(MongoDeviceGroup.PROP_TOKEN, token);
			DBCollection ops = getMongoClient().getBatchOperationsCollection();
			MongoPersistence.update(ops, query, existing);
			return MongoBatchOperation.fromDBObject(existing);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listBatchElements(java.lang.String,
	 * com.sitewhere.spi.search.device.IBatchElementSearchCriteria)
	 */
	@Override
	public SearchResults<IBatchElement> listBatchElements(String batchToken,
			IBatchElementSearchCriteria criteria) throws SiteWhereException {
		DBCollection elements = getMongoClient().getBatchOperationElementsCollection();
		DBObject dbCriteria = new BasicDBObject(MongoBatchElement.PROP_BATCH_OPERATION_TOKEN, batchToken);
		if (criteria.getProcessingStatus() != null) {
			dbCriteria.put(MongoBatchElement.PROP_PROCESSING_STATUS, criteria.getProcessingStatus());
		}
		BasicDBObject sort = new BasicDBObject(MongoBatchElement.PROP_INDEX, 1);
		return MongoPersistence.search(IBatchElement.class, elements, dbCriteria, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#updateBatchElement(java.lang.String,
	 * long, com.sitewhere.spi.device.request.IBatchElementUpdateRequest)
	 */
	@Override
	public IBatchElement updateBatchElement(String operationToken, long index,
			IBatchElementUpdateRequest request) throws SiteWhereException {
		DBCollection elements = getMongoClient().getBatchOperationElementsCollection();
		DBObject dbElement = assertBatchElement(operationToken, index);

		BatchElement element = MongoBatchElement.fromDBObject(dbElement);
		SiteWherePersistence.batchElementUpdateLogic(request, element);

		DBObject updated = MongoBatchElement.toDBObject(element);

		BasicDBObject query =
				new BasicDBObject(MongoBatchElement.PROP_BATCH_OPERATION_TOKEN, operationToken).append(
						MongoBatchElement.PROP_INDEX, index);
		MongoPersistence.update(elements, query, updated);
		return MongoBatchElement.fromDBObject(updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createBatchCommandInvocation(com.sitewhere
	 * .spi.device.request.IBatchCommandInvocationRequest)
	 */
	@Override
	public IBatchOperation createBatchCommandInvocation(IBatchCommandInvocationRequest request)
			throws SiteWhereException {
		String uuid = ((request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString());
		IBatchOperationCreateRequest generic =
				SiteWherePersistence.batchCommandInvocationCreateLogic(request, uuid);
		return createBatchOperation(generic);
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
		if (getCacheProvider() != null) {

		}
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
	 * Return an {@link IDeviceAssignment} for the given token. Throws an exception if the
	 * token is not valid.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDeviceAssignment assertApiDeviceAssignment(String token) throws SiteWhereException {
		if (getCacheProvider() != null) {
			IDeviceAssignment result = getCacheProvider().getDeviceAssignmentCache().get(token);
			if (result != null) {
				return result;
			}
		}
		DBObject match = assertDeviceAssignment(token);
		IDeviceAssignment result = MongoDeviceAssignment.fromDBObject(match);
		if ((getCacheProvider() != null) && (result != null)) {
			getCacheProvider().getDeviceAssignmentCache().put(token, result);
		}
		return result;
	}

	/**
	 * Return the {@link DBObject} for the zone with the given token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getZoneDBObjectByToken(String token) throws SiteWhereException {
		try {
			DBCollection zones = getMongoClient().getZonesCollection();
			BasicDBObject query = new BasicDBObject(MongoZone.PROP_TOKEN, token);
			DBObject result = zones.findOne(query);
			return result;
		} catch (MongoTimeoutException e) {
			throw new SiteWhereException("Connection to MongoDB lost.", e);
		}
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

	/**
	 * Get the {@link DBObject} for an {@link IDeviceStream} based on assignment token and
	 * stream id.
	 * 
	 * @param assignmentToken
	 * @param streamId
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getDeviceStreamDBObject(String assignmentToken, String streamId)
			throws SiteWhereException {
		try {
			DBCollection streams = getMongoClient().getStreamsCollection();
			BasicDBObject query =
					new BasicDBObject(MongoDeviceStream.PROP_ASSIGNMENT_TOKEN, assignmentToken).append(
							MongoDeviceStream.PROP_STREAM_ID, streamId);
			DBObject result = streams.findOne(query);
			return result;
		} catch (MongoTimeoutException e) {
			throw new SiteWhereException("Connection to MongoDB lost.", e);
		}
	}

	/**
	 * Get the {@link DBObject} for an {@link IDeviceStreamData} chunk based on assignment
	 * token, stream id, and sequence number.
	 * 
	 * @param assignmentToken
	 * @param streamId
	 * @param sequenceNumber
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getDeviceStreamDataDBObject(String assignmentToken, String streamId,
			long sequenceNumber) throws SiteWhereException {
		try {
			DBCollection events = getMongoClient().getEventsCollection();
			BasicDBObject query =
					new BasicDBObject(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken).append(
							MongoDeviceStreamData.PROP_STREAM_ID, streamId).append(
							MongoDeviceStreamData.PROP_SEQUENCE_NUMBER, sequenceNumber);
			DBObject result = events.findOne(query);
			return result;
		} catch (MongoTimeoutException e) {
			throw new SiteWhereException("Connection to MongoDB lost.", e);
		}
	}

	/**
	 * Returns the {@link DBObject} for the device group with the given token. Returns
	 * null if not found.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getDeviceGroupDBObjectByToken(String token) throws SiteWhereException {
		try {
			DBCollection groups = getMongoClient().getDeviceGroupsCollection();
			BasicDBObject query = new BasicDBObject(MongoDeviceGroup.PROP_TOKEN, token);
			DBObject result = groups.findOne(query);
			return result;
		} catch (MongoTimeoutException e) {
			throw new SiteWhereException("Connection to MongoDB lost.", e);
		}
	}

	/**
	 * Return the {@link DBObject} for the device group with the given token. Throws an
	 * exception if the token is not valid.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject assertDeviceGroup(String token) throws SiteWhereException {
		DBObject match = getDeviceGroupDBObjectByToken(token);
		if (match == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceGroupToken, ErrorLevel.ERROR);
		}
		return match;
	}

	/**
	 * Returns the {@link DBObject} for the batch operation with the given token. Returns
	 * null if not found.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getBatchOperationDBObjectByToken(String token) throws SiteWhereException {
		try {
			DBCollection ops = getMongoClient().getBatchOperationsCollection();
			BasicDBObject query = new BasicDBObject(MongoBatchOperation.PROP_TOKEN, token);
			DBObject result = ops.findOne(query);
			return result;
		} catch (MongoTimeoutException e) {
			throw new SiteWhereException("Connection to MongoDB lost.", e);
		}
	}

	/**
	 * Return the {@link DBObject} for the batch operation with the given token. Throws an
	 * exception if the token is not valid.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject assertBatchOperation(String token) throws SiteWhereException {
		DBObject match = getBatchOperationDBObjectByToken(token);
		if (match == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidBatchOperationToken, ErrorLevel.ERROR);
		}
		return match;
	}

	/**
	 * Return the {@link DBObject} for the batch operation element based on the token for
	 * its parent operation and its index.
	 * 
	 * @param operationToken
	 * @param index
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getBatchElementDBObjectByIndex(String operationToken, long index)
			throws SiteWhereException {
		try {
			DBCollection ops = getMongoClient().getBatchOperationElementsCollection();
			BasicDBObject query =
					new BasicDBObject(MongoBatchElement.PROP_BATCH_OPERATION_TOKEN, operationToken).append(
							MongoBatchElement.PROP_INDEX, index);
			DBObject result = ops.findOne(query);
			return result;
		} catch (MongoTimeoutException e) {
			throw new SiteWhereException("Connection to MongoDB lost.", e);
		}
	}

	protected DBObject assertBatchElement(String operationToken, long index) throws SiteWhereException {
		DBObject match = getBatchElementDBObjectByIndex(operationToken, index);
		if (match == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidBatchElement, ErrorLevel.ERROR);
		}
		return match;
	}

	public IDeviceManagementMongoClient getMongoClient() {
		return mongoClient;
	}

	public void setMongoClient(IDeviceManagementMongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	public IDeviceEventBuffer getEventBuffer() {
		return eventBuffer;
	}

	public void setEventBuffer(IDeviceEventBuffer eventBuffer) {
		this.eventBuffer = eventBuffer;
	}

	public boolean isUseBulkEventInserts() {
		return useBulkEventInserts;
	}

	public void setUseBulkEventInserts(boolean useBulkEventInserts) {
		this.useBulkEventInserts = useBulkEventInserts;
	}

	public int getBulkInsertMaxChunkSize() {
		return bulkInsertMaxChunkSize;
	}

	public void setBulkInsertMaxChunkSize(int bulkInsertMaxChunkSize) {
		this.bulkInsertMaxChunkSize = bulkInsertMaxChunkSize;
	}
}