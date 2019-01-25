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
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bson.Document;

import com.mongodb.MongoClientException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.DeleteResult;
import com.sitewhere.common.MarshalUtils;
import com.sitewhere.device.microservice.DeviceManagementMicroservice;
import com.sitewhere.device.persistence.DeviceManagementPersistence;
import com.sitewhere.mongodb.IMongoConverterLookup;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.mongodb.MongoTenantComponent;
import com.sitewhere.mongodb.common.MongoPersistentEntity;
import com.sitewhere.rest.model.area.Area;
import com.sitewhere.rest.model.area.AreaType;
import com.sitewhere.rest.model.area.Zone;
import com.sitewhere.rest.model.customer.Customer;
import com.sitewhere.rest.model.customer.CustomerType;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAlarm;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceStatus;
import com.sitewhere.rest.model.device.DeviceType;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.DeviceCommandSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceStatusSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.area.request.IAreaCreateRequest;
import com.sitewhere.spi.area.request.IAreaTypeCreateRequest;
import com.sitewhere.spi.area.request.IZoneCreateRequest;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.customer.ICustomerType;
import com.sitewhere.spi.customer.request.ICustomerCreateRequest;
import com.sitewhere.spi.customer.request.ICustomerTypeCreateRequest;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAlarm;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceAlarmCreateRequest;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCommandCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.device.request.IDeviceStatusCreateRequest;
import com.sitewhere.spi.device.request.IDeviceTypeCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.error.ResourceExistsException;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.area.IAreaSearchCriteria;
import com.sitewhere.spi.search.customer.ICustomerSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceAlarmSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceAssignmentSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceCommandSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceStatusSearchCriteria;
import com.sitewhere.spi.search.device.IZoneSearchCriteria;
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
public class MongoDeviceManagement extends MongoTenantComponent<DeviceManagementMongoClient>
	implements IDeviceManagement {

    /** Converter lookup */
    private static IMongoConverterLookup LOOKUP = new MongoConverters();

    /** Injected with global SiteWhere Mongo client */
    private DeviceManagementMongoClient mongoClient;

    public MongoDeviceManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * @see com.sitewhere.mongodb.MongoTenantComponent#ensureIndexes()
     */
    @Override
    public void ensureIndexes() throws SiteWhereException {
	// Area indexes.
	getMongoClient().getAreasCollection().createIndex(new Document(MongoPersistentEntity.PROP_TOKEN, 1),
		new IndexOptions().unique(true));

	// Device-type-related indexes.
	getMongoClient().getDeviceTypesCollection().createIndex(new Document(MongoPersistentEntity.PROP_TOKEN, 1),
		new IndexOptions().unique(true));
	getMongoClient().getDeviceStatusesCollection().createIndex(
		new Document(MongoDeviceStatus.PROP_DEVICE_TYPE_ID, 1).append(MongoDeviceStatus.PROP_CODE, 1),
		new IndexOptions().unique(true));

	// Devices.
	getMongoClient().getDevicesCollection().createIndex(new Document(MongoPersistentEntity.PROP_TOKEN, 1),
		new IndexOptions().unique(true));

	// Device assignments.
	getMongoClient().getDeviceAssignmentsCollection().createIndex(new Document(MongoPersistentEntity.PROP_TOKEN, 1),
		new IndexOptions().unique(true));
	getMongoClient().getDeviceAssignmentsCollection()
		.createIndex(new Document(MongoDeviceAssignment.PROP_AREA_ID, 1)
			.append(MongoDeviceAssignment.PROP_ASSET_ID, 1).append(MongoDeviceAssignment.PROP_STATUS, 1));

	// Device group indexes.
	getMongoClient().getDeviceGroupsCollection().createIndex(new Document(MongoPersistentEntity.PROP_TOKEN, 1),
		new IndexOptions().unique(true));
	getMongoClient().getDeviceGroupsCollection().createIndex(new Document(MongoDeviceGroup.PROP_ROLES, 1));

	// Device group element indexes.
	getMongoClient().getGroupElementsCollection().createIndex(new Document(MongoDeviceGroupElement.PROP_GROUP_ID, 1)
		.append(MongoDeviceGroupElement.PROP_DEVICE_ID, 1), new IndexOptions().unique(true));
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
	// Use common logic so all backend implementations work the same.
	DeviceType deviceType = DeviceManagementPersistence.deviceTypeCreateLogic(request);

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

	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
	MongoCollection<Document> types = getMongoClient().getDeviceTypesCollection();
	MongoPersistence.update(types, query, updated);

	return MongoDeviceType.fromDocument(updated);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceTypes(com.sitewhere.spi.
     * search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceType> listDeviceTypes(ISearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> types = getMongoClient().getDeviceTypesCollection();
	Document dbCriteria = new Document();
	Document sort = new Document(MongoPersistentEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IDeviceType.class, types, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceType(java.util.UUID)
     */
    @Override
    public IDeviceType deleteDeviceType(UUID id) throws SiteWhereException {
	Document existing = assertDeviceType(id);

	// Verify that device type can be deleted.
	IDeviceType deviceType = MongoDeviceType.fromDocument(existing);
	DeviceManagementPersistence.deviceTypeDeleteLogic(deviceType, this);

	MongoCollection<Document> types = getMongoClient().getDeviceTypesCollection();
	MongoPersistence.delete(types, existing);
	return MongoDeviceType.fromDocument(existing);
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
	Document query = new Document(MongoPersistentEntity.PROP_TOKEN, token);
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
	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
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
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceCommand(com.sitewhere.
     * spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public IDeviceCommand createDeviceCommand(IDeviceCommandCreateRequest request) throws SiteWhereException {
	// Validate device type token passed.
	if (request.getDeviceTypeToken() == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}
	IDeviceType deviceType = getDeviceTypeByToken(request.getDeviceTypeToken());
	if (deviceType == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}

	DeviceCommandSearchCriteria criteria = new DeviceCommandSearchCriteria(1, 0);
	criteria.setDeviceTypeId(deviceType.getId());
	ISearchResults<IDeviceCommand> existing = listDeviceCommands(criteria);

	// Use common logic so all backend implementations work the same.
	DeviceCommand command = DeviceManagementPersistence.deviceCommandCreateLogic(deviceType, request,
		existing.getResults());

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

	// Validate device type token passed.
	IDeviceType deviceType = null;
	if (request.getDeviceTypeToken() != null) {
	    deviceType = getDeviceTypeByToken(request.getDeviceTypeToken());
	    if (deviceType == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	    }
	} else {
	    deviceType = getDeviceType(command.getDeviceTypeId());
	}

	DeviceCommandSearchCriteria criteria = new DeviceCommandSearchCriteria(1, 0);
	criteria.setDeviceTypeId(deviceType.getId());
	ISearchResults<IDeviceCommand> existing = listDeviceCommands(criteria);

	// Use common update logic.
	DeviceManagementPersistence.deviceCommandUpdateLogic(deviceType, request, command, existing.getResults());
	Document updated = MongoDeviceCommand.toDocument(command);

	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
	MongoCollection<Document> commands = getMongoClient().getDeviceCommandsCollection();
	MongoPersistence.update(commands, query, updated);
	return MongoDeviceCommand.fromDocument(updated);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommands(com.sitewhere.
     * spi.search.device.IDeviceCommandSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommand> listDeviceCommands(IDeviceCommandSearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> commands = getMongoClient().getDeviceCommandsCollection();
	Document dbCriteria = new Document();
	if (criteria.getDeviceTypeId() != null) {
	    dbCriteria.put(MongoDeviceCommand.PROP_DEVICE_TYPE_ID, criteria.getDeviceTypeId());
	}
	Document sort = new Document(MongoDeviceCommand.PROP_NAME, 1);
	return MongoPersistence.search(IDeviceCommand.class, commands, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceCommand(java.util.
     * UUID)
     */
    @Override
    public IDeviceCommand deleteDeviceCommand(UUID id) throws SiteWhereException {
	Document existing = assertDeviceCommand(id);
	MongoCollection<Document> commands = getMongoClient().getDeviceCommandsCollection();
	MongoPersistence.delete(commands, existing);
	return MongoDeviceCommand.fromDocument(existing);
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
	Document query = new Document(MongoPersistentEntity.PROP_TOKEN, token);
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
	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
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
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceStatus(com.sitewhere.
     * spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus createDeviceStatus(IDeviceStatusCreateRequest request) throws SiteWhereException {
	// Validate device type token passed.
	if (request.getDeviceTypeToken() == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}
	IDeviceType deviceType = getDeviceTypeByToken(request.getDeviceTypeToken());
	if (deviceType == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}

	DeviceStatusSearchCriteria criteria = new DeviceStatusSearchCriteria(1, 0);
	criteria.setDeviceTypeId(deviceType.getId());
	ISearchResults<IDeviceStatus> existing = listDeviceStatuses(criteria);

	// Use common logic so all backend implementations work the same.
	DeviceStatus status = DeviceManagementPersistence.deviceStatusCreateLogic(deviceType, request,
		existing.getResults());

	MongoCollection<Document> statuses = getMongoClient().getDeviceStatusesCollection();
	Document created = MongoDeviceStatus.toDocument(status);
	MongoPersistence.insert(statuses, created, ErrorCode.DeviceStatusExists);
	return MongoDeviceStatus.fromDocument(created);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceStatus(java.util.UUID)
     */
    @Override
    public IDeviceStatus getDeviceStatus(UUID id) throws SiteWhereException {
	Document result = getDeviceStatusDocumentById(id);
	if (result != null) {
	    return MongoDeviceStatus.fromDocument(result);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceStatusByToken(java.lang.
     * String)
     */
    @Override
    public IDeviceStatus getDeviceStatusByToken(String token) throws SiteWhereException {
	Document result = getDeviceStatusDocumentByToken(token);
	if (result != null) {
	    return MongoDeviceStatus.fromDocument(result);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceStatus(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus updateDeviceStatus(UUID id, IDeviceStatusCreateRequest request) throws SiteWhereException {
	Document match = assertDeviceStatus(id);
	DeviceStatus status = MongoDeviceStatus.fromDocument(match);

	// Validate device type token passed.
	IDeviceType deviceType = null;
	if (request.getDeviceTypeToken() != null) {
	    deviceType = getDeviceTypeByToken(request.getDeviceTypeToken());
	    if (deviceType == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	    }
	} else {
	    deviceType = getDeviceType(status.getDeviceTypeId());
	}

	DeviceStatusSearchCriteria criteria = new DeviceStatusSearchCriteria(1, 0);
	criteria.setDeviceTypeId(deviceType.getId());
	ISearchResults<IDeviceStatus> existing = listDeviceStatuses(criteria);

	// Use common update logic.
	DeviceManagementPersistence.deviceStatusUpdateLogic(deviceType, request, status, existing.getResults());
	Document updated = MongoDeviceStatus.toDocument(status);

	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
	MongoCollection<Document> statuses = getMongoClient().getDeviceStatusesCollection();
	MongoPersistence.update(statuses, query, updated);
	return MongoDeviceStatus.fromDocument(updated);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceStatuses(com.sitewhere.
     * spi.search.device.IDeviceStatusSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStatus> listDeviceStatuses(IDeviceStatusSearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> statuses = getMongoClient().getDeviceStatusesCollection();
	Document dbCriteria = new Document();
	if (criteria.getDeviceTypeId() != null) {
	    dbCriteria.put(MongoDeviceStatus.PROP_DEVICE_TYPE_ID, criteria.getDeviceTypeId());
	}
	if (criteria.getCode() != null) {
	    dbCriteria.put(MongoDeviceStatus.PROP_CODE, criteria.getCode());
	}
	Document sort = new Document(MongoDeviceStatus.PROP_NAME, 1);
	return MongoPersistence.search(IDeviceStatus.class, statuses, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceStatus(java.util.UUID)
     */
    @Override
    public IDeviceStatus deleteDeviceStatus(UUID id) throws SiteWhereException {
	Document existing = assertDeviceStatus(id);
	MongoCollection<Document> statuses = getMongoClient().getDeviceStatusesCollection();
	MongoPersistence.delete(statuses, existing);
	return MongoDeviceStatus.fromDocument(existing);
    }

    /**
     * Return the {@link Document} for the device status with the given token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceStatusDocumentByToken(String token) throws SiteWhereException {
	MongoCollection<Document> statuses = getMongoClient().getDeviceStatusesCollection();
	Document query = new Document(MongoPersistentEntity.PROP_TOKEN, token);
	return statuses.find(query).first();
    }

    /**
     * Return the {@link Document} for the device status with the given id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceStatusDocumentById(UUID id) throws SiteWhereException {
	MongoCollection<Document> statuses = getMongoClient().getDeviceStatusesCollection();
	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
	return statuses.find(query).first();
    }

    /**
     * Return the {@link Document} for the device status. Throws an exception if not
     * found.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected Document assertDeviceStatus(UUID id) throws SiteWhereException {
	Document match = getDeviceStatusDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceStatusId, ErrorLevel.ERROR);
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
	IDeviceType deviceType = getDeviceTypeByToken(request.getDeviceTypeToken());
	if (deviceType == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}
	Device newDevice = DeviceManagementPersistence.deviceCreateLogic(request, deviceType);

	// Convert and save device data.
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection();
	Document created = MongoDevice.toDocument(newDevice);
	MongoPersistence.insert(devices, created, ErrorCode.DuplicateDeviceToken);

	return newDevice;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#updateDevice(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceCreateRequest)
     */
    @Override
    public IDevice updateDevice(UUID id, IDeviceCreateRequest request) throws SiteWhereException {

	getLogger().info("Request:\n\n" + MarshalUtils.marshalJsonAsPrettyString(request));

	Document existing = assertDevice(id);
	Device updatedDevice = MongoDevice.fromDocument(existing);

	IDeviceType deviceType = null;
	if (request.getDeviceTypeToken() != null) {
	    deviceType = getDeviceTypeByToken(request.getDeviceTypeToken());
	    if (deviceType == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	    }
	}

	IDevice parent = null;
	if (request.getParentDeviceToken() != null) {
	    parent = getDeviceByToken(request.getParentDeviceToken());
	    if (parent == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidDeviceToken, ErrorLevel.ERROR);
	    }
	}

	DeviceManagementPersistence.deviceUpdateLogic(request, deviceType, parent, updatedDevice);
	getLogger().info("Updated:\n\n" + MarshalUtils.marshalJsonAsPrettyString(updatedDevice));
	Document updated = MongoDevice.toDocument(updatedDevice);

	MongoCollection<Document> devices = getMongoClient().getDevicesCollection();
	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceByToken(java.lang.String)
     */
    @Override
    public IDevice getDeviceByToken(String token) throws SiteWhereException {
	Document dbDevice = getDeviceDocumentByToken(token);
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDevices(com.sitewhere.spi.
     * search.device.IDeviceSearchCriteria)
     */
    @Override
    public SearchResults<IDevice> listDevices(IDeviceSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection();
	Document dbCriteria = new Document();
	if (criteria.isExcludeAssigned()) {
	    dbCriteria.put(MongoDevice.PROP_ASSIGNMENT_ID, null);
	}
	MongoPersistence.addDateSearchCriteria(dbCriteria, MongoPersistentEntity.PROP_CREATED_DATE, criteria);

	// Add device type filter if specified.
	if (!StringUtils.isEmpty(criteria.getDeviceTypeToken())) {
	    IDeviceType deviceType = getDeviceTypeByToken(criteria.getDeviceTypeToken());
	    dbCriteria.put(MongoDevice.PROP_DEVICE_TYPE_ID, deviceType.getId());
	}

	Document sort = new Document(MongoPersistentEntity.PROP_CREATED_DATE, -1);
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
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteDevice(java.util.UUID)
     */
    @Override
    public IDevice deleteDevice(UUID id) throws SiteWhereException {
	Document existing = assertDevice(id);
	Device device = MongoDevice.fromDocument(existing);
	DeviceManagementPersistence.deviceDeleteLogic(device, this);
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection();
	MongoPersistence.delete(devices, existing);
	return MongoDevice.fromDocument(existing);
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
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceId, ErrorLevel.ERROR);
	}
	return device;
    }

    /**
     * Get the {@link Document} containing device information that matches the given
     * token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceDocumentByToken(String token) throws SiteWhereException {
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection();
	Document query = new Document(MongoPersistentEntity.PROP_TOKEN, token);
	return devices.find(query).first();
    }

    /**
     * Get the {@link Document} containing device information that matches the given
     * id.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceDocumentById(UUID id) throws SiteWhereException {
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection();
	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
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
	// Verify device is not already assigned.
	IDevice existing = getDeviceByToken(request.getDeviceToken());
	if (existing.getDeviceAssignmentId() != null) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceAlreadyAssigned, ErrorLevel.ERROR);
	}
	Document deviceDb = assertDevice(existing.getId());

	// Look up customer if specified.
	ICustomer customer = null;
	if (request.getCustomerToken() != null) {
	    customer = getCustomerByToken(request.getCustomerToken());
	    if (customer == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidCustomerToken, ErrorLevel.ERROR);
	    }
	}

	// Look up area if specified.
	IArea area = null;
	if (request.getAreaToken() != null) {
	    area = getAreaByToken(request.getAreaToken());
	    if (area == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
	    }
	}

	// Look up asset if specified.
	IAsset asset = null;
	if (request.getAssetToken() != null) {
	    asset = getAssetManagement().getAssetByToken(request.getAssetToken());
	    if (asset == null) {
		getLogger().warn("Assignment references invalid asset token: " + request.getAssetToken());
		throw new SiteWhereSystemException(ErrorCode.InvalidAssetToken, ErrorLevel.ERROR);
	    }
	}

	// Use common logic to load assignment from request.
	DeviceAssignment newAssignment = DeviceManagementPersistence.deviceAssignmentCreateLogic(request, customer,
		area, asset, existing);
	if (newAssignment.getToken() == null) {
	    newAssignment.setToken(UUID.randomUUID().toString());
	}

	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection();
	Document created = MongoDeviceAssignment.toDocument(newAssignment);
	MongoPersistence.insert(assignments, created, ErrorCode.DuplicateDeviceAssignment);

	// Update device to point to created assignment.
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection();
	Document query = new Document(MongoPersistentEntity.PROP_TOKEN, request.getDeviceToken());
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
     * UUID)
     */
    @Override
    public IDeviceAssignment deleteDeviceAssignment(UUID id) throws SiteWhereException {
	Document existing = assertDeviceAssignment(id);
	DeviceManagementPersistence.deviceAssignmentDeleteLogic(MongoDeviceAssignment.fromDocument(existing));
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection();
	MongoPersistence.delete(assignments, existing);
	return MongoDeviceAssignment.fromDocument(existing);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceAlarm(com.sitewhere.
     * spi.device.request.IDeviceAlarmCreateRequest)
     */
    @Override
    public IDeviceAlarm createDeviceAlarm(IDeviceAlarmCreateRequest request) throws SiteWhereException {
	IDeviceAssignment assignment = getDeviceAssignmentByToken(request.getDeviceAssignmentToken());
	if (assignment == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
	}

	// Use common logic to load alarm from request.
	DeviceAlarm newAlarm = DeviceManagementPersistence.deviceAlarmCreateLogic(assignment, request);

	MongoCollection<Document> alarms = getMongoClient().getDeviceAlarmsCollection();
	Document created = MongoDeviceAlarm.toDocument(newAlarm);
	MongoPersistence.insert(alarms, created, ErrorCode.DuplicateId);

	return newAlarm;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAlarm(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceAlarmCreateRequest)
     */
    @Override
    public IDeviceAlarm updateDeviceAlarm(UUID id, IDeviceAlarmCreateRequest request) throws SiteWhereException {
	Document existing = assertDeviceAlarm(id);
	DeviceAlarm updatedAlarm = MongoDeviceAlarm.fromDocument(existing);

	IDeviceAssignment assignment = null;
	if (request.getDeviceAssignmentToken() != null) {
	    assignment = getDeviceAssignmentByToken(request.getDeviceAssignmentToken());
	    if (assignment == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
	    }
	}

	DeviceManagementPersistence.deviceAlarmUpdateLogic(assignment, request, updatedAlarm);
	Document updated = MongoDeviceAlarm.toDocument(updatedAlarm);

	MongoCollection<Document> alarms = getMongoClient().getDeviceAlarmsCollection();
	Document query = new Document(MongoDeviceAlarm.PROP_ID, id);
	MongoPersistence.update(alarms, query, updated);

	return MongoDeviceAlarm.fromDocument(updated);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAlarm(java.util.UUID)
     */
    @Override
    public IDeviceAlarm getDeviceAlarm(UUID id) throws SiteWhereException {
	Document dbAlarm = getDeviceAlarmDocumentById(id);
	if (dbAlarm != null) {
	    return MongoDeviceAlarm.fromDocument(dbAlarm);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#searchDeviceAlarms(com.sitewhere.
     * spi.search.device.IDeviceAlarmSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAlarm> searchDeviceAlarms(IDeviceAlarmSearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> alarms = getMongoClient().getDeviceAlarmsCollection();
	Document dbCriteria = new Document();
	if (criteria.getDeviceId() != null) {
	    dbCriteria.put(MongoDeviceAlarm.PROP_DEVICE_ID, criteria.getDeviceId());
	}
	if (criteria.getDeviceAssignmentId() != null) {
	    dbCriteria.put(MongoDeviceAlarm.PROP_DEVICE_ASSIGNMENT_ID, criteria.getDeviceAssignmentId());
	}
	if (criteria.getCustomerId() != null) {
	    dbCriteria.put(MongoDeviceAlarm.PROP_CUSTOMER_ID, criteria.getCustomerId());
	}
	if (criteria.getAreaId() != null) {
	    dbCriteria.put(MongoDeviceAlarm.PROP_AREA_ID, criteria.getAreaId());
	}
	if (criteria.getAssetId() != null) {
	    dbCriteria.put(MongoDeviceAlarm.PROP_ASSET_ID, criteria.getAssetId());
	}
	if (criteria.getState() != null) {
	    dbCriteria.put(MongoDeviceAlarm.PROP_ALARM_STATE, criteria.getState().name());
	}
	if (criteria.getTriggeringEventId() != null) {
	    dbCriteria.put(MongoDeviceAlarm.PROP_TRIGGERING_EVENT_ID, criteria.getTriggeringEventId());
	}

	Document sort = new Document(MongoDeviceAlarm.PROP_TRIGGERED_DATE, -1);
	return MongoPersistence.search(IDeviceAlarm.class, alarms, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceAlarm(java.util.UUID)
     */
    @Override
    public IDeviceAlarm deleteDeviceAlarm(UUID id) throws SiteWhereException {
	Document existing = assertDeviceAlarm(id);
	DeviceManagementPersistence.deviceAlarmDeleteLogic(MongoDeviceAlarm.fromDocument(existing));

	Document query = new Document(MongoDeviceAlarm.PROP_ID, id);
	MongoCollection<Document> alarms = getMongoClient().getDeviceAlarmsCollection();
	MongoPersistence.update(alarms, query, existing);
	return MongoDeviceAlarm.fromDocument(existing);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignment(java.util.
     * UUID, com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest)
     */
    @Override
    public IDeviceAssignment updateDeviceAssignment(UUID id, IDeviceAssignmentCreateRequest request)
	    throws SiteWhereException {
	Document match = assertDeviceAssignment(id);
	DeviceAssignment assignment = MongoDeviceAssignment.fromDocument(match);

	// Verify updated device token exists.
	IDevice device = null;
	if (request.getDeviceToken() != null) {
	    device = getDeviceByToken(request.getDeviceToken());
	    if (device == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidDeviceToken, ErrorLevel.ERROR);
	    }
	}

	// Verify updated customer token exists.
	ICustomer customer = null;
	if (request.getCustomerToken() != null) {
	    customer = getCustomerByToken(request.getCustomerToken());
	    if (customer == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidCustomerToken, ErrorLevel.ERROR);
	    }
	}

	// Verify updated area token exists.
	IArea area = null;
	if (request.getAreaToken() != null) {
	    area = getAreaByToken(request.getAreaToken());
	    if (area == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
	    }
	}

	// Verify updated asset token exists.
	IAsset asset = null;
	if (request.getAssetToken() != null) {
	    asset = getAssetManagement().getAssetByToken(request.getAssetToken());
	    if (asset == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidAssetToken, ErrorLevel.ERROR);
	    }
	}

	DeviceManagementPersistence.deviceAssignmentUpdateLogic(device, customer, area, asset, request, assignment);

	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection();
	MongoPersistence.update(assignments, query, MongoDeviceAssignment.toDocument(assignment));

	return assignment;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceAssignments(com.
     * sitewhere.spi.search.device.IDeviceAssignmentSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAssignment> listDeviceAssignments(IDeviceAssignmentSearchCriteria criteria)
	    throws SiteWhereException {
	if (getLogger().isDebugEnabled()) {
	    getLogger().debug("Assignment search criteria:\n\n" + MarshalUtils.marshalJsonAsPrettyString(criteria));
	}
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection();
	Document query = new Document();
	if (criteria.getStatus() != null) {
	    query.append(MongoDeviceAssignment.PROP_STATUS, criteria.getStatus().name());
	}
	if (criteria.getDeviceId() != null) {
	    query.append(MongoDeviceAssignment.PROP_DEVICE_ID, criteria.getDeviceId());
	}
	if ((criteria.getCustomerIds() != null) && (criteria.getCustomerIds().size() > 0)) {
	    query.append(MongoDeviceAssignment.PROP_CUSTOMER_ID, new Document("$in", criteria.getCustomerIds()));
	}
	if ((criteria.getAreaIds() != null) && (criteria.getAreaIds().size() > 0)) {
	    query.append(MongoDeviceAssignment.PROP_AREA_ID, new Document("$in", criteria.getAreaIds()));
	}
	if ((criteria.getAssetIds() != null) && (criteria.getAssetIds().size() > 0)) {
	    query.append(MongoDeviceAssignment.PROP_ASSET_ID, new Document("$in", criteria.getAssetIds()));
	}
	Document sort = new Document(MongoDeviceAssignment.PROP_ACTIVE_DATE, -1);
	return MongoPersistence.search(IDeviceAssignment.class, assignments, query, sort, criteria, LOOKUP);
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
	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
	MongoPersistence.update(assignments, query, match);

	// Remove device assignment reference.
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection();
	UUID deviceId = (UUID) match.get(MongoDeviceAssignment.PROP_DEVICE_ID);
	Document deviceMatch = getDeviceDocumentById(deviceId);
	deviceMatch.put(MongoDevice.PROP_ASSIGNMENT_ID, null);
	query = new Document(MongoPersistentEntity.PROP_ID, deviceId);
	MongoPersistence.update(devices, query, deviceMatch);

	DeviceAssignment assignment = MongoDeviceAssignment.fromDocument(match);
	return assignment;
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
	Document query = new Document(MongoPersistentEntity.PROP_TOKEN, token);
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
	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
	return assignments.find(query).first();
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createCustomerType(com.sitewhere.
     * spi.customer.request.ICustomerTypeCreateRequest)
     */
    @Override
    public ICustomerType createCustomerType(ICustomerTypeCreateRequest request) throws SiteWhereException {
	// Convert contained customer type tokens to ids.
	List<UUID> cctids = convertCustomerTypeTokensToIds(request.getContainedCustomerTypeTokens());

	// Use common logic so all backend implementations work the same.
	CustomerType type = DeviceManagementPersistence.customerTypeCreateLogic(request, cctids);

	MongoCollection<Document> types = getMongoClient().getCustomerTypesCollection();
	Document created = MongoCustomerType.toDocument(type);
	MongoPersistence.insert(types, created, ErrorCode.DuplicateCustomerTypeToken);
	return MongoCustomerType.fromDocument(created);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getCustomerType(java.util.UUID)
     */
    @Override
    public ICustomerType getCustomerType(UUID id) throws SiteWhereException {
	Document document = getCustomerTypeDocumentById(id);
	if (document != null) {
	    return MongoCustomerType.fromDocument(document);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getCustomerTypeByToken(java.lang.
     * String)
     */
    @Override
    public ICustomerType getCustomerTypeByToken(String token) throws SiteWhereException {
	Document document = getCustomerTypeDocumentByToken(token);
	if (document != null) {
	    return MongoCustomerType.fromDocument(document);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateCustomerType(java.util.UUID,
     * com.sitewhere.spi.customer.request.ICustomerTypeCreateRequest)
     */
    @Override
    public ICustomerType updateCustomerType(UUID id, ICustomerTypeCreateRequest request) throws SiteWhereException {
	ICustomerType type = getApiCustomerTypeById(id);

	// Convert contained customer type tokens to ids.
	List<UUID> cctids = convertCustomerTypeTokensToIds(request.getContainedCustomerTypeTokens());

	// Use common update logic.
	DeviceManagementPersistence.customerTypeUpdateLogic(request, cctids, (CustomerType) type);

	Document updated = MongoCustomerType.toDocument(type);

	MongoCollection<Document> types = getMongoClient().getCustomerTypesCollection();
	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
	MongoPersistence.update(types, query, updated);
	return MongoCustomerType.fromDocument(updated);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listCustomerTypes(com.sitewhere.
     * spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<ICustomerType> listCustomerTypes(ISearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> types = getMongoClient().getCustomerTypesCollection();
	Document query = new Document();
	Document sort = new Document(MongoCustomerType.PROP_NAME, 1);
	return MongoPersistence.search(ICustomerType.class, types, query, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteCustomerType(java.util.UUID)
     */
    @Override
    public ICustomerType deleteCustomerType(UUID id) throws SiteWhereException {
	Document existing = getCustomerTypeDocumentById(id);
	if (existing == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidCustomerTypeToken, ErrorLevel.ERROR);
	}
	MongoCollection<Document> types = getMongoClient().getCustomerTypesCollection();
	MongoPersistence.delete(types, existing);
	return MongoCustomerType.fromDocument(existing);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createCustomer(com.sitewhere.spi.
     * customer.request.ICustomerCreateRequest)
     */
    @Override
    public ICustomer createCustomer(ICustomerCreateRequest request) throws SiteWhereException {
	// Look up customer type.
	ICustomerType customerType = getCustomerTypeByToken(request.getCustomerTypeToken());
	if (customerType == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidCustomerTypeToken, ErrorLevel.ERROR);
	}

	// Look up parent customer.
	ICustomer parentCustomer = (request.getParentCustomerToken() != null)
		? getCustomerByToken(request.getParentCustomerToken())
		: null;

	// Use common logic so all backend implementations work the same.
	Customer customer = DeviceManagementPersistence.customerCreateLogic(request, customerType, parentCustomer);

	MongoCollection<Document> customers = getMongoClient().getCustomersCollection();
	Document created = MongoCustomer.toDocument(customer);
	MongoPersistence.insert(customers, created, ErrorCode.DuplicateCustomerToken);
	return MongoCustomer.fromDocument(created);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getCustomer(java.util.UUID)
     */
    @Override
    public ICustomer getCustomer(UUID id) throws SiteWhereException {
	Document document = getCustomerDocumentById(id);
	if (document != null) {
	    return MongoCustomer.fromDocument(document);
	}
	return null;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getCustomerByToken(java.lang.
     * String)
     */
    @Override
    public ICustomer getCustomerByToken(String token) throws SiteWhereException {
	Document document = getCustomerDocumentByToken(token);
	if (document != null) {
	    return MongoCustomer.fromDocument(document);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getCustomerChildren(java.lang.
     * String)
     */
    @Override
    public List<ICustomer> getCustomerChildren(String token) throws SiteWhereException {
	ICustomer existing = getCustomerByToken(token);
	if (existing == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidCustomerToken, ErrorLevel.ERROR);
	}

	MongoCollection<Document> customers = getMongoClient().getCustomersCollection();
	Document query = new Document(MongoCustomer.PROP_PARENT_CUSTOMER_ID, existing.getId());
	Document sort = new Document(MongoCustomer.PROP_NAME, 1);
	SearchResults<ICustomer> matches = MongoPersistence.search(ICustomer.class, customers, query, sort,
		SearchCriteria.ALL, LOOKUP);
	return matches.getResults();
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateCustomer(java.util.UUID,
     * com.sitewhere.spi.customer.request.ICustomerCreateRequest)
     */
    @Override
    public ICustomer updateCustomer(UUID id, ICustomerCreateRequest request) throws SiteWhereException {
	ICustomer customer = assertApiCustomer(id);

	// Use common update logic.
	DeviceManagementPersistence.customerUpdateLogic(request, (Customer) customer);

	Document updated = MongoCustomer.toDocument(customer);

	MongoCollection<Document> customers = getMongoClient().getCustomersCollection();
	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
	MongoPersistence.update(customers, query, updated);
	return MongoCustomer.fromDocument(updated);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listCustomers(com.sitewhere.spi.
     * search.customer.ICustomerSearchCriteria)
     */
    @Override
    public ISearchResults<ICustomer> listCustomers(ICustomerSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> customers = getMongoClient().getCustomersCollection();
	Document query = new Document();
	if ((criteria.getRootOnly() != null) && (criteria.getRootOnly().booleanValue() == true)) {
	    query.append(MongoCustomer.PROP_PARENT_CUSTOMER_ID, null);
	} else if (criteria.getParentCustomerId() != null) {
	    query.append(MongoCustomer.PROP_PARENT_CUSTOMER_ID, criteria.getParentCustomerId());
	}
	if (criteria.getCustomerTypeId() != null) {
	    query.append(MongoCustomer.PROP_CUSTOMER_TYPE_ID, criteria.getCustomerTypeId());
	}
	Document sort = new Document(MongoArea.PROP_NAME, 1);
	return MongoPersistence.search(ICustomer.class, customers, query, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteCustomer(java.util.UUID)
     */
    @Override
    public ICustomer deleteCustomer(UUID id) throws SiteWhereException {
	Document existing = getCustomerDocumentById(id);
	if (existing == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidCustomerToken, ErrorLevel.ERROR);
	}
	MongoCollection<Document> customers = getMongoClient().getCustomersCollection();
	MongoPersistence.delete(customers, existing);
	return MongoCustomer.fromDocument(existing);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createAreaType(com.sitewhere.spi.
     * area.request.IAreaTypeCreateRequest)
     */
    @Override
    public IAreaType createAreaType(IAreaTypeCreateRequest request) throws SiteWhereException {
	// Convert contained area type tokens to ids.
	List<UUID> catids = convertAreaTypeTokensToIds(request.getContainedAreaTypeTokens());

	// Use common logic so all backend implementations work the same.
	AreaType type = DeviceManagementPersistence.areaTypeCreateLogic(request, catids);

	MongoCollection<Document> types = getMongoClient().getAreaTypesCollection();
	Document created = MongoAreaType.toDocument(type);
	MongoPersistence.insert(types, created, ErrorCode.DuplicateAreaTypeToken);
	return MongoAreaType.fromDocument(created);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getAreaType(java.util.UUID)
     */
    @Override
    public IAreaType getAreaType(UUID id) throws SiteWhereException {
	Document document = getAreaTypeDocumentById(id);
	if (document != null) {
	    return MongoAreaType.fromDocument(document);
	}
	return null;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getAreaTypeByToken(java.lang.
     * String)
     */
    @Override
    public IAreaType getAreaTypeByToken(String token) throws SiteWhereException {
	Document document = getAreaTypeDocumentByToken(token);
	if (document != null) {
	    return MongoAreaType.fromDocument(document);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateAreaType(java.util.UUID,
     * com.sitewhere.spi.area.request.IAreaTypeCreateRequest)
     */
    @Override
    public IAreaType updateAreaType(UUID id, IAreaTypeCreateRequest request) throws SiteWhereException {
	IAreaType type = getApiAreaTypeById(id);

	// Convert contained area type tokens to ids.
	List<UUID> catids = convertAreaTypeTokensToIds(request.getContainedAreaTypeTokens());

	// Use common update logic.
	DeviceManagementPersistence.areaTypeUpdateLogic(request, catids, (AreaType) type);

	Document updated = MongoAreaType.toDocument(type);

	MongoCollection<Document> types = getMongoClient().getAreaTypesCollection();
	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
	MongoPersistence.update(types, query, updated);
	return MongoAreaType.fromDocument(updated);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listAreaTypes(com.sitewhere.spi.
     * search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IAreaType> listAreaTypes(ISearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> types = getMongoClient().getAreaTypesCollection();
	Document query = new Document();
	Document sort = new Document(MongoAreaType.PROP_NAME, 1);
	return MongoPersistence.search(IAreaType.class, types, query, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteAreaType(java.util.UUID)
     */
    @Override
    public IAreaType deleteAreaType(UUID id) throws SiteWhereException {
	Document existing = getAreaTypeDocumentById(id);
	if (existing == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAreaTypeToken, ErrorLevel.ERROR);
	}
	MongoCollection<Document> types = getMongoClient().getAreaTypesCollection();
	MongoPersistence.delete(types, existing);
	return MongoAreaType.fromDocument(existing);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createArea(com.sitewhere.spi.area.
     * request.IAreaCreateRequest)
     */
    @Override
    public IArea createArea(IAreaCreateRequest request) throws SiteWhereException {
	// Look up area type.
	IAreaType areaType = getAreaTypeByToken(request.getAreaTypeToken());
	if (areaType == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAreaTypeToken, ErrorLevel.ERROR);
	}

	// Look up parent area.
	IArea parentArea = (request.getParentAreaToken() != null) ? getAreaByToken(request.getParentAreaToken()) : null;

	// Use common logic so all backend implementations work the same.
	Area area = DeviceManagementPersistence.areaCreateLogic(request, areaType, parentArea);

	MongoCollection<Document> areas = getMongoClient().getAreasCollection();
	Document created = MongoArea.toDocument(area);
	MongoPersistence.insert(areas, created, ErrorCode.DuplicateAreaToken);
	return MongoArea.fromDocument(created);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getArea(java.util.UUID)
     */
    @Override
    public IArea getArea(UUID id) throws SiteWhereException {
	Document dbArea = getAreaDocumentById(id);
	if (dbArea != null) {
	    return MongoArea.fromDocument(dbArea);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getAreaByToken(java.lang.String)
     */
    @Override
    public IArea getAreaByToken(String token) throws SiteWhereException {
	Document dbArea = getAreaDocumentByToken(token);
	if (dbArea != null) {
	    return MongoArea.fromDocument(dbArea);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getAreaChildren(java.lang.String)
     */
    @Override
    public List<IArea> getAreaChildren(String token) throws SiteWhereException {
	IArea existing = getAreaByToken(token);
	if (existing == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
	}

	MongoCollection<Document> areas = getMongoClient().getAreasCollection();
	Document query = new Document(MongoArea.PROP_PARENT_AREA_ID, existing.getId());
	Document sort = new Document(MongoArea.PROP_NAME, 1);
	SearchResults<IArea> matches = MongoPersistence.search(IArea.class, areas, query, sort, SearchCriteria.ALL,
		LOOKUP);
	return matches.getResults();
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#updateArea(java.util.UUID,
     * com.sitewhere.spi.area.request.IAreaCreateRequest)
     */
    @Override
    public IArea updateArea(UUID id, IAreaCreateRequest request) throws SiteWhereException {
	IArea area = assertApiArea(id);

	// Use common update logic.
	DeviceManagementPersistence.areaUpdateLogic(request, (Area) area);

	Document updated = MongoArea.toDocument(area);

	MongoCollection<Document> areas = getMongoClient().getAreasCollection();
	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
	MongoPersistence.update(areas, query, updated);
	return MongoArea.fromDocument(updated);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listAreas(com.sitewhere.spi.search
     * .area.IAreaSearchCriteria)
     */
    @Override
    public SearchResults<IArea> listAreas(IAreaSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> areas = getMongoClient().getAreasCollection();
	Document query = new Document();
	if ((criteria.getRootOnly() != null) && (criteria.getRootOnly().booleanValue() == true)) {
	    query.append(MongoArea.PROP_PARENT_AREA_ID, null);
	} else if (criteria.getParentAreaId() != null) {
	    query.append(MongoArea.PROP_PARENT_AREA_ID, criteria.getParentAreaId());
	}
	if (criteria.getAreaTypeId() != null) {
	    query.append(MongoArea.PROP_AREA_TYPE_ID, criteria.getAreaTypeId());
	}
	Document sort = new Document(MongoArea.PROP_NAME, 1);
	return MongoPersistence.search(IArea.class, areas, query, sort, criteria, LOOKUP);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteArea(java.util.UUID)
     */
    @Override
    public IArea deleteArea(UUID id) throws SiteWhereException {
	Document existing = getAreaDocumentById(id);
	if (existing == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
	}
	MongoCollection<Document> areas = getMongoClient().getAreasCollection();
	MongoPersistence.delete(areas, existing);
	return MongoArea.fromDocument(existing);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createZone(com.sitewhere.spi.area.
     * request.IZoneCreateRequest)
     */
    @Override
    public IZone createZone(IZoneCreateRequest request) throws SiteWhereException {
	if (request.getAreaToken() == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
	}

	IArea area = null;
	if (request.getAreaToken() != null) {
	    area = getAreaByToken(request.getAreaToken());
	    if (area == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
	    }
	}

	Zone zone = DeviceManagementPersistence.zoneCreateLogic(request, area, UUID.randomUUID().toString());

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

	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listZones(com.sitewhere.spi.search
     * .device.IZoneSearchCriteria)
     */
    @Override
    public SearchResults<IZone> listZones(IZoneSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> zones = getMongoClient().getZonesCollection();

	// Query based on search criteria.
	Document query = new Document();
	if (criteria.getAreaId() != null) {
	    query.append(MongoZone.PROP_AREA_ID, criteria.getAreaId());
	}

	Document sort = new Document(MongoPersistentEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IZone.class, zones, query, sort, criteria, LOOKUP);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteZone(java.util.UUID)
     */
    @Override
    public IZone deleteZone(UUID id) throws SiteWhereException {
	Document existing = assertZone(id);
	MongoCollection<Document> zones = getMongoClient().getZonesCollection();
	MongoPersistence.delete(zones, existing);
	return MongoZone.fromDocument(existing);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#createDeviceGroup(com.
     * sitewhere.spi. device.request.IDeviceGroupCreateRequest)
     */
    @Override
    public IDeviceGroup createDeviceGroup(IDeviceGroupCreateRequest request) throws SiteWhereException {
	// Use common logic so all backend implementations work the same.
	DeviceGroup group = DeviceManagementPersistence.deviceGroupCreateLogic(request);

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

	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroups(com.sitewhere.spi
     * .search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroups(ISearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection();
	Document dbCriteria = new Document();
	Document sort = new Document(MongoPersistentEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IDeviceGroup.class, groups, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroupsWithRole(java.lang
     * .String, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroupsWithRole(String role, ISearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection();
	Document dbCriteria = new Document(MongoDeviceGroup.PROP_ROLES, role);
	Document sort = new Document(MongoPersistentEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IDeviceGroup.class, groups, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceGroup(java.util.UUID)
     */
    @Override
    public IDeviceGroup deleteDeviceGroup(UUID id) throws SiteWhereException {
	Document existing = assertDeviceGroup(id);
	MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection();
	MongoPersistence.delete(groups, existing);

	// Delete group elements as well.
	MongoCollection<Document> elements = getMongoClient().getGroupElementsCollection();
	Document match = new Document(MongoDeviceGroupElement.PROP_GROUP_ID, id);
	MongoPersistence.delete(elements, match);

	return MongoDeviceGroup.fromDocument(existing);
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
	    // Look up referenced device if provided.
	    IDevice device = null;
	    if (request.getDeviceToken() != null) {
		device = getDeviceByToken(request.getDeviceToken());
		if (device == null) {
		    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceToken, ErrorLevel.ERROR);
		}
	    }
	    // Look up referenced nested group if provided.
	    IDeviceGroup nested = null;
	    if (request.getNestedGroupToken() != null) {
		nested = getDeviceGroupByToken(request.getNestedGroupToken());
		if (nested == null) {
		    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceGroupToken, ErrorLevel.ERROR);
		}
	    }

	    DeviceGroupElement element = DeviceManagementPersistence.deviceGroupElementCreateLogic(request, group,
		    device, nested);
	    Document created = MongoDeviceGroupElement.toDocument(element);
	    try {
		MongoPersistence.insert(getMongoClient().getGroupElementsCollection(), created, ErrorCode.DuplicateId);
		results.add(MongoDeviceGroupElement.fromDocument(created));
	    } catch (ResourceExistsException e) {
		if (!ignoreDuplicates) {
		    throw e;
		}
	    }
	}
	return results;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#removeDeviceGroupElements(java.
     * util.List)
     */
    @Override
    public List<IDeviceGroupElement> removeDeviceGroupElements(List<UUID> elementIds) throws SiteWhereException {
	List<IDeviceGroupElement> deleted = new ArrayList<IDeviceGroupElement>();
	for (UUID elementId : elementIds) {
	    Document match = new Document(MongoDeviceGroupElement.PROP_ID, elementId);
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
	Document sort = new Document();
	return MongoPersistence.search(IDeviceGroupElement.class, getMongoClient().getGroupElementsCollection(), match,
		sort, criteria, LOOKUP);
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
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceId, ErrorLevel.INFO);
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
     * Get the {@link Document} containing device alarm information that matches the
     * given id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceAlarmDocumentById(UUID id) throws SiteWhereException {
	MongoCollection<Document> alarms = getMongoClient().getDeviceAlarmsCollection();
	Document query = new Document(MongoDeviceAlarm.PROP_ID, id);
	return alarms.find(query).first();
    }

    /**
     * Return the {@link Document} for the device alarm with the given id. Throws an
     * exception if the id is not found.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected Document assertDeviceAlarm(UUID id) throws SiteWhereException {
	Document match = getDeviceAlarmDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAlarmId, ErrorLevel.INFO);
	}
	return match;
    }

    /**
     * Get the DBObject containing customer type information that matches the given
     * id.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getCustomerTypeDocumentById(UUID id) throws SiteWhereException {
	MongoCollection<Document> types = getMongoClient().getCustomerTypesCollection();
	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
	return types.find(query).first();
    }

    /**
     * Get the DBObject containing customer type information that matches the given
     * token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getCustomerTypeDocumentByToken(String token) throws SiteWhereException {
	MongoCollection<Document> types = getMongoClient().getCustomerTypesCollection();
	Document query = new Document(MongoPersistentEntity.PROP_TOKEN, token);
	return types.find(query).first();
    }

    /**
     * Get API customer type based on unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected ICustomerType getApiCustomerTypeById(UUID id) throws SiteWhereException {
	Document match = getCustomerTypeDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidCustomerTypeToken, ErrorLevel.ERROR);
	}
	return MongoCustomerType.fromDocument(match);
    }

    /**
     * Convert a list of area type tokens to ids.
     * 
     * @param tokens
     * @return
     * @throws SiteWhereException
     */
    protected List<UUID> convertCustomerTypeTokensToIds(List<String> tokens) throws SiteWhereException {
	List<UUID> cctids = new ArrayList<>();
	if (tokens != null) {
	    for (String token : tokens) {
		ICustomerType contained = getCustomerTypeByToken(token);
		if (contained != null) {
		    cctids.add(contained.getId());
		}
	    }
	}
	return cctids;
    }

    /**
     * Get the DBObject containing customer information that matches the given id.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getCustomerDocumentById(UUID id) throws SiteWhereException {
	MongoCollection<Document> customers = getMongoClient().getCustomersCollection();
	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
	return customers.find(query).first();
    }

    /**
     * Get the DBObject containing customer information that matches the given
     * token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getCustomerDocumentByToken(String token) throws SiteWhereException {
	MongoCollection<Document> customers = getMongoClient().getCustomersCollection();
	Document query = new Document(MongoPersistentEntity.PROP_TOKEN, token);
	return customers.find(query).first();
    }

    /**
     * Get API customer based on unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected ICustomer assertApiCustomer(UUID id) throws SiteWhereException {
	Document match = getCustomerDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidCustomerToken, ErrorLevel.ERROR);
	}
	return MongoCustomer.fromDocument(match);
    }

    /**
     * Get the DBObject containing area type information that matches the given
     * token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getAreaTypeDocumentByToken(String token) throws SiteWhereException {
	MongoCollection<Document> types = getMongoClient().getAreaTypesCollection();
	Document query = new Document(MongoPersistentEntity.PROP_TOKEN, token);
	return types.find(query).first();
    }

    /**
     * Get the DBObject containing area type information that matches the given id.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getAreaTypeDocumentById(UUID id) throws SiteWhereException {
	MongoCollection<Document> types = getMongoClient().getAreaTypesCollection();
	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
	return types.find(query).first();
    }

    /**
     * Get API area type based on unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected IAreaType getApiAreaTypeById(UUID id) throws SiteWhereException {
	Document match = getAreaTypeDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAreaTypeToken, ErrorLevel.ERROR);
	}
	return MongoAreaType.fromDocument(match);
    }

    /**
     * Convert a list of area type tokens to ids.
     * 
     * @param tokens
     * @return
     * @throws SiteWhereException
     */
    protected List<UUID> convertAreaTypeTokensToIds(List<String> tokens) throws SiteWhereException {
	List<UUID> catids = new ArrayList<>();
	if (tokens != null) {
	    for (String token : tokens) {
		IAreaType contained = getAreaTypeByToken(token);
		if (contained != null) {
		    catids.add(contained.getId());
		}
	    }
	}
	return catids;
    }

    /**
     * Get the DBObject containing area information that matches the given token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getAreaDocumentByToken(String token) throws SiteWhereException {
	MongoCollection<Document> areas = getMongoClient().getAreasCollection();
	Document query = new Document(MongoPersistentEntity.PROP_TOKEN, token);
	return areas.find(query).first();
    }

    /**
     * Get the DBObject containing area information that matches the given id.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getAreaDocumentById(UUID id) throws SiteWhereException {
	MongoCollection<Document> areas = getMongoClient().getAreasCollection();
	Document query = new Document(MongoPersistentEntity.PROP_ID, id);
	return areas.find(query).first();
    }

    /**
     * Return the {@link Document} for the area with the given token. Throws an
     * exception if the token is not found.
     * 
     * @param hardwareId
     * @return
     * @throws SiteWhereException
     */
    protected Document assertArea(String token) throws SiteWhereException {
	Document match = getAreaDocumentByToken(token);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.INFO);
	}
	return match;
    }

    /**
     * Get API area based on unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected IArea assertApiArea(UUID id) throws SiteWhereException {
	Document match = getAreaDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
	}
	return MongoArea.fromDocument(match);
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
	    Document query = new Document(MongoPersistentEntity.PROP_TOKEN, token);
	    return zones.find(query).first();
	} catch (MongoClientException e) {
	    throw MongoPersistence.handleClientException(e);
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
	    Document query = new Document(MongoPersistentEntity.PROP_ID, id);
	    return zones.find(query).first();
	} catch (MongoClientException e) {
	    throw MongoPersistence.handleClientException(e);
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
	    Document query = new Document(MongoPersistentEntity.PROP_TOKEN, token);
	    return groups.find(query).first();
	} catch (MongoClientException e) {
	    throw MongoPersistence.handleClientException(e);
	}
    }

    protected Document getDeviceGroupDocumentById(UUID id) throws SiteWhereException {
	try {
	    MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection();
	    Document query = new Document(MongoPersistentEntity.PROP_ID, id);
	    return groups.find(query).first();
	} catch (MongoClientException e) {
	    throw MongoPersistence.handleClientException(e);
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

    /**
     * Get asset management implementation from microservice.
     * 
     * @return
     */
    public IAssetManagement getAssetManagement() {
	return ((DeviceManagementMicroservice) getTenantEngine().getMicroservice()).getAssetManagementApiDemux()
		.getApiChannel();
    }

    public DeviceManagementMongoClient getMongoClient() {
	return mongoClient;
    }

    public void setMongoClient(DeviceManagementMongoClient mongoClient) {
	this.mongoClient = mongoClient;
    }
}