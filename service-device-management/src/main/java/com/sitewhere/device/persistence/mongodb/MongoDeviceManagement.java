/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.mongodb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.MongoTimeoutException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.DeleteResult;
import com.sitewhere.device.persistence.DeviceManagementPersistence;
import com.sitewhere.mongodb.IMongoConverterLookup;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceStatus;
import com.sitewhere.rest.model.device.DeviceType;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAssetReference;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCommandCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.device.request.IDeviceStatusCreateRequest;
import com.sitewhere.spi.device.request.IDeviceTypeCreateRequest;
import com.sitewhere.spi.device.request.ISiteCreateRequest;
import com.sitewhere.spi.device.request.IZoneCreateRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.error.ResourceExistsException;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IAssignmentSearchCriteria;
import com.sitewhere.spi.search.device.IAssignmentsForAssetSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Device management implementation that uses MongoDB for persistence.
 * 
 * @author dadams
 */
/**
 * @author Derek
 *
 */
public class MongoDeviceManagement extends TenantEngineLifecycleComponent implements IDeviceManagement {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Converter lookup */
    private static IMongoConverterLookup LOOKUP = new MongoConverters();

    /** Injected with global SiteWhere Mongo client */
    private IDeviceManagementMongoClient mongoClient;

    public MongoDeviceManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Ensure that collection indexes exist.
	ensureIndexes();
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

    /**
     * Ensure that expected collection indexes exist.
     * 
     * @throws SiteWhereException
     */
    protected void ensureIndexes() throws SiteWhereException {
	// Site indexes.
	getMongoClient().getSitesCollection().createIndex(new Document(MongoSite.PROP_ID, 1),
		new IndexOptions().unique(true));
	getMongoClient().getSitesCollection().createIndex(new Document(MongoSite.PROP_TOKEN, 1),
		new IndexOptions().unique(true));

	// Specification-related indexes.
	getMongoClient().getDeviceTypesCollection().createIndex(new Document(MongoDeviceType.PROP_ID, 1),
		new IndexOptions().unique(true));
	getMongoClient().getDeviceTypesCollection().createIndex(new Document(MongoDeviceType.PROP_TOKEN, 1),
		new IndexOptions().unique(true));
	getMongoClient().getDeviceStatusesCollection().createIndex(
		new Document(MongoDeviceStatus.PROP_DEVICE_TYPE_ID, 1).append(MongoDeviceStatus.PROP_CODE, 1),
		new IndexOptions().unique(true));

	// Devices.
	getMongoClient().getDevicesCollection().createIndex(new Document(MongoDevice.PROP_ID, 1),
		new IndexOptions().unique(true));
	getMongoClient().getDevicesCollection().createIndex(new Document(MongoDevice.PROP_HARDWARE_ID, 1),
		new IndexOptions().unique(true));

	// Device assignments.
	getMongoClient().getDeviceAssignmentsCollection().createIndex(new Document(MongoDeviceAssignment.PROP_ID, 1),
		new IndexOptions().unique(true));
	getMongoClient().getDeviceAssignmentsCollection().createIndex(new Document(MongoDeviceAssignment.PROP_TOKEN, 1),
		new IndexOptions().unique(true));
	getMongoClient().getDeviceAssignmentsCollection()
		.createIndex(new Document(MongoDeviceAssignment.PROP_SITE_ID, 1)
			.append(MongoDeviceAssignment.PROP_ASSET_REFERENCE, 1)
			.append(MongoDeviceAssignment.PROP_STATUS, 1));

	// Device group indexes.
	getMongoClient().getDeviceGroupsCollection().createIndex(new Document(MongoDeviceGroup.PROP_ID, 1),
		new IndexOptions().unique(true));
	getMongoClient().getDeviceGroupsCollection().createIndex(new Document(MongoDeviceGroup.PROP_TOKEN, 1),
		new IndexOptions().unique(true));
	getMongoClient().getDeviceGroupsCollection().createIndex(new Document(MongoDeviceGroup.PROP_ROLES, 1));
	getMongoClient().getGroupElementsCollection().createIndex(new Document(MongoDeviceGroupElement.PROP_GROUP_ID, 1)
		.append(MongoDeviceGroupElement.PROP_TYPE, 1).append(MongoDeviceGroupElement.PROP_ELEMENT_ID, 1));
	getMongoClient().getGroupElementsCollection().createIndex(
		new Document(MongoDeviceGroupElement.PROP_GROUP_ID, 1).append(MongoDeviceGroupElement.PROP_ROLES, 1));
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceType(com.sitewhere.spi
     * .device.request.IDeviceTypeCreateRequest)
     */
    @Override
    public IDeviceType createDeviceType(IDeviceTypeCreateRequest request) throws SiteWhereException {
	String uuid = null;
	if (request.getToken() != null) {
	    uuid = request.getToken();
	} else {
	    uuid = UUID.randomUUID().toString();
	}

	// Use common logic so all backend implementations work the same.
	DeviceType deviceType = DeviceManagementPersistence.deviceTypeCreateLogic(request, uuid);

	MongoCollection<Document> types = getMongoClient().getDeviceTypesCollection();
	Document created = MongoDeviceType.toDocument(deviceType);
	MongoPersistence.insert(types, created, ErrorCode.DuplicateDeviceTypeToken);

	return MongoDeviceType.fromDocument(created);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceType(java.util.UUID)
     */
    @Override
    public IDeviceType getDeviceType(UUID id) throws SiteWhereException {
	Document dbType = getDeviceTypeDocumentById(id);
	if (dbType != null) {
	    return MongoDeviceType.fromDocument(dbType);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceTypeByToken(java.lang.
     * String)
     */
    @Override
    public IDeviceType getDeviceTypeByToken(String token) throws SiteWhereException {
	Document dbSpecification = getDeviceTypeDocumentByToken(token);
	if (dbSpecification != null) {
	    return MongoDeviceType.fromDocument(dbSpecification);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceType(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceTypeCreateRequest)
     */
    @Override
    public IDeviceType updateDeviceType(UUID id, IDeviceTypeCreateRequest request) throws SiteWhereException {
	Document match = assertDeviceType(id);
	DeviceType deviceType = MongoDeviceType.fromDocument(match);

	// Use common update logic.
	DeviceManagementPersistence.deviceTypeUpdateLogic(request, deviceType);
	Document updated = MongoDeviceType.toDocument(deviceType);

	Document query = new Document(MongoDeviceType.PROP_ID, id);
	MongoCollection<Document> types = getMongoClient().getDeviceTypesCollection();
	MongoPersistence.update(types, query, updated);

	return MongoDeviceType.fromDocument(updated);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceTypes(boolean,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceType> listDeviceTypes(boolean includeDeleted, ISearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> types = getMongoClient().getDeviceTypesCollection();
	Document dbCriteria = new Document();
	if (!includeDeleted) {
	    MongoSiteWhereEntity.setDeleted(dbCriteria, false);
	}
	Document sort = new Document(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IDeviceType.class, types, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceType(java.util.UUID,
     * boolean)
     */
    @Override
    public IDeviceType deleteDeviceType(UUID id, boolean force) throws SiteWhereException {
	Document existing = assertDeviceType(id);
	MongoCollection<Document> types = getMongoClient().getDeviceTypesCollection();
	if (force) {
	    MongoPersistence.delete(types, existing);
	    return MongoDeviceType.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoDeviceType.PROP_ID, id);
	    MongoPersistence.update(types, query, existing);
	    return MongoDeviceType.fromDocument(existing);
	}
    }

    /**
     * Return the {@link Document} for the device type with the given token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceTypeDocumentByToken(String token) throws SiteWhereException {
	MongoCollection<Document> types = getMongoClient().getDeviceTypesCollection();
	Document query = new Document(MongoDeviceType.PROP_TOKEN, token);
	return types.find(query).first();
    }

    /**
     * Return the {@link Document} for the device type with the given id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceTypeDocumentById(UUID id) throws SiteWhereException {
	MongoCollection<Document> types = getMongoClient().getDeviceTypesCollection();
	Document query = new Document(MongoDeviceType.PROP_ID, id);
	return types.find(query).first();
    }

    /**
     * Return the {@link Document} for the device type with the given token. Throws
     * an exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document assertDeviceType(UUID id) throws SiteWhereException {
	Document match = getDeviceTypeDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Get API device type or throw exception if not found.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceType assertApiDeviceType(UUID id) throws SiteWhereException {
	Document match = getDeviceTypeDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}
	return MongoDeviceType.fromDocument(match);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceCommand(java.util.
     * UUID, com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public IDeviceCommand createDeviceCommand(UUID deviceTypeId, IDeviceCommandCreateRequest request)
	    throws SiteWhereException {
	// Note: This allows duplicates if duplicate was marked deleted.
	List<IDeviceCommand> existing = listDeviceCommands(deviceTypeId, false);

	// Use common logic so all backend implementations work the same.
	String uuid = ((request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString());
	IDeviceType deviceType = getDeviceType(deviceTypeId);
	DeviceCommand command = DeviceManagementPersistence.deviceCommandCreateLogic(deviceType, request, uuid,
		existing);

	MongoCollection<Document> commands = getMongoClient().getDeviceCommandsCollection();
	Document created = MongoDeviceCommand.toDocument(command);
	MongoPersistence.insert(commands, created, ErrorCode.DeviceCommandExists);
	return MongoDeviceCommand.fromDocument(created);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceCommand(java.util.UUID)
     */
    @Override
    public IDeviceCommand getDeviceCommand(UUID id) throws SiteWhereException {
	Document result = getDeviceCommandDocumentById(id);
	if (result != null) {
	    return MongoDeviceCommand.fromDocument(result);
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceCommandByToken(java.
     * lang.String )
     */
    @Override
    public IDeviceCommand getDeviceCommandByToken(String token) throws SiteWhereException {
	Document result = getDeviceCommandDocumentByToken(token);
	if (result != null) {
	    return MongoDeviceCommand.fromDocument(result);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceCommand(java.util.
     * UUID, com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public IDeviceCommand updateDeviceCommand(UUID id, IDeviceCommandCreateRequest request) throws SiteWhereException {
	Document match = assertDeviceCommand(id);
	DeviceCommand command = MongoDeviceCommand.fromDocument(match);

	// Note: This allows duplicates if duplicate was marked deleted.
	List<IDeviceCommand> existing = listDeviceCommands(command.getDeviceTypeId(), false);

	// Use common update logic.
	DeviceManagementPersistence.deviceCommandUpdateLogic(request, command, existing);
	Document updated = MongoDeviceCommand.toDocument(command);

	Document query = new Document(MongoDeviceCommand.PROP_ID, id);
	MongoCollection<Document> commands = getMongoClient().getDeviceCommandsCollection();
	MongoPersistence.update(commands, query, updated);
	return MongoDeviceCommand.fromDocument(updated);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommands(java.util.UUID,
     * boolean)
     */
    @Override
    public List<IDeviceCommand> listDeviceCommands(UUID deviceTypeId, boolean includeDeleted)
	    throws SiteWhereException {
	MongoCollection<Document> commands = getMongoClient().getDeviceCommandsCollection();
	Document dbCriteria = new Document();
	dbCriteria.put(MongoDeviceCommand.PROP_DEVICE_TYPE_ID, deviceTypeId);
	if (!includeDeleted) {
	    MongoSiteWhereEntity.setDeleted(dbCriteria, false);
	}
	Document sort = new Document(MongoDeviceCommand.PROP_NAME, 1);
	return MongoPersistence.list(IDeviceCommand.class, commands, dbCriteria, sort, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceCommand(java.util.
     * UUID, boolean)
     */
    @Override
    public IDeviceCommand deleteDeviceCommand(UUID id, boolean force) throws SiteWhereException {
	Document existing = assertDeviceCommand(id);
	MongoCollection<Document> commands = getMongoClient().getDeviceCommandsCollection();
	if (force) {
	    MongoPersistence.delete(commands, existing);
	    return MongoDeviceCommand.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoDeviceCommand.PROP_ID, id);
	    MongoPersistence.update(commands, query, existing);
	    return MongoDeviceCommand.fromDocument(existing);
	}
    }

    /**
     * Return the {@link Document} for the device command with the given token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceCommandDocumentByToken(String token) throws SiteWhereException {
	MongoCollection<Document> commands = getMongoClient().getDeviceCommandsCollection();
	Document query = new Document(MongoDeviceCommand.PROP_TOKEN, token);
	return commands.find(query).first();
    }

    /**
     * Return the {@link Document} for the device command with the given id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceCommandDocumentById(UUID id) throws SiteWhereException {
	MongoCollection<Document> commands = getMongoClient().getDeviceCommandsCollection();
	Document query = new Document(MongoDeviceCommand.PROP_ID, id);
	return commands.find(query).first();
    }

    /**
     * Return the {@link Document} for the device command with the given token.
     * Throws an exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document assertDeviceCommand(UUID id) throws SiteWhereException {
	Document match = getDeviceCommandDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceCommandToken, ErrorLevel.ERROR);
	}
	return match;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceStatus(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus createDeviceStatus(UUID deviceTypeId, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	// Get list of existing statuses to prevent duplicates.
	List<IDeviceStatus> existing = listDeviceStatuses(deviceTypeId);
	IDeviceType deviceType = getDeviceType(deviceTypeId);
	if (deviceType == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}

	// Use common logic so all backend implementations work the same.
	DeviceStatus status = DeviceManagementPersistence.deviceStatusCreateLogic(deviceType, request, existing);

	MongoCollection<Document> statuses = getMongoClient().getDeviceStatusesCollection();
	Document created = MongoDeviceStatus.toDocument(status);
	MongoPersistence.insert(statuses, created, ErrorCode.DeviceStatusExists);
	return MongoDeviceStatus.fromDocument(created);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceStatusByCode(java.util.
     * UUID, java.lang.String)
     */
    @Override
    public IDeviceStatus getDeviceStatusByCode(UUID deviceTypeId, String code) throws SiteWhereException {
	Document result = getDeviceStatusDocument(deviceTypeId, code);
	if (result != null) {
	    return MongoDeviceStatus.fromDocument(result);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceStatus(java.util.UUID,
     * java.lang.String,
     * com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus updateDeviceStatus(UUID deviceTypeId, String code, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	Document match = assertDeviceStatus(deviceTypeId, code);
	DeviceStatus status = MongoDeviceStatus.fromDocument(match);

	List<IDeviceStatus> existing = listDeviceStatuses(deviceTypeId);

	// Use common update logic.
	DeviceManagementPersistence.deviceStatusUpdateLogic(request, status, existing);
	Document updated = MongoDeviceStatus.toDocument(status);

	Document query = new Document(MongoDeviceStatus.PROP_DEVICE_TYPE_ID, deviceTypeId)
		.append(MongoDeviceStatus.PROP_CODE, code);
	MongoCollection<Document> statuses = getMongoClient().getDeviceStatusesCollection();
	MongoPersistence.update(statuses, query, updated);
	return MongoDeviceStatus.fromDocument(updated);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceStatuses(java.util.UUID)
     */
    @Override
    public List<IDeviceStatus> listDeviceStatuses(UUID deviceTypeId) throws SiteWhereException {
	MongoCollection<Document> statuses = getMongoClient().getDeviceStatusesCollection();
	Document dbCriteria = new Document();
	dbCriteria.put(MongoDeviceStatus.PROP_DEVICE_TYPE_ID, deviceTypeId);
	Document sort = new Document(MongoDeviceStatus.PROP_NAME, 1);
	return MongoPersistence.list(IDeviceStatus.class, statuses, dbCriteria, sort, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceStatus(java.util.UUID,
     * java.lang.String)
     */
    @Override
    public IDeviceStatus deleteDeviceStatus(UUID deviceTypeId, String code) throws SiteWhereException {
	Document existing = assertDeviceStatus(deviceTypeId, code);
	MongoCollection<Document> statuses = getMongoClient().getDeviceStatusesCollection();
	MongoPersistence.delete(statuses, existing);
	return MongoDeviceStatus.fromDocument(existing);
    }

    /**
     * Return the {@link Document} for a device status based on device type id and
     * status code.
     * 
     * @param deviceTypeId
     * @param code
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceStatusDocument(UUID deviceTypeId, String code) throws SiteWhereException {
	MongoCollection<Document> statuses = getMongoClient().getDeviceStatusesCollection();
	Document query = new Document(MongoDeviceStatus.PROP_DEVICE_TYPE_ID, deviceTypeId)
		.append(MongoDeviceStatus.PROP_CODE, code);
	return statuses.find(query).first();
    }

    /**
     * Return the {@link Document} for the device status. Throws an exception if not
     * found.
     * 
     * @param deviceTypeId
     * @param code
     * @return
     * @throws SiteWhereException
     */
    protected Document assertDeviceStatus(UUID deviceTypeId, String code) throws SiteWhereException {
	Document match = getDeviceStatusDocument(deviceTypeId, code);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceStatusCode, ErrorLevel.ERROR);
	}
	return match;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDevice(com.sitewhere.spi
     * .device .request. IDeviceCreateRequest)
     */
    @Override
    public IDevice createDevice(IDeviceCreateRequest request) throws SiteWhereException {
	ISite site = getSiteByToken(request.getSiteToken());
	if (site == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
	}
	IDeviceType deviceType = getDeviceTypeByToken(request.getDeviceTypeToken());
	if (deviceType == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}
	Device newDevice = DeviceManagementPersistence.deviceCreateLogic(request, site, deviceType);

	// Convert and save device data.
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection();
	Document created = MongoDevice.toDocument(newDevice);
	MongoPersistence.insert(devices, created, ErrorCode.DuplicateHardwareId);

	return newDevice;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#updateDevice(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceCreateRequest)
     */
    @Override
    public IDevice updateDevice(UUID id, IDeviceCreateRequest request) throws SiteWhereException {
	Document existing = assertDevice(id);
	Device updatedDevice = MongoDevice.fromDocument(existing);

	ISite site = null;
	if (request.getSiteToken() != null) {
	    site = getSiteByToken(request.getSiteToken());
	    if (site == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
	    }
	}

	IDeviceType deviceType = null;
	if (request.getDeviceTypeToken() != null) {
	    deviceType = getDeviceTypeByToken(request.getDeviceTypeToken());
	    if (deviceType == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	    }
	}

	IDevice parent = null;
	if (request.getParentHardwareId() != null) {
	    parent = getDeviceByHardwareId(request.getParentHardwareId());
	    if (parent == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
	    }
	}

	DeviceManagementPersistence.deviceUpdateLogic(request, site, deviceType, parent, updatedDevice);
	Document updated = MongoDevice.toDocument(updatedDevice);

	MongoCollection<Document> devices = getMongoClient().getDevicesCollection();
	Document query = new Document(MongoDevice.PROP_ID, id);
	MongoPersistence.update(devices, query, updated);

	return MongoDevice.fromDocument(updated);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getDevice(java.util.UUID)
     */
    @Override
    public IDevice getDevice(UUID deviceId) throws SiteWhereException {
	Document dbDevice = getDeviceDocumentById(deviceId);
	if (dbDevice != null) {
	    return MongoDevice.fromDocument(dbDevice);
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceByHardwareId(java
     * .lang.String)
     */
    @Override
    public IDevice getDeviceByHardwareId(String hardwareId) throws SiteWhereException {
	Document dbDevice = getDeviceDocumentByHardwareId(hardwareId);
	if (dbDevice != null) {
	    return MongoDevice.fromDocument(dbDevice);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getCurrentDeviceAssignment(java.
     * util.UUID)
     */
    @Override
    public IDeviceAssignment getCurrentDeviceAssignment(UUID deviceId) throws SiteWhereException {
	IDevice device = getApiDeviceById(deviceId);
	if (device.getDeviceAssignmentId() == null) {
	    return null;
	}
	return getDeviceAssignment(device.getDeviceAssignmentId());
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
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection();
	Document dbCriteria = new Document();
	if (!includeDeleted) {
	    MongoSiteWhereEntity.setDeleted(dbCriteria, false);
	}
	if (criteria.isExcludeAssigned()) {
	    dbCriteria.put(MongoDevice.PROP_ASSIGNMENT_ID, null);
	}
	MongoPersistence.addDateSearchCriteria(dbCriteria, MongoSiteWhereEntity.PROP_CREATED_DATE, criteria);

	// Add device type filter if specified.
	if (!StringUtils.isEmpty(criteria.getDeviceTypeToken())) {
	    IDeviceType deviceType = getDeviceTypeByToken(criteria.getDeviceTypeToken());
	    dbCriteria.put(MongoDevice.PROP_DEVICE_TYPE_ID, deviceType.getId());
	}

	// Add site filter if specified.
	if (!StringUtils.isEmpty(criteria.getSiteToken())) {
	    ISite site = getSiteByToken(criteria.getSiteToken());
	    dbCriteria.put(MongoDevice.PROP_SITE_ID, site.getId());
	}

	Document sort = new Document(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IDevice.class, devices, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceElementMapping(java.
     * util.UUID, com.sitewhere.spi.device.IDeviceElementMapping)
     */
    @Override
    public IDevice createDeviceElementMapping(UUID deviceId, IDeviceElementMapping mapping) throws SiteWhereException {
	IDevice device = getApiDeviceById(deviceId);
	return DeviceManagementPersistence.deviceElementMappingCreateLogic(this, device, mapping);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceElementMapping(java.
     * util.UUID, java.lang.String)
     */
    @Override
    public IDevice deleteDeviceElementMapping(UUID deviceId, String path) throws SiteWhereException {
	IDevice device = getApiDeviceById(deviceId);
	return DeviceManagementPersistence.deviceElementMappingDeleteLogic(this, device, path);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteDevice(java.util.UUID,
     * boolean)
     */
    @Override
    public IDevice deleteDevice(UUID id, boolean force) throws SiteWhereException {
	Document existing = assertDevice(id);
	Device device = MongoDevice.fromDocument(existing);
	if (device.getDeviceAssignmentId() != null) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceCanNotBeDeletedIfAssigned, ErrorLevel.ERROR);
	}
	if (force) {
	    MongoCollection<Document> devices = getMongoClient().getDevicesCollection();
	    MongoPersistence.delete(devices, existing);
	    return MongoDevice.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoDevice.PROP_ID, id);
	    MongoCollection<Document> devices = getMongoClient().getDevicesCollection();
	    MongoPersistence.update(devices, query, existing);
	    return MongoDevice.fromDocument(existing);
	}
    }

    /**
     * Get API device object by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected IDevice getApiDeviceById(UUID id) throws SiteWhereException {
	IDevice device = getDevice(id);
	if (device == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
	}
	return device;
    }

    /**
     * Get the {@link Document} containing site information that matches the given
     * token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceDocumentByHardwareId(String hardwareId) throws SiteWhereException {
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection();
	Document query = new Document(MongoDevice.PROP_HARDWARE_ID, hardwareId);
	return devices.find(query).first();
    }

    /**
     * Get the {@link Document} containing site information that matches the given
     * id.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceDocumentById(UUID id) throws SiteWhereException {
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection();
	Document query = new Document(MongoDevice.PROP_ID, id);
	return devices.find(query).first();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#createDeviceAssignment(com.
     * sitewhere .spi.device.request. IDeviceAssignmentCreateRequest)
     */
    @Override
    public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request) throws SiteWhereException {
	IDevice existing = getDeviceByHardwareId(request.getDeviceHardwareId());
	if (existing.getDeviceAssignmentId() != null) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceAlreadyAssigned, ErrorLevel.ERROR);
	}
	Document deviceDb = assertDevice(existing.getId());

	// Use common logic to load assignment from request.
	DeviceAssignment newAssignment = DeviceManagementPersistence.deviceAssignmentCreateLogic(request, existing);
	if (newAssignment.getToken() == null) {
	    newAssignment.setToken(UUID.randomUUID().toString());
	}

	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection();
	Document created = MongoDeviceAssignment.toDocument(newAssignment);
	MongoPersistence.insert(assignments, created, ErrorCode.DuplicateDeviceAssignment);

	// Update device to point to created assignment.
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection();
	Document query = new Document(MongoDevice.PROP_HARDWARE_ID, request.getDeviceHardwareId());
	deviceDb.put(MongoDevice.PROP_ASSIGNMENT_ID, newAssignment.getId());
	MongoPersistence.update(devices, query, deviceDb);

	return newAssignment;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignment(java.util.
     * UUID)
     */
    @Override
    public IDeviceAssignment getDeviceAssignment(UUID id) throws SiteWhereException {
	Document dbAssignment = getDeviceAssignmentDocumentById(id);
	if (dbAssignment != null) {
	    return MongoDeviceAssignment.fromDocument(dbAssignment);
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentByToken
     * (java.lang.String)
     */
    @Override
    public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
	Document dbAssignment = getDeviceAssignmentDocumentByToken(token);
	if (dbAssignment != null) {
	    return MongoDeviceAssignment.fromDocument(dbAssignment);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceAssignment(java.util.
     * UUID, boolean)
     */
    @Override
    public IDeviceAssignment deleteDeviceAssignment(UUID id, boolean force) throws SiteWhereException {
	Document existing = assertDeviceAssignment(id);
	DeviceManagementPersistence.deviceAssignmentDeleteLogic(MongoDeviceAssignment.fromDocument(existing));
	if (force) {
	    MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection();
	    MongoPersistence.delete(assignments, existing);
	    return MongoDeviceAssignment.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoDeviceAssignment.PROP_ID, id);
	    MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection();
	    MongoPersistence.update(assignments, query, existing);
	    return MongoDeviceAssignment.fromDocument(existing);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentMetadata(
     * java.util.UUID, java.util.Map)
     */
    @Override
    public IDeviceAssignment updateDeviceAssignmentMetadata(UUID id, Map<String, String> metadata)
	    throws SiteWhereException {
	Document match = assertDeviceAssignment(id);
	DeviceAssignment assignment = MongoDeviceAssignment.fromDocument(match);
	for (String key : metadata.keySet()) {
	    assignment.addOrReplaceMetadata(key, metadata.get(key));
	}
	DeviceManagementPersistence.setUpdatedEntityMetadata(assignment);
	Document query = new Document(MongoDeviceAssignment.PROP_ID, id);
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection();
	MongoPersistence.update(assignments, query, MongoDeviceAssignment.toDocument(assignment));

	return MongoDeviceAssignment.fromDocument(match);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentStatus(java.
     * util.UUID, com.sitewhere.spi.device.DeviceAssignmentStatus)
     */
    @Override
    public IDeviceAssignment updateDeviceAssignmentStatus(UUID id, DeviceAssignmentStatus status)
	    throws SiteWhereException {
	Document match = assertDeviceAssignment(id);
	match.put(MongoDeviceAssignment.PROP_STATUS, status.name());
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection();
	Document query = new Document(MongoDeviceAssignment.PROP_ID, id);
	MongoPersistence.update(assignments, query, match);
	return MongoDeviceAssignment.fromDocument(match);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#endDeviceAssignment(java.util.
     * UUID)
     */
    @Override
    public IDeviceAssignment endDeviceAssignment(UUID id) throws SiteWhereException {
	Document match = assertDeviceAssignment(id);
	match.put(MongoDeviceAssignment.PROP_RELEASED_DATE, Calendar.getInstance().getTime());
	match.put(MongoDeviceAssignment.PROP_STATUS, DeviceAssignmentStatus.Released.name());
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection();
	Document query = new Document(MongoDeviceAssignment.PROP_ID, id);
	MongoPersistence.update(assignments, query, match);

	// Remove device assignment reference.
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection();
	UUID deviceId = (UUID) match.get(MongoDeviceAssignment.PROP_DEVICE_ID);
	Document deviceMatch = getDeviceDocumentById(deviceId);
	deviceMatch.put(MongoDevice.PROP_ASSIGNMENT_ID, null);
	query = new Document(MongoDeviceAssignment.PROP_ID, id);
	MongoPersistence.update(devices, query, deviceMatch);

	DeviceAssignment assignment = MongoDeviceAssignment.fromDocument(match);
	return assignment;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentHistory(java.
     * util.UUID, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public SearchResults<IDeviceAssignment> getDeviceAssignmentHistory(UUID deviceId, ISearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection();
	Document query = new Document(MongoDeviceAssignment.PROP_DEVICE_ID, deviceId);
	Document sort = new Document(MongoDeviceAssignment.PROP_ACTIVE_DATE, -1);
	return MongoPersistence.search(IDeviceAssignment.class, assignments, query, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentsForSite(java.
     * util.UUID, com.sitewhere.spi.search.device.IAssignmentSearchCriteria)
     */
    @Override
    public SearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(UUID siteId, IAssignmentSearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection();
	Document query = new Document(MongoDeviceAssignment.PROP_SITE_ID, siteId);
	if (criteria.getStatus() != null) {
	    query.append(MongoDeviceAssignment.PROP_STATUS, criteria.getStatus().name());
	}
	Document sort = new Document(MongoDeviceAssignment.PROP_ACTIVE_DATE, -1);
	return MongoPersistence.search(IDeviceAssignment.class, assignments, query, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentsForAsset(com.
     * sitewhere.spi.asset.IAssetReference,
     * com.sitewhere.spi.search.device.IAssignmentsForAssetSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForAsset(IAssetReference assetReference,
	    IAssignmentsForAssetSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection();
	Document query = new Document(MongoDeviceAssignment.PROP_ASSET_REFERENCE, assetReference);
	if (criteria.getSiteToken() != null) {
	    ISite site = getSiteByToken(criteria.getSiteToken());
	    query.append(MongoDeviceAssignment.PROP_SITE_ID, site.getId());
	}
	if (criteria.getStatus() != null) {
	    query.append(MongoDeviceAssignment.PROP_STATUS, criteria.getStatus().name());
	}
	Document sort = new Document(MongoDeviceAssignment.PROP_ACTIVE_DATE, -1);
	return MongoPersistence.search(IDeviceAssignment.class, assignments, query, sort, criteria, LOOKUP);
    }

    /**
     * Find the {@link Document} for a device assignment based on unique token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceAssignmentDocumentByToken(String token) throws SiteWhereException {
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection();
	Document query = new Document(MongoDeviceAssignment.PROP_TOKEN, token);
	return assignments.find(query).first();
    }

    /**
     * Find the {@link Document} for a device assignment based on unique id.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceAssignmentDocumentById(UUID id) throws SiteWhereException {
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection();
	Document query = new Document(MongoDeviceAssignment.PROP_ID, id);
	return assignments.find(query).first();
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceStream(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest)
     */
    @Override
    public IDeviceStream createDeviceStream(UUID assignmentId, IDeviceStreamCreateRequest request)
	    throws SiteWhereException {
	// Use common logic so all backend implementations work the same.
	IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentId);
	DeviceStream stream = DeviceManagementPersistence.deviceStreamCreateLogic(assignment, request);

	MongoCollection<Document> streams = getMongoClient().getStreamsCollection();
	Document created = MongoDeviceStream.toDocument(stream);
	MongoPersistence.insert(streams, created, ErrorCode.DuplicateStreamId);
	return MongoDeviceStream.fromDocument(created);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceStream(java.util.UUID,
     * java.lang.String)
     */
    @Override
    public IDeviceStream getDeviceStream(UUID assignmentId, String streamId) throws SiteWhereException {
	Document dbStream = getDeviceStreamDocument(assignmentId, streamId);
	if (dbStream == null) {
	    return null;
	}
	return MongoDeviceStream.fromDocument(dbStream);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceStreams(java.util.UUID,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStream> listDeviceStreams(UUID assignmentId, ISearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> streams = getMongoClient().getStreamsCollection();
	Document query = new Document(MongoDeviceStream.PROP_ASSIGNMENT_ID, assignmentId);
	Document sort = new Document(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IDeviceStream.class, streams, query, sort, criteria, LOOKUP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#createSite(com.sitewhere.spi.
     * device. request.ISiteCreateRequest )
     */
    @Override
    public ISite createSite(ISiteCreateRequest request) throws SiteWhereException {
	// Use common logic so all backend implementations work the same.
	Site site = DeviceManagementPersistence.siteCreateLogic(request);

	MongoCollection<Document> sites = getMongoClient().getSitesCollection();
	Document created = MongoSite.toDocument(site);
	MongoPersistence.insert(sites, created, ErrorCode.DeuplicateSiteToken);
	return MongoSite.fromDocument(created);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#updateSite(java.util.UUID,
     * com.sitewhere.spi.device.request.ISiteCreateRequest)
     */
    @Override
    public ISite updateSite(UUID id, ISiteCreateRequest request) throws SiteWhereException {
	ISite site = getApiSiteById(id);

	// Use common update logic.
	DeviceManagementPersistence.siteUpdateLogic(request, (Site) site);

	Document updated = MongoSite.toDocument(site);

	MongoCollection<Document> sites = getMongoClient().getSitesCollection();
	Document query = new Document(MongoSite.PROP_ID, id);
	MongoPersistence.update(sites, query, updated);
	return MongoSite.fromDocument(updated);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getSite(java.util.UUID)
     */
    @Override
    public ISite getSite(UUID id) throws SiteWhereException {
	Document dbSite = getSiteDocumentById(id);
	if (dbSite != null) {
	    return MongoSite.fromDocument(dbSite);
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getSiteByToken(java.lang.
     * String )
     */
    @Override
    public ISite getSiteByToken(String token) throws SiteWhereException {
	Document dbSite = getSiteDocumentByToken(token);
	if (dbSite != null) {
	    return MongoSite.fromDocument(dbSite);
	}
	return null;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteSite(java.util.UUID,
     * boolean)
     */
    @Override
    public ISite deleteSite(UUID id, boolean force) throws SiteWhereException {
	Document existing = getSiteDocumentById(id);
	if (existing == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
	}
	if (force) {
	    MongoCollection<Document> sites = getMongoClient().getSitesCollection();
	    MongoPersistence.delete(sites, existing);
	    return MongoSite.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoSite.PROP_ID, id);
	    MongoCollection<Document> sites = getMongoClient().getSitesCollection();
	    MongoPersistence.update(sites, query, existing);
	    return MongoSite.fromDocument(existing);
	}
    }

    /**
     * Get the DBObject containing site information that matches the given token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getSiteDocumentByToken(String token) throws SiteWhereException {
	MongoCollection<Document> sites = getMongoClient().getSitesCollection();
	Document query = new Document(MongoSite.PROP_TOKEN, token);
	return sites.find(query).first();
    }

    /**
     * Get the DBObject containing site information that matches the given id.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getSiteDocumentById(UUID id) throws SiteWhereException {
	MongoCollection<Document> sites = getMongoClient().getSitesCollection();
	Document query = new Document(MongoSite.PROP_ID, id);
	return sites.find(query).first();
    }

    /**
     * Get API site based on unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected ISite getApiSiteById(UUID id) throws SiteWhereException {
	Document match = getSiteDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
	}
	return MongoSite.fromDocument(match);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#listSites(com.sitewhere.spi.
     * common. ISearchCriteria)
     */
    @Override
    public SearchResults<ISite> listSites(ISearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> sites = getMongoClient().getSitesCollection();
	Document query = new Document();
	Document sort = new Document(MongoSite.PROP_NAME, 1);
	return MongoPersistence.search(ISite.class, sites, query, sort, criteria, LOOKUP);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#createZone(java.util.UUID,
     * com.sitewhere.spi.device.request.IZoneCreateRequest)
     */
    @Override
    public IZone createZone(UUID siteId, IZoneCreateRequest request) throws SiteWhereException {
	ISite site = assertApiSite(siteId);
	Zone zone = DeviceManagementPersistence.zoneCreateLogic(request, site, UUID.randomUUID().toString());

	MongoCollection<Document> zones = getMongoClient().getZonesCollection();
	Document created = MongoZone.toDocument(zone);
	MongoPersistence.insert(zones, created, ErrorCode.DuplicateZoneToken);
	return MongoZone.fromDocument(created);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#updateZone(java.util.UUID,
     * com.sitewhere.spi.device.request.IZoneCreateRequest)
     */
    @Override
    public IZone updateZone(UUID id, IZoneCreateRequest request) throws SiteWhereException {
	MongoCollection<Document> zones = getMongoClient().getZonesCollection();
	Document match = assertZone(id);

	Zone zone = MongoZone.fromDocument(match);
	DeviceManagementPersistence.zoneUpdateLogic(request, zone);

	Document updated = MongoZone.toDocument(zone);

	Document query = new Document(MongoSite.PROP_ID, id);
	MongoPersistence.update(zones, query, updated);
	return MongoZone.fromDocument(updated);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getZone(java.util.UUID)
     */
    @Override
    public IZone getZone(UUID id) throws SiteWhereException {
	Document dbZone = getZoneDocumentById(id);
	if (dbZone != null) {
	    return MongoZone.fromDocument(dbZone);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getZoneByToken(java.lang.String)
     */
    @Override
    public IZone getZoneByToken(String zoneToken) throws SiteWhereException {
	Document dbZone = getZoneDocumentByToken(zoneToken);
	if (dbZone != null) {
	    return MongoZone.fromDocument(dbZone);
	}
	return null;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#listZones(java.util.UUID,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public SearchResults<IZone> listZones(UUID siteId, ISearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> zones = getMongoClient().getZonesCollection();
	Document query = new Document(MongoZone.PROP_SITE_ID, siteId);
	Document sort = new Document(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IZone.class, zones, query, sort, criteria, LOOKUP);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteZone(java.util.UUID,
     * boolean)
     */
    @Override
    public IZone deleteZone(UUID id, boolean force) throws SiteWhereException {
	Document existing = assertZone(id);
	if (force) {
	    MongoCollection<Document> zones = getMongoClient().getZonesCollection();
	    MongoPersistence.delete(zones, existing);
	    return MongoZone.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoZone.PROP_ID, id);
	    MongoCollection<Document> zones = getMongoClient().getZonesCollection();
	    MongoPersistence.update(zones, query, existing);
	    return MongoZone.fromDocument(existing);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#createDeviceGroup(com.
     * sitewhere.spi. device.request.IDeviceGroupCreateRequest)
     */
    @Override
    public IDeviceGroup createDeviceGroup(IDeviceGroupCreateRequest request) throws SiteWhereException {
	String uuid = ((request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString());
	DeviceGroup group = DeviceManagementPersistence.deviceGroupCreateLogic(request, uuid);

	MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection();
	Document created = MongoDeviceGroup.toDocument(group);
	created.put(MongoDeviceGroup.PROP_LAST_INDEX, new Long(0));

	MongoPersistence.insert(groups, created, ErrorCode.DuplicateDeviceGroupToken);
	return MongoDeviceGroup.fromDocument(created);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceGroup(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceGroupCreateRequest)
     */
    @Override
    public IDeviceGroup updateDeviceGroup(UUID id, IDeviceGroupCreateRequest request) throws SiteWhereException {
	MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection();
	Document match = assertDeviceGroup(id);

	DeviceGroup group = MongoDeviceGroup.fromDocument(match);
	DeviceManagementPersistence.deviceGroupUpdateLogic(request, group);

	Document updated = MongoDeviceGroup.toDocument(group);

	// Manually copy last index since it's not copied by default.
	updated.put(MongoDeviceGroup.PROP_LAST_INDEX, match.get(MongoDeviceGroup.PROP_LAST_INDEX));

	Document query = new Document(MongoDeviceGroup.PROP_ID, id);
	MongoPersistence.update(groups, query, updated);
	return MongoDeviceGroup.fromDocument(updated);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceGroup(java.util.UUID)
     */
    @Override
    public IDeviceGroup getDeviceGroup(UUID id) throws SiteWhereException {
	Document found = getDeviceGroupDocumentById(id);
	if (found != null) {
	    return MongoDeviceGroup.fromDocument(found);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceGroupByToken(java.lang.
     * String)
     */
    @Override
    public IDeviceGroup getDeviceGroupByToken(String token) throws SiteWhereException {
	Document found = getDeviceGroupDocumentByToken(token);
	if (found != null) {
	    return MongoDeviceGroup.fromDocument(found);
	}
	return null;
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
	MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection();
	Document dbCriteria = new Document();
	if (!includeDeleted) {
	    MongoSiteWhereEntity.setDeleted(dbCriteria, false);
	}
	Document sort = new Document(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IDeviceGroup.class, groups, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroupsWithRole(java.
     * lang. String , boolean, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroupsWithRole(String role, boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection();
	Document dbCriteria = new Document(MongoDeviceGroup.PROP_ROLES, role);
	if (!includeDeleted) {
	    MongoSiteWhereEntity.setDeleted(dbCriteria, false);
	}
	Document sort = new Document(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IDeviceGroup.class, groups, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceGroup(java.util.UUID,
     * boolean)
     */
    @Override
    public IDeviceGroup deleteDeviceGroup(UUID id, boolean force) throws SiteWhereException {
	Document existing = assertDeviceGroup(id);
	if (force) {
	    MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection();
	    MongoPersistence.delete(groups, existing);

	    // Delete group elements as well.
	    MongoCollection<Document> elements = getMongoClient().getGroupElementsCollection();
	    Document match = new Document(MongoDeviceGroupElement.PROP_GROUP_ID, id);
	    MongoPersistence.delete(elements, match);

	    return MongoDeviceGroup.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoDeviceGroup.PROP_ID, id);
	    MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection();
	    MongoPersistence.update(groups, query, existing);
	    return MongoDeviceGroup.fromDocument(existing);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#addDeviceGroupElements(java.util.
     * UUID, java.util.List, boolean)
     */
    @Override
    public List<IDeviceGroupElement> addDeviceGroupElements(UUID groupId,
	    List<IDeviceGroupElementCreateRequest> elements, boolean ignoreDuplicates) throws SiteWhereException {
	Document existing = assertDeviceGroup(groupId);
	IDeviceGroup group = MongoDeviceGroup.fromDocument(existing);
	List<IDeviceGroupElement> results = new ArrayList<IDeviceGroupElement>();
	for (IDeviceGroupElementCreateRequest request : elements) {
	    long index = MongoDeviceGroup.getNextGroupIndex(getMongoClient(), getTenantEngine().getTenant(), groupId);
	    UUID elementId;
	    switch (request.getType()) {
	    case Device: {
		IDevice elementDevice = getDeviceByHardwareId(request.getElementId());
		elementId = elementDevice.getId();
		break;
	    }
	    case Group: {
		IDeviceGroup elementGroup = getDeviceGroupByToken(request.getElementId());
		elementId = elementGroup.getId();
		break;
	    }
	    default: {
		elementId = null;
	    }
	    }
	    if (elementId != null) {
		DeviceGroupElement element = DeviceManagementPersistence.deviceGroupElementCreateLogic(request, group,
			index, elementId);
		Document created = MongoDeviceGroupElement.toDocument(element);
		try {
		    MongoPersistence.insert(getMongoClient().getGroupElementsCollection(), created,
			    ErrorCode.DuplicateId);
		    results.add(MongoDeviceGroupElement.fromDocument(created));
		} catch (ResourceExistsException e) {
		    if (!ignoreDuplicates) {
			throw e;
		    }
		}
	    }
	}
	return results;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#removeDeviceGroupElements(java.
     * util.UUID, java.util.List)
     */
    @Override
    public List<IDeviceGroupElement> removeDeviceGroupElements(UUID groupId,
	    List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
	List<IDeviceGroupElement> deleted = new ArrayList<IDeviceGroupElement>();
	for (IDeviceGroupElementCreateRequest request : elements) {
	    Document match = new Document(MongoDeviceGroupElement.PROP_GROUP_ID, groupId)
		    .append(MongoDeviceGroupElement.PROP_TYPE, request.getType().name())
		    .append(MongoDeviceGroupElement.PROP_ELEMENT_ID, request.getElementId());
	    FindIterable<Document> found = getMongoClient().getGroupElementsCollection().find(match);
	    MongoCursor<Document> cursor = found.iterator();

	    while (cursor.hasNext()) {
		Document current = cursor.next();
		DeleteResult result = MongoPersistence.delete(getMongoClient().getGroupElementsCollection(), current);
		if (result.getDeletedCount() > 0) {
		    deleted.add(MongoDeviceGroupElement.fromDocument(current));
		}
	    }
	}
	return deleted;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroupElements(java.util.
     * UUID, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public SearchResults<IDeviceGroupElement> listDeviceGroupElements(UUID groupId, ISearchCriteria criteria)
	    throws SiteWhereException {
	Document match = new Document(MongoDeviceGroupElement.PROP_GROUP_ID, groupId);
	Document sort = new Document(MongoDeviceGroupElement.PROP_INDEX, 1);
	return MongoPersistence.search(IDeviceGroupElement.class, getMongoClient().getGroupElementsCollection(), match,
		sort, criteria, LOOKUP);
    }

    /**
     * Return the {@link Document} for the site with the given token. Throws an
     * exception if the token is not found.
     * 
     * @param hardwareId
     * @return
     * @throws SiteWhereException
     */
    protected Document assertSite(String token) throws SiteWhereException {
	Document match = getSiteDocumentByToken(token);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.INFO);
	}
	return match;
    }

    /**
     * Return the API site if found. Throw an exception if not.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected ISite assertApiSite(UUID id) throws SiteWhereException {
	Document match = getSiteDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.INFO);
	}
	return MongoSite.fromDocument(match);
    }

    /**
     * Return the {@link Document} for the device with the given hardware id. Throws
     * an exception if the hardware id is not found.
     * 
     * @param hardwareId
     * @return
     * @throws SiteWhereException
     */
    protected Document assertDevice(UUID id) throws SiteWhereException {
	Document match = getDeviceDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.INFO);
	}
	return match;
    }

    /**
     * Return the {@link Document} for the assignment with the given token. Throws
     * an exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document assertDeviceAssignment(UUID id) throws SiteWhereException {
	Document match = getDeviceAssignmentDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Return an {@link IDeviceAssignment} for the given token. Throws an exception
     * if the id is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment assertApiDeviceAssignment(UUID id) throws SiteWhereException {
	Document match = assertDeviceAssignment(id);
	return MongoDeviceAssignment.fromDocument(match);
    }

    /**
     * Return the {@link Document} for the zone with the given token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getZoneDocumentByToken(String token) throws SiteWhereException {
	try {
	    MongoCollection<Document> zones = getMongoClient().getZonesCollection();
	    Document query = new Document(MongoZone.PROP_TOKEN, token);
	    return zones.find(query).first();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    /**
     * Return the {@link Document} for the zone with the given id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected Document getZoneDocumentById(UUID id) throws SiteWhereException {
	try {
	    MongoCollection<Document> zones = getMongoClient().getZonesCollection();
	    Document query = new Document(MongoZone.PROP_ID, id);
	    return zones.find(query).first();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    /**
     * Get API zone by unique id or throw exception if not found.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected IZone assertApiZone(UUID id) throws SiteWhereException {
	Document match = getZoneDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidZoneToken, ErrorLevel.ERROR);
	}
	return MongoZone.fromDocument(match);
    }

    /**
     * Return the {@link Document} for the zone with the given token. Throws an
     * exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document assertZone(String token) throws SiteWhereException {
	Document match = getZoneDocumentByToken(token);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidZoneToken, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Return the {@link Document} for the zone with the given id. Throws an
     * exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document assertZone(UUID id) throws SiteWhereException {
	Document match = getZoneDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidZoneToken, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Get the {@link Document} for an {@link IDeviceStream} based on assignment
     * token and stream id.
     * 
     * @param assignmentToken
     * @param streamId
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceStreamDocument(UUID assignmentId, String streamId) throws SiteWhereException {
	try {
	    MongoCollection<Document> streams = getMongoClient().getStreamsCollection();
	    Document query = new Document(MongoDeviceStream.PROP_ASSIGNMENT_ID, assignmentId)
		    .append(MongoDeviceStream.PROP_STREAM_ID, streamId);
	    return streams.find(query).first();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    /**
     * Returns the {@link Document} for the device group with the given token.
     * Returns null if not found.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceGroupDocumentByToken(String token) throws SiteWhereException {
	try {
	    MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection();
	    Document query = new Document(MongoDeviceGroup.PROP_TOKEN, token);
	    return groups.find(query).first();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    protected Document getDeviceGroupDocumentById(UUID id) throws SiteWhereException {
	try {
	    MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection();
	    Document query = new Document(MongoDeviceGroup.PROP_ID, id);
	    return groups.find(query).first();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    /**
     * Return the {@link Document} for the device group with the given token. Throws
     * an exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document assertDeviceGroup(String token) throws SiteWhereException {
	Document match = getDeviceGroupDocumentByToken(token);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceGroupToken, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Get device group document by id or throw exception if not found.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected Document assertDeviceGroup(UUID id) throws SiteWhereException {
	Document match = getDeviceGroupDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceGroupToken, ErrorLevel.ERROR);
	}
	return match;
    }

    public IDeviceManagementMongoClient getMongoClient() {
	return mongoClient;
    }

    public void setMongoClient(IDeviceManagementMongoClient mongoClient) {
	this.mongoClient = mongoClient;
    }
}