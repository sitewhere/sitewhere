/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicemanagement.mongodb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.mongodb.IMongoConverterLookup;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.DeviceStatus;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.device.batch.BatchElement;
import com.sitewhere.rest.model.device.batch.BatchOperation;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.common.IMetadataProvider;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.ICachingDeviceManagement;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.device.batch.IBatchElement;
import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
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
import com.sitewhere.spi.device.request.IDeviceStatusCreateRequest;
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
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Device management implementation that uses MongoDB for persistence.
 * 
 * @author dadams
 */
public class MongoDeviceManagement extends TenantLifecycleComponent
	implements IDeviceManagement, ICachingDeviceManagement {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Converter lookup */
    private static IMongoConverterLookup LOOKUP = new MongoConverters();

    /** Injected with global SiteWhere Mongo client */
    private IDeviceManagementMongoClient mongoClient;

    /** Provides caching for device management entities */
    private IDeviceManagementCacheProvider cacheProvider;

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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.ICachingDeviceManagement#setCacheProvider(com.
     * sitewhere .spi.device.IDeviceManagementCacheProvider)
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
	// Site indexes.
	getMongoClient().getSitesCollection(getTenant()).createIndex(new Document(MongoSite.PROP_TOKEN, 1),
		new IndexOptions().unique(true));

	// Specification-related indexes.
	getMongoClient().getDeviceSpecificationsCollection(getTenant())
		.createIndex(new Document(MongoDeviceSpecification.PROP_TOKEN, 1), new IndexOptions().unique(true));
	getMongoClient().getDeviceStatusesCollection(getTenant()).createIndex(
		new Document(MongoDeviceStatus.PROP_SPEC_TOKEN, 1).append(MongoDeviceStatus.PROP_CODE, 1),
		new IndexOptions().unique(true));

	// Device and assignment indexes.
	getMongoClient().getDevicesCollection(getTenant()).createIndex(new Document(MongoDevice.PROP_HARDWARE_ID, 1),
		new IndexOptions().unique(true));
	getMongoClient().getDeviceAssignmentsCollection(getTenant())
		.createIndex(new Document(MongoDeviceAssignment.PROP_TOKEN, 1), new IndexOptions().unique(true));
	getMongoClient().getDeviceAssignmentsCollection(getTenant())
		.createIndex(new Document(MongoDeviceAssignment.PROP_SITE_TOKEN, 1)
			.append(MongoDeviceAssignment.PROP_ASSET_MODULE_ID, 1)
			.append(MongoDeviceAssignment.PROP_ASSET_ID, 1).append(MongoDeviceAssignment.PROP_STATUS, 1));

	// Device group indexes.
	getMongoClient().getDeviceGroupsCollection(getTenant())
		.createIndex(new Document(MongoDeviceGroup.PROP_TOKEN, 1), new IndexOptions().unique(true));
	getMongoClient().getDeviceGroupsCollection(getTenant())
		.createIndex(new Document(MongoDeviceGroup.PROP_ROLES, 1));
	getMongoClient().getGroupElementsCollection(getTenant())
		.createIndex(new Document(MongoDeviceGroupElement.PROP_GROUP_TOKEN, 1)
			.append(MongoDeviceGroupElement.PROP_TYPE, 1).append(MongoDeviceGroupElement.PROP_ELEMENT_ID,
				1));
	getMongoClient().getGroupElementsCollection(getTenant())
		.createIndex(new Document(MongoDeviceGroupElement.PROP_GROUP_TOKEN, 1)
			.append(MongoDeviceGroupElement.PROP_ROLES, 1));

	// Batch operation indexes.
	getMongoClient().getBatchOperationsCollection(getTenant())
		.createIndex(new Document(MongoBatchOperation.PROP_TOKEN, 1), new IndexOptions().unique(true));
	getMongoClient().getBatchOperationElementsCollection(getTenant())
		.createIndex(new Document(MongoBatchElement.PROP_BATCH_OPERATION_TOKEN, 1));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceSpecification(com.
     * sitewhere .spi.device.request.IDeviceSpecificationCreateRequest)
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

	MongoCollection<Document> specs = getMongoClient().getDeviceSpecificationsCollection(getTenant());
	Document created = MongoDeviceSpecification.toDocument(spec);
	MongoPersistence.insert(specs, created, ErrorCode.DuplicateDeviceSpecificationToken);

	// Update cache with new data.
	if (getCacheProvider() != null) {
	    getCacheProvider().getDeviceSpecificationCache().put(uuid, spec);
	}
	return MongoDeviceSpecification.fromDocument(created);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceSpecificationByToken(
     * java.lang .String)
     */
    @Override
    public IDeviceSpecification getDeviceSpecificationByToken(String token) throws SiteWhereException {
	if (getCacheProvider() != null) {
	    IDeviceSpecification cached = getCacheProvider().getDeviceSpecificationCache().get(token);
	    if (cached != null) {
		return cached;
	    }
	}
	Document dbSpecification = getDeviceSpecificationDocumentByToken(token);
	if (dbSpecification != null) {
	    IDeviceSpecification result = MongoDeviceSpecification.fromDocument(dbSpecification);
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
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceSpecification(java
     * .lang. String,
     * com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest)
     */
    @Override
    public IDeviceSpecification updateDeviceSpecification(String token, IDeviceSpecificationCreateRequest request)
	    throws SiteWhereException {
	Document match = assertDeviceSpecification(token);
	DeviceSpecification spec = MongoDeviceSpecification.fromDocument(match);

	// Use common update logic.
	SiteWherePersistence.deviceSpecificationUpdateLogic(request, spec);
	Document updated = MongoDeviceSpecification.toDocument(spec);

	Document query = new Document(MongoDeviceSpecification.PROP_TOKEN, token);
	MongoCollection<Document> specs = getMongoClient().getDeviceSpecificationsCollection(getTenant());
	MongoPersistence.update(specs, query, updated);

	// Update cache with new data.
	if (getCacheProvider() != null) {
	    getCacheProvider().getDeviceSpecificationCache().put(token, spec);
	}
	return MongoDeviceSpecification.fromDocument(updated);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceSpecifications(
     * boolean, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceSpecification> listDeviceSpecifications(boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> specs = getMongoClient().getDeviceSpecificationsCollection(getTenant());
	Document dbCriteria = new Document();
	if (!includeDeleted) {
	    MongoSiteWhereEntity.setDeleted(dbCriteria, false);
	}
	Document sort = new Document(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IDeviceSpecification.class, specs, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceSpecification(java
     * .lang. String, boolean)
     */
    @Override
    public IDeviceSpecification deleteDeviceSpecification(String token, boolean force) throws SiteWhereException {
	Document existing = assertDeviceSpecification(token);
	MongoCollection<Document> specs = getMongoClient().getDeviceSpecificationsCollection(getTenant());
	if (force) {
	    MongoPersistence.delete(specs, existing);
	    if (getCacheProvider() != null) {
		getCacheProvider().getDeviceSpecificationCache().remove(token);
	    }
	    return MongoDeviceSpecification.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoDeviceSpecification.PROP_TOKEN, token);
	    MongoPersistence.update(specs, query, existing);
	    if (getCacheProvider() != null) {
		getCacheProvider().getDeviceSpecificationCache().remove(token);
	    }
	    return MongoDeviceSpecification.fromDocument(existing);
	}
    }

    /**
     * Return the {@link Document} for the device specification with the given
     * token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceSpecificationDocumentByToken(String token) throws SiteWhereException {
	MongoCollection<Document> specs = getMongoClient().getDeviceSpecificationsCollection(getTenant());
	Document query = new Document(MongoDeviceSpecification.PROP_TOKEN, token);
	return specs.find(query).first();
    }

    /**
     * Return the {@link Document} for the device specification with the given
     * token. Throws an exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document assertDeviceSpecification(String token) throws SiteWhereException {
	Document match = getDeviceSpecificationDocumentByToken(token);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceSpecificationToken, ErrorLevel.ERROR);
	}
	return match;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#createDeviceCommand(com.
     * sitewhere.spi .device.IDeviceSpecification,
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

	MongoCollection<Document> commands = getMongoClient().getDeviceCommandsCollection(getTenant());
	Document created = MongoDeviceCommand.toDocument(command);
	MongoPersistence.insert(commands, created, ErrorCode.DeviceCommandExists);
	return MongoDeviceCommand.fromDocument(created);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceCommandByToken(java.
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
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceCommand(java.lang.
     * String, com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public IDeviceCommand updateDeviceCommand(String token, IDeviceCommandCreateRequest request)
	    throws SiteWhereException {
	Document match = assertDeviceCommand(token);
	DeviceCommand command = MongoDeviceCommand.fromDocument(match);

	// Note: This allows duplicates if duplicate was marked deleted.
	List<IDeviceCommand> existing = listDeviceCommands(token, false);

	// Use common update logic.
	SiteWherePersistence.deviceCommandUpdateLogic(request, command, existing);
	Document updated = MongoDeviceCommand.toDocument(command);

	Document query = new Document(MongoDeviceCommand.PROP_TOKEN, token);
	MongoCollection<Document> commands = getMongoClient().getDeviceCommandsCollection(getTenant());
	MongoPersistence.update(commands, query, updated);
	return MongoDeviceCommand.fromDocument(updated);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommands(java.lang.
     * String, boolean)
     */
    @Override
    public List<IDeviceCommand> listDeviceCommands(String token, boolean includeDeleted) throws SiteWhereException {
	MongoCollection<Document> commands = getMongoClient().getDeviceCommandsCollection(getTenant());
	Document dbCriteria = new Document();
	dbCriteria.put(MongoDeviceCommand.PROP_SPEC_TOKEN, token);
	if (!includeDeleted) {
	    MongoSiteWhereEntity.setDeleted(dbCriteria, false);
	}
	Document sort = new Document(MongoDeviceCommand.PROP_NAME, 1);
	return MongoPersistence.list(IDeviceCommand.class, commands, dbCriteria, sort, LOOKUP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceCommand(java.lang.
     * String, boolean)
     */
    @Override
    public IDeviceCommand deleteDeviceCommand(String token, boolean force) throws SiteWhereException {
	Document existing = assertDeviceCommand(token);
	MongoCollection<Document> commands = getMongoClient().getDeviceCommandsCollection(getTenant());
	if (force) {
	    MongoPersistence.delete(commands, existing);
	    return MongoDeviceCommand.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoDeviceCommand.PROP_TOKEN, token);
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
	MongoCollection<Document> commands = getMongoClient().getDeviceCommandsCollection(getTenant());
	Document query = new Document(MongoDeviceCommand.PROP_TOKEN, token);
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
    protected Document assertDeviceCommand(String token) throws SiteWhereException {
	Document match = getDeviceCommandDocumentByToken(token);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceCommandToken, ErrorLevel.ERROR);
	}
	return match;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceStatus(java.lang.
     * String, com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus createDeviceStatus(String specToken, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	// Get list of existing statuses to prevent duplicates.
	List<IDeviceStatus> existing = listDeviceStatuses(specToken);
	IDeviceSpecification specification = getDeviceSpecificationByToken(specToken);
	if (specification == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceSpecificationToken, ErrorLevel.ERROR);
	}

	// Use common logic so all backend implementations work the same.
	DeviceStatus status = SiteWherePersistence.deviceStatusCreateLogic(specification, request, existing);

	MongoCollection<Document> statuses = getMongoClient().getDeviceStatusesCollection(getTenant());
	Document created = MongoDeviceStatus.toDocument(status);
	MongoPersistence.insert(statuses, created, ErrorCode.DeviceStatusExists);
	return MongoDeviceStatus.fromDocument(created);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceStatusByCode(java.
     * lang.String, java.lang.String)
     */
    @Override
    public IDeviceStatus getDeviceStatusByCode(String specToken, String code) throws SiteWhereException {
	Document result = getDeviceStatusDocument(specToken, code);
	if (result != null) {
	    return MongoDeviceStatus.fromDocument(result);
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceStatus(java.lang.
     * String, java.lang.String,
     * com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus updateDeviceStatus(String specToken, String code, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	Document match = assertDeviceStatus(specToken, code);
	DeviceStatus status = MongoDeviceStatus.fromDocument(match);

	List<IDeviceStatus> existing = listDeviceStatuses(specToken);

	// Use common update logic.
	SiteWherePersistence.deviceStatusUpdateLogic(request, status, existing);
	Document updated = MongoDeviceStatus.toDocument(status);

	Document query = new Document(MongoDeviceStatus.PROP_SPEC_TOKEN, specToken).append(MongoDeviceStatus.PROP_CODE,
		code);
	MongoCollection<Document> statuses = getMongoClient().getDeviceStatusesCollection(getTenant());
	MongoPersistence.update(statuses, query, updated);
	return MongoDeviceStatus.fromDocument(updated);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceStatuses(java.lang.
     * String, boolean)
     */
    @Override
    public List<IDeviceStatus> listDeviceStatuses(String specToken) throws SiteWhereException {
	MongoCollection<Document> statuses = getMongoClient().getDeviceStatusesCollection(getTenant());
	Document dbCriteria = new Document();
	dbCriteria.put(MongoDeviceStatus.PROP_SPEC_TOKEN, specToken);
	Document sort = new Document(MongoDeviceStatus.PROP_NAME, 1);
	return MongoPersistence.list(IDeviceStatus.class, statuses, dbCriteria, sort, LOOKUP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceStatus(java.lang.
     * String, java.lang.String)
     */
    @Override
    public IDeviceStatus deleteDeviceStatus(String specToken, String code) throws SiteWhereException {
	Document existing = assertDeviceStatus(specToken, code);
	MongoCollection<Document> statuses = getMongoClient().getDeviceStatusesCollection(getTenant());
	MongoPersistence.delete(statuses, existing);
	return MongoDeviceStatus.fromDocument(existing);
    }

    /**
     * Return the {@link Document} for a device status based on specification
     * token and status code.
     * 
     * @param specToken
     * @param code
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceStatusDocument(String specToken, String code) throws SiteWhereException {
	MongoCollection<Document> statuses = getMongoClient().getDeviceStatusesCollection(getTenant());
	Document query = new Document(MongoDeviceStatus.PROP_SPEC_TOKEN, specToken).append(MongoDeviceStatus.PROP_CODE,
		code);
	return statuses.find(query).first();
    }

    /**
     * Return the {@link Document} for the device status. Throws an exception if
     * the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document assertDeviceStatus(String specToken, String code) throws SiteWhereException {
	Document match = getDeviceStatusDocument(specToken, code);
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
	Device newDevice = SiteWherePersistence.deviceCreateLogic(request);

	// Convert and save device data.
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection(getTenant());
	Document created = MongoDevice.toDocument(newDevice);
	MongoPersistence.insert(devices, created, ErrorCode.DuplicateHardwareId);

	// Update cache with new data.
	if (getCacheProvider() != null) {
	    getCacheProvider().getDeviceCache().put(request.getHardwareId(), newDevice);
	}
	return newDevice;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDevice(java.lang.String,
     * com.sitewhere.spi.device.request.IDeviceCreateRequest)
     */
    @Override
    public IDevice updateDevice(String hardwareId, IDeviceCreateRequest request) throws SiteWhereException {
	Document existing = assertDevice(hardwareId);
	Device updatedDevice = MongoDevice.fromDocument(existing);

	SiteWherePersistence.deviceUpdateLogic(request, updatedDevice);
	Document updated = MongoDevice.toDocument(updatedDevice);

	MongoCollection<Document> devices = getMongoClient().getDevicesCollection(getTenant());
	Document query = new Document(MongoDevice.PROP_HARDWARE_ID, hardwareId);
	MongoPersistence.update(devices, query, updated);

	// Update cache with new data.
	if (getCacheProvider() != null) {
	    getCacheProvider().getDeviceCache().put(hardwareId, updatedDevice);
	}
	return MongoDevice.fromDocument(updated);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceByHardwareId(java
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
	Document dbDevice = getDeviceDocumentByHardwareId(hardwareId);
	if (dbDevice != null) {
	    IDevice result = MongoDevice.fromDocument(dbDevice);
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getCurrentDeviceAssignment
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
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection(getTenant());
	Document dbCriteria = new Document();
	if (!includeDeleted) {
	    MongoSiteWhereEntity.setDeleted(dbCriteria, false);
	}
	if (criteria.isExcludeAssigned()) {
	    dbCriteria.put(MongoDevice.PROP_ASSIGNMENT_TOKEN, null);
	}
	MongoPersistence.addDateSearchCriteria(dbCriteria, MongoSiteWhereEntity.PROP_CREATED_DATE, criteria);

	// Add specification filter if specified.
	if (!StringUtils.isEmpty(criteria.getSpecificationToken())) {
	    dbCriteria.put(MongoDevice.PROP_SPECIFICATION_TOKEN, criteria.getSpecificationToken());
	}

	// Add site filter if specified.
	if (!StringUtils.isEmpty(criteria.getSiteToken())) {
	    dbCriteria.put(MongoDevice.PROP_SITE_TOKEN, criteria.getSiteToken());
	}

	Document sort = new Document(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IDevice.class, devices, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceElementMapping(
     * java.lang .String, com.sitewhere.spi.device.IDeviceElementMapping)
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
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceElementMapping(
     * java.lang .String, java.lang.String)
     */
    @Override
    public IDevice deleteDeviceElementMapping(String hardwareId, String path) throws SiteWhereException {
	return SiteWherePersistence.deviceElementMappingDeleteLogic(this, hardwareId, path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDevice(java.lang.String,
     * boolean)
     */
    @Override
    public IDevice deleteDevice(String hardwareId, boolean force) throws SiteWhereException {
	Document existing = assertDevice(hardwareId);
	Device device = MongoDevice.fromDocument(existing);
	IDeviceAssignment assignment = getCurrentDeviceAssignment(device);
	if (assignment != null) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceCanNotBeDeletedIfAssigned, ErrorLevel.ERROR);
	}
	if (force) {
	    MongoCollection<Document> devices = getMongoClient().getDevicesCollection(getTenant());
	    MongoPersistence.delete(devices, existing);
	    if (getCacheProvider() != null) {
		getCacheProvider().getDeviceCache().remove(hardwareId);
	    }
	    return MongoDevice.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoDevice.PROP_HARDWARE_ID, hardwareId);
	    MongoCollection<Document> devices = getMongoClient().getDevicesCollection(getTenant());
	    MongoPersistence.update(devices, query, existing);
	    if (getCacheProvider() != null) {
		getCacheProvider().getDeviceCache().remove(hardwareId);
	    }
	    return MongoDevice.fromDocument(existing);
	}
    }

    /**
     * Get the {@link Document} containing site information that matches the
     * given token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceDocumentByHardwareId(String hardwareId) throws SiteWhereException {
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection(getTenant());
	Document query = new Document(MongoDevice.PROP_HARDWARE_ID, hardwareId);
	return devices.find(query).first();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceAssignment(com.
     * sitewhere .spi.device.request. IDeviceAssignmentCreateRequest)
     */
    @Override
    public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request) throws SiteWhereException {
	Document deviceDb = assertDevice(request.getDeviceHardwareId());
	if (deviceDb.get(MongoDevice.PROP_ASSIGNMENT_TOKEN) != null) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceAlreadyAssigned, ErrorLevel.ERROR);
	}
	Device device = MongoDevice.fromDocument(deviceDb);

	// Use common logic to load assignment from request.
	DeviceAssignment newAssignment = SiteWherePersistence.deviceAssignmentCreateLogic(request, device);
	if (newAssignment.getToken() == null) {
	    newAssignment.setToken(UUID.randomUUID().toString());
	}

	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection(getTenant());
	Document created = MongoDeviceAssignment.toDocument(newAssignment);
	MongoPersistence.insert(assignments, created, ErrorCode.DuplicateDeviceAssignment);

	// Update cache with new assignment data.
	if (getCacheProvider() != null) {
	    getCacheProvider().getDeviceAssignmentCache().put(newAssignment.getToken(), newAssignment);
	}

	// Update device to point to created assignment.
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection(getTenant());
	Document query = new Document(MongoDevice.PROP_HARDWARE_ID, request.getDeviceHardwareId());
	deviceDb.put(MongoDevice.PROP_ASSIGNMENT_TOKEN, newAssignment.getToken());
	MongoPersistence.update(devices, query, deviceDb);

	// Update cache with new device data.
	if (getCacheProvider() != null) {
	    Device updated = MongoDevice.fromDocument(deviceDb);
	    getCacheProvider().getDeviceCache().put(updated.getHardwareId(), updated);
	}
	return newAssignment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentByToken
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
	Document dbAssignment = getDeviceAssignmentDocumentByToken(token);
	if (dbAssignment != null) {
	    IDeviceAssignment result = MongoDeviceAssignment.fromDocument(dbAssignment);
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
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceAssignment(java.
     * lang.String, boolean)
     */
    @Override
    public IDeviceAssignment deleteDeviceAssignment(String token, boolean force) throws SiteWhereException {
	Document existing = assertDeviceAssignment(token);
	SiteWherePersistence.deviceAssignmentDeleteLogic(MongoDeviceAssignment.fromDocument(existing));
	if (force) {
	    MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection(getTenant());
	    MongoPersistence.delete(assignments, existing);
	    return MongoDeviceAssignment.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoDeviceAssignment.PROP_TOKEN, token);
	    MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection(getTenant());
	    MongoPersistence.update(assignments, query, existing);
	    return MongoDeviceAssignment.fromDocument(existing);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceForAssignment(com
     * .sitewhere.spi.device .IDeviceAssignment)
     */
    @Override
    public IDevice getDeviceForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
	return getDeviceByHardwareId(assignment.getDeviceHardwareId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getSiteForAssignment(com.
     * sitewhere .spi.device. IDeviceAssignment)
     */
    @Override
    public ISite getSiteForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
	return getSiteByToken(assignment.getSiteToken());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentMetadata
     * (java.lang.String, com.sitewhere.spi.device.IMetadataProvider)
     */
    @Override
    public IDeviceAssignment updateDeviceAssignmentMetadata(String token, IMetadataProvider metadata)
	    throws SiteWhereException {
	Document match = assertDeviceAssignment(token);
	MongoMetadataProvider.toDocument(metadata, match);
	DeviceAssignment assignment = MongoDeviceAssignment.fromDocument(match);
	SiteWherePersistence.setUpdatedEntityMetadata(assignment);
	Document query = new Document(MongoDeviceAssignment.PROP_TOKEN, token);
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection(getTenant());
	MongoPersistence.update(assignments, query, MongoDeviceAssignment.toDocument(assignment));

	// Update cache with new assignment data.
	if (getCacheProvider() != null) {
	    getCacheProvider().getDeviceAssignmentCache().put(assignment.getToken(), assignment);
	}

	return MongoDeviceAssignment.fromDocument(match);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentStatus
     * (java.lang.String, com.sitewhere.spi.device.DeviceAssignmentStatus)
     */
    @Override
    public IDeviceAssignment updateDeviceAssignmentStatus(String token, DeviceAssignmentStatus status)
	    throws SiteWhereException {
	Document match = assertDeviceAssignment(token);
	match.put(MongoDeviceAssignment.PROP_STATUS, status.name());
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection(getTenant());
	Document query = new Document(MongoDeviceAssignment.PROP_TOKEN, token);
	MongoPersistence.update(assignments, query, match);
	DeviceAssignment updated = MongoDeviceAssignment.fromDocument(match);

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
     * com.sitewhere.spi.device.IDeviceManagement#endDeviceAssignment(java.lang
     * .String)
     */
    @Override
    public IDeviceAssignment endDeviceAssignment(String token) throws SiteWhereException {
	Document match = assertDeviceAssignment(token);
	match.put(MongoDeviceAssignment.PROP_RELEASED_DATE, Calendar.getInstance().getTime());
	match.put(MongoDeviceAssignment.PROP_STATUS, DeviceAssignmentStatus.Released.name());
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection(getTenant());
	Document query = new Document(MongoDeviceAssignment.PROP_TOKEN, token);
	MongoPersistence.update(assignments, query, match);

	// Update cache with new assignment data.
	if (getCacheProvider() != null) {
	    DeviceAssignment updated = MongoDeviceAssignment.fromDocument(match);
	    getCacheProvider().getDeviceAssignmentCache().put(updated.getToken(), updated);
	}

	// Remove device assignment reference.
	MongoCollection<Document> devices = getMongoClient().getDevicesCollection(getTenant());
	String hardwareId = (String) match.get(MongoDeviceAssignment.PROP_DEVICE_HARDWARE_ID);
	Document deviceMatch = getDeviceDocumentByHardwareId(hardwareId);
	deviceMatch.put(MongoDevice.PROP_ASSIGNMENT_TOKEN, null);
	query = new Document(MongoDevice.PROP_HARDWARE_ID, hardwareId);
	MongoPersistence.update(devices, query, deviceMatch);

	// Update cache with new device data.
	if (getCacheProvider() != null) {
	    Device updated = MongoDevice.fromDocument(deviceMatch);
	    getCacheProvider().getDeviceCache().put(updated.getHardwareId(), updated);
	}

	DeviceAssignment assignment = MongoDeviceAssignment.fromDocument(match);
	return assignment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentHistory(
     * java.lang .String, com.sitewhere.spi.common.ISearchCriteria)
     */
    @Override
    public SearchResults<IDeviceAssignment> getDeviceAssignmentHistory(String hardwareId, ISearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection(getTenant());
	Document query = new Document(MongoDeviceAssignment.PROP_DEVICE_HARDWARE_ID, hardwareId);
	Document sort = new Document(MongoDeviceAssignment.PROP_ACTIVE_DATE, -1);
	return MongoPersistence.search(IDeviceAssignment.class, assignments, query, sort, criteria, LOOKUP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentsForSite(
     * java.lang.String,
     * com.sitewhere.spi.search.device.IAssignmentSearchCriteria)
     */
    @Override
    public SearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(String siteToken,
	    IAssignmentSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection(getTenant());
	Document query = new Document(MongoDeviceAssignment.PROP_SITE_TOKEN, siteToken);
	if (criteria.getStatus() != null) {
	    query.append(MongoDeviceAssignment.PROP_STATUS, criteria.getStatus().name());
	}
	Document sort = new Document(MongoDeviceAssignment.PROP_ACTIVE_DATE, -1);
	return MongoPersistence.search(IDeviceAssignment.class, assignments, query, sort, criteria, LOOKUP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentsForAsset(
     * java.lang.String, java.lang.String,
     * com.sitewhere.spi.search.device.IAssignmentsForAssetSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForAsset(String assetModuleId, String assetId,
	    IAssignmentsForAssetSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection(getTenant());
	Document query = new Document(MongoDeviceAssignment.PROP_ASSET_MODULE_ID, assetModuleId)
		.append(MongoDeviceAssignment.PROP_ASSET_ID, assetId);
	if (criteria.getSiteToken() != null) {
	    query.append(MongoDeviceAssignment.PROP_SITE_TOKEN, criteria.getSiteToken());
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
	MongoCollection<Document> assignments = getMongoClient().getDeviceAssignmentsCollection(getTenant());
	Document query = new Document(MongoDeviceAssignment.PROP_TOKEN, token);
	return assignments.find(query).first();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceStream(java.lang.
     * String,
     * com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest)
     */
    @Override
    public IDeviceStream createDeviceStream(String assignmentToken, IDeviceStreamCreateRequest request)
	    throws SiteWhereException {
	// Use common logic so all backend implementations work the same.
	IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentToken);
	DeviceStream stream = SiteWherePersistence.deviceStreamCreateLogic(assignment, request);

	MongoCollection<Document> streams = getMongoClient().getStreamsCollection(getTenant());
	Document created = MongoDeviceStream.toDocument(stream);
	MongoPersistence.insert(streams, created, ErrorCode.DuplicateStreamId);
	return MongoDeviceStream.fromDocument(created);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceStream(java.lang.
     * String, java.lang.String)
     */
    @Override
    public IDeviceStream getDeviceStream(String assignmentToken, String streamId) throws SiteWhereException {
	Document dbStream = getDeviceStreamDocument(assignmentToken, streamId);
	if (dbStream == null) {
	    return null;
	}
	return MongoDeviceStream.fromDocument(dbStream);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceStreams(java.lang.
     * String, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStream> listDeviceStreams(String assignmentToken, ISearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> streams = getMongoClient().getStreamsCollection(getTenant());
	Document query = new Document(MongoDeviceStream.PROP_ASSIGNMENT_TOKEN, assignmentToken);
	Document sort = new Document(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IDeviceStream.class, streams, query, sort, criteria, LOOKUP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createSite(com.sitewhere.spi.
     * device. request.ISiteCreateRequest )
     */
    @Override
    public ISite createSite(ISiteCreateRequest request) throws SiteWhereException {
	// Use common logic so all backend implementations work the same.
	Site site = SiteWherePersistence.siteCreateLogic(request);

	MongoCollection<Document> sites = getMongoClient().getSitesCollection(getTenant());
	Document created = MongoSite.toDocument(site);
	MongoPersistence.insert(sites, created, ErrorCode.DeuplicateSiteToken);
	return MongoSite.fromDocument(created);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateSite(java.lang.String,
     * com.sitewhere.spi.device.request.ISiteCreateRequest)
     */
    @Override
    public ISite updateSite(String token, ISiteCreateRequest request) throws SiteWhereException {
	Document match = getSiteDocumentByToken(token);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidSiteToken, ErrorLevel.ERROR);
	}
	Site site = MongoSite.fromDocument(match);

	// Use common update logic.
	SiteWherePersistence.siteUpdateLogic(request, site);

	Document updated = MongoSite.toDocument(site);

	MongoCollection<Document> sites = getMongoClient().getSitesCollection(getTenant());
	Document query = new Document(MongoSite.PROP_TOKEN, token);
	MongoPersistence.update(sites, query, updated);
	if (getCacheProvider() != null) {
	    getCacheProvider().getSiteCache().put(token, site);
	}
	return MongoSite.fromDocument(updated);
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
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteSite(java.lang.String,
     * boolean)
     */
    @Override
    public ISite deleteSite(String siteToken, boolean force) throws SiteWhereException {
	Document existing = assertSite(siteToken);
	if (force) {
	    MongoCollection<Document> sites = getMongoClient().getSitesCollection(getTenant());
	    MongoPersistence.delete(sites, existing);
	    if (getCacheProvider() != null) {
		getCacheProvider().getSiteCache().remove(siteToken);
	    }
	    return MongoSite.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoSite.PROP_TOKEN, siteToken);
	    MongoCollection<Document> sites = getMongoClient().getSitesCollection(getTenant());
	    MongoPersistence.update(sites, query, existing);
	    if (getCacheProvider() != null) {
		getCacheProvider().getSiteCache().remove(siteToken);
	    }
	    return MongoSite.fromDocument(existing);
	}
    }

    /**
     * Get the DBObject containing site information that matches the given
     * token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getSiteDocumentByToken(String token) throws SiteWhereException {
	if (getCacheProvider() != null) {
	    ISite cached = getCacheProvider().getSiteCache().get(token);
	    if (cached != null) {
		return MongoSite.toDocument(cached);
	    }
	}
	MongoCollection<Document> sites = getMongoClient().getSitesCollection(getTenant());
	Document query = new Document(MongoSite.PROP_TOKEN, token);
	Document result = sites.find(query).first();
	if ((getCacheProvider() != null) && (result != null)) {
	    ISite site = MongoSite.fromDocument(result);
	    getCacheProvider().getSiteCache().put(token, site);
	}
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listSites(com.sitewhere.spi.
     * common. ISearchCriteria)
     */
    @Override
    public SearchResults<ISite> listSites(ISearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> sites = getMongoClient().getSitesCollection(getTenant());
	Document query = new Document();
	Document sort = new Document(MongoSite.PROP_NAME, 1);
	return MongoPersistence.search(ISite.class, sites, query, sort, criteria, LOOKUP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createZone(com.sitewhere.spi.
     * device. ISite, com.sitewhere.spi.device.request.IZoneCreateRequest)
     */
    @Override
    public IZone createZone(ISite site, IZoneCreateRequest request) throws SiteWhereException {
	Zone zone = SiteWherePersistence.zoneCreateLogic(request, site.getToken(), UUID.randomUUID().toString());

	MongoCollection<Document> zones = getMongoClient().getZonesCollection(getTenant());
	Document created = MongoZone.toDocument(zone);
	MongoPersistence.insert(zones, created, ErrorCode.DuplicateZoneToken);
	return MongoZone.fromDocument(created);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateZone(java.lang.String,
     * com.sitewhere.spi.device.request.IZoneCreateRequest)
     */
    @Override
    public IZone updateZone(String token, IZoneCreateRequest request) throws SiteWhereException {
	MongoCollection<Document> zones = getMongoClient().getZonesCollection(getTenant());
	Document match = assertZone(token);

	Zone zone = MongoZone.fromDocument(match);
	SiteWherePersistence.zoneUpdateLogic(request, zone);

	Document updated = MongoZone.toDocument(zone);

	Document query = new Document(MongoSite.PROP_TOKEN, token);
	MongoPersistence.update(zones, query, updated);
	return MongoZone.fromDocument(updated);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getZone(java.lang.String)
     */
    @Override
    public IZone getZone(String zoneToken) throws SiteWhereException {
	Document dbZone = getZoneDocumentByToken(zoneToken);
	if (dbZone != null) {
	    return MongoZone.fromDocument(dbZone);
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listZones(java.lang.String,
     * com.sitewhere.spi.common.ISearchCriteria)
     */
    @Override
    public SearchResults<IZone> listZones(String siteToken, ISearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> zones = getMongoClient().getZonesCollection(getTenant());
	Document query = new Document(MongoZone.PROP_SITE_TOKEN, siteToken);
	Document sort = new Document(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IZone.class, zones, query, sort, criteria, LOOKUP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteZone(java.lang.String,
     * boolean)
     */
    @Override
    public IZone deleteZone(String zoneToken, boolean force) throws SiteWhereException {
	Document existing = assertZone(zoneToken);
	if (force) {
	    MongoCollection<Document> zones = getMongoClient().getZonesCollection(getTenant());
	    MongoPersistence.delete(zones, existing);
	    return MongoZone.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoZone.PROP_TOKEN, zoneToken);
	    MongoCollection<Document> zones = getMongoClient().getZonesCollection(getTenant());
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
	DeviceGroup group = SiteWherePersistence.deviceGroupCreateLogic(request, uuid);

	MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection(getTenant());
	Document created = MongoDeviceGroup.toDocument(group);
	created.put(MongoDeviceGroup.PROP_LAST_INDEX, new Long(0));

	MongoPersistence.insert(groups, created, ErrorCode.DuplicateDeviceGroupToken);
	return MongoDeviceGroup.fromDocument(created);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceGroup(java.lang.
     * String, com.sitewhere.spi.device.request.IDeviceGroupCreateRequest)
     */
    @Override
    public IDeviceGroup updateDeviceGroup(String token, IDeviceGroupCreateRequest request) throws SiteWhereException {
	MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection(getTenant());
	Document match = assertDeviceGroup(token);

	DeviceGroup group = MongoDeviceGroup.fromDocument(match);
	SiteWherePersistence.deviceGroupUpdateLogic(request, group);

	Document updated = MongoDeviceGroup.toDocument(group);

	// Manually copy last index since it's not copied by default.
	updated.put(MongoDeviceGroup.PROP_LAST_INDEX, match.get(MongoDeviceGroup.PROP_LAST_INDEX));

	Document query = new Document(MongoDeviceGroup.PROP_TOKEN, token);
	MongoPersistence.update(groups, query, updated);
	return MongoDeviceGroup.fromDocument(updated);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceGroup(java.lang.
     * String)
     */
    @Override
    public IDeviceGroup getDeviceGroup(String token) throws SiteWhereException {
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
	MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection(getTenant());
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
	MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection(getTenant());
	Document dbCriteria = new Document(MongoDeviceGroup.PROP_ROLES, role);
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
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceGroup(java.lang.
     * String, boolean)
     */
    @Override
    public IDeviceGroup deleteDeviceGroup(String token, boolean force) throws SiteWhereException {
	Document existing = assertDeviceGroup(token);
	if (force) {
	    MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection(getTenant());
	    MongoPersistence.delete(groups, existing);

	    // Delete group elements as well.
	    MongoCollection<Document> elements = getMongoClient().getGroupElementsCollection(getTenant());
	    Document match = new Document(MongoDeviceGroupElement.PROP_GROUP_TOKEN, token);
	    MongoPersistence.delete(elements, match);

	    return MongoDeviceGroup.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoDeviceGroup.PROP_TOKEN, token);
	    MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection(getTenant());
	    MongoPersistence.update(groups, query, existing);
	    return MongoDeviceGroup.fromDocument(existing);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#addDeviceGroupElements(java.
     * lang.String, java.util.List, boolean)
     */
    @Override
    public List<IDeviceGroupElement> addDeviceGroupElements(String groupToken,
	    List<IDeviceGroupElementCreateRequest> elements, boolean ignoreDuplicates) throws SiteWhereException {
	List<IDeviceGroupElement> results = new ArrayList<IDeviceGroupElement>();
	for (IDeviceGroupElementCreateRequest request : elements) {
	    long index = MongoDeviceGroup.getNextGroupIndex(getMongoClient(), getTenant(), groupToken);
	    DeviceGroupElement element = SiteWherePersistence.deviceGroupElementCreateLogic(request, groupToken, index);
	    Document created = MongoDeviceGroupElement.toDocument(element);
	    try {
		MongoPersistence.insert(getMongoClient().getGroupElementsCollection(getTenant()), created,
			ErrorCode.DuplicateId);
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
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#removeDeviceGroupElements(java
     * .lang. String, java.util.List)
     */
    @Override
    public List<IDeviceGroupElement> removeDeviceGroupElements(String groupToken,
	    List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
	List<IDeviceGroupElement> deleted = new ArrayList<IDeviceGroupElement>();
	for (IDeviceGroupElementCreateRequest request : elements) {
	    Document match = new Document(MongoDeviceGroupElement.PROP_GROUP_TOKEN, groupToken)
		    .append(MongoDeviceGroupElement.PROP_TYPE, request.getType().name())
		    .append(MongoDeviceGroupElement.PROP_ELEMENT_ID, request.getElementId());
	    FindIterable<Document> found = getMongoClient().getGroupElementsCollection(getTenant()).find(match);
	    MongoCursor<Document> cursor = found.iterator();

	    while (cursor.hasNext()) {
		Document current = cursor.next();
		DeleteResult result = MongoPersistence.delete(getMongoClient().getGroupElementsCollection(getTenant()),
			current);
		if (result.getDeletedCount() > 0) {
		    deleted.add(MongoDeviceGroupElement.fromDocument(current));
		}
	    }
	}
	return deleted;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroupElements(java.
     * lang.String , com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public SearchResults<IDeviceGroupElement> listDeviceGroupElements(String groupToken, ISearchCriteria criteria)
	    throws SiteWhereException {
	Document match = new Document(MongoDeviceGroupElement.PROP_GROUP_TOKEN, groupToken);
	Document sort = new Document(MongoDeviceGroupElement.PROP_INDEX, 1);
	return MongoPersistence.search(IDeviceGroupElement.class,
		getMongoClient().getGroupElementsCollection(getTenant()), match, sort, criteria, LOOKUP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#createBatchOperation(com.
     * sitewhere.spi .device.request.IBatchOperationCreateRequest)
     */
    @Override
    public IBatchOperation createBatchOperation(IBatchOperationCreateRequest request) throws SiteWhereException {
	String uuid = ((request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString());
	BatchOperation batch = SiteWherePersistence.batchOperationCreateLogic(request, uuid);

	MongoCollection<Document> batches = getMongoClient().getBatchOperationsCollection(getTenant());
	Document created = MongoBatchOperation.toDocument(batch);
	MongoPersistence.insert(batches, created, ErrorCode.DuplicateBatchOperationToken);

	// Insert element for each hardware id.
	long index = 0;
	MongoCollection<Document> elements = getMongoClient().getBatchOperationElementsCollection(getTenant());
	for (String hardwareId : request.getHardwareIds()) {
	    BatchElement element = SiteWherePersistence.batchElementCreateLogic(batch.getToken(), hardwareId, ++index);
	    Document dbElement = MongoBatchElement.toDocument(element);
	    MongoPersistence.insert(elements, dbElement, ErrorCode.DuplicateBatchElement);
	}

	return MongoBatchOperation.fromDocument(created);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateBatchOperation(java.lang
     * .String, com.sitewhere.spi.device.request.IBatchOperationUpdateRequest)
     */
    @Override
    public IBatchOperation updateBatchOperation(String token, IBatchOperationUpdateRequest request)
	    throws SiteWhereException {
	MongoCollection<Document> batchops = getMongoClient().getBatchOperationsCollection(getTenant());
	Document match = assertBatchOperation(token);

	BatchOperation operation = MongoBatchOperation.fromDocument(match);
	SiteWherePersistence.batchOperationUpdateLogic(request, operation);

	Document updated = MongoBatchOperation.toDocument(operation);

	Document query = new Document(MongoBatchOperation.PROP_TOKEN, token);
	MongoPersistence.update(batchops, query, updated);
	return MongoBatchOperation.fromDocument(updated);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getBatchOperation(java.lang.
     * String)
     */
    @Override
    public IBatchOperation getBatchOperation(String token) throws SiteWhereException {
	Document found = getBatchOperationDocumentByToken(token);
	if (found != null) {
	    return MongoBatchOperation.fromDocument(found);
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listBatchOperations(boolean,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IBatchOperation> listBatchOperations(boolean includeDeleted, ISearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> ops = getMongoClient().getBatchOperationsCollection(getTenant());
	Document dbCriteria = new Document();
	if (!includeDeleted) {
	    MongoSiteWhereEntity.setDeleted(dbCriteria, false);
	}
	Document sort = new Document(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IBatchOperation.class, ops, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteBatchOperation(java.lang
     * .String, boolean)
     */
    @Override
    public IBatchOperation deleteBatchOperation(String token, boolean force) throws SiteWhereException {
	Document existing = assertBatchOperation(token);
	if (force) {
	    MongoCollection<Document> ops = getMongoClient().getBatchOperationsCollection(getTenant());
	    MongoPersistence.delete(ops, existing);

	    // Delete operation elements as well.
	    MongoCollection<Document> elements = getMongoClient().getBatchOperationElementsCollection(getTenant());
	    Document match = new Document(MongoBatchElement.PROP_BATCH_OPERATION_TOKEN, token);
	    MongoPersistence.delete(elements, match);

	    return MongoBatchOperation.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoDeviceGroup.PROP_TOKEN, token);
	    MongoCollection<Document> ops = getMongoClient().getBatchOperationsCollection(getTenant());
	    MongoPersistence.update(ops, query, existing);
	    return MongoBatchOperation.fromDocument(existing);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listBatchElements(java.lang.
     * String, com.sitewhere.spi.search.device.IBatchElementSearchCriteria)
     */
    @Override
    public SearchResults<IBatchElement> listBatchElements(String batchToken, IBatchElementSearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> elements = getMongoClient().getBatchOperationElementsCollection(getTenant());
	Document dbCriteria = new Document(MongoBatchElement.PROP_BATCH_OPERATION_TOKEN, batchToken);
	if (criteria.getProcessingStatus() != null) {
	    dbCriteria.put(MongoBatchElement.PROP_PROCESSING_STATUS, criteria.getProcessingStatus());
	}
	Document sort = new Document(MongoBatchElement.PROP_INDEX, 1);
	return MongoPersistence.search(IBatchElement.class, elements, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateBatchElement(java.lang.
     * String, long,
     * com.sitewhere.spi.device.request.IBatchElementUpdateRequest)
     */
    @Override
    public IBatchElement updateBatchElement(String operationToken, long index, IBatchElementUpdateRequest request)
	    throws SiteWhereException {
	MongoCollection<Document> elements = getMongoClient().getBatchOperationElementsCollection(getTenant());
	Document dbElement = assertBatchElement(operationToken, index);

	BatchElement element = MongoBatchElement.fromDocument(dbElement);
	SiteWherePersistence.batchElementUpdateLogic(request, element);

	Document updated = MongoBatchElement.toDocument(element);

	Document query = new Document(MongoBatchElement.PROP_BATCH_OPERATION_TOKEN, operationToken)
		.append(MongoBatchElement.PROP_INDEX, index);
	MongoPersistence.update(elements, query, updated);
	return MongoBatchElement.fromDocument(updated);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createBatchCommandInvocation(
     * com. sitewhere .spi.device.request.IBatchCommandInvocationRequest)
     */
    @Override
    public IBatchOperation createBatchCommandInvocation(IBatchCommandInvocationRequest request)
	    throws SiteWhereException {
	String uuid = ((request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString());
	IBatchOperationCreateRequest generic = SiteWherePersistence.batchCommandInvocationCreateLogic(request, uuid);
	return createBatchOperation(generic);
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
     * Return the {@link Document} for the device with the given hardware id.
     * Throws an exception if the hardware id is not found.
     * 
     * @param hardwareId
     * @return
     * @throws SiteWhereException
     */
    protected Document assertDevice(String hardwareId) throws SiteWhereException {
	if (getCacheProvider() != null) {

	}
	Document match = getDeviceDocumentByHardwareId(hardwareId);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.INFO);
	}
	return match;
    }

    /**
     * Return the {@link Document} for the assignment with the given token.
     * Throws an exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document assertDeviceAssignment(String token) throws SiteWhereException {
	Document match = getDeviceAssignmentDocumentByToken(token);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Return an {@link IDeviceAssignment} for the given token. Throws an
     * exception if the token is not valid.
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
	Document match = assertDeviceAssignment(token);
	IDeviceAssignment result = MongoDeviceAssignment.fromDocument(match);
	if ((getCacheProvider() != null) && (result != null)) {
	    getCacheProvider().getDeviceAssignmentCache().put(token, result);
	}
	return result;
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
	    MongoCollection<Document> zones = getMongoClient().getZonesCollection(getTenant());
	    Document query = new Document(MongoZone.PROP_TOKEN, token);
	    return zones.find(query).first();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
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
     * Get the {@link Document} for an {@link IDeviceStream} based on assignment
     * token and stream id.
     * 
     * @param assignmentToken
     * @param streamId
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceStreamDocument(String assignmentToken, String streamId) throws SiteWhereException {
	try {
	    MongoCollection<Document> streams = getMongoClient().getStreamsCollection(getTenant());
	    Document query = new Document(MongoDeviceStream.PROP_ASSIGNMENT_TOKEN, assignmentToken)
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
	    MongoCollection<Document> groups = getMongoClient().getDeviceGroupsCollection(getTenant());
	    Document query = new Document(MongoDeviceGroup.PROP_TOKEN, token);
	    return groups.find(query).first();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    /**
     * Return the {@link Document} for the device group with the given token.
     * Throws an exception if the token is not valid.
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
     * Returns the {@link Document} for the batch operation with the given
     * token. Returns null if not found.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getBatchOperationDocumentByToken(String token) throws SiteWhereException {
	try {
	    MongoCollection<Document> ops = getMongoClient().getBatchOperationsCollection(getTenant());
	    Document query = new Document(MongoBatchOperation.PROP_TOKEN, token);
	    return ops.find(query).first();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    /**
     * Return the {@link Document} for the batch operation with the given token.
     * Throws an exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document assertBatchOperation(String token) throws SiteWhereException {
	Document match = getBatchOperationDocumentByToken(token);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidBatchOperationToken, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Return the {@link Document} for the batch operation element based on the
     * token for its parent operation and its index.
     * 
     * @param operationToken
     * @param index
     * @return
     * @throws SiteWhereException
     */
    protected Document getBatchElementDocumentByIndex(String operationToken, long index) throws SiteWhereException {
	try {
	    MongoCollection<Document> ops = getMongoClient().getBatchOperationElementsCollection(getTenant());
	    Document query = new Document(MongoBatchElement.PROP_BATCH_OPERATION_TOKEN, operationToken)
		    .append(MongoBatchElement.PROP_INDEX, index);
	    return ops.find(query).first();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    protected Document assertBatchElement(String operationToken, long index) throws SiteWhereException {
	Document match = getBatchElementDocumentByIndex(operationToken, index);
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
}