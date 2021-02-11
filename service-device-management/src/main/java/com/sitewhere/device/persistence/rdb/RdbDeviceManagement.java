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
package com.sitewhere.device.persistence.rdb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.sitewhere.device.DeviceManagementUtils;
import com.sitewhere.device.microservice.DeviceManagementMicroservice;
import com.sitewhere.device.persistence.DeviceManagementPersistence;
import com.sitewhere.device.persistence.TreeBuilder;
import com.sitewhere.device.persistence.rdb.entity.Queries;
import com.sitewhere.device.persistence.rdb.entity.RdbArea;
import com.sitewhere.device.persistence.rdb.entity.RdbAreaBoundary;
import com.sitewhere.device.persistence.rdb.entity.RdbAreaType;
import com.sitewhere.device.persistence.rdb.entity.RdbCommandParameter;
import com.sitewhere.device.persistence.rdb.entity.RdbCustomer;
import com.sitewhere.device.persistence.rdb.entity.RdbCustomerType;
import com.sitewhere.device.persistence.rdb.entity.RdbDevice;
import com.sitewhere.device.persistence.rdb.entity.RdbDeviceAlarm;
import com.sitewhere.device.persistence.rdb.entity.RdbDeviceAssignment;
import com.sitewhere.device.persistence.rdb.entity.RdbDeviceAssignmentSummary;
import com.sitewhere.device.persistence.rdb.entity.RdbDeviceCommand;
import com.sitewhere.device.persistence.rdb.entity.RdbDeviceGroup;
import com.sitewhere.device.persistence.rdb.entity.RdbDeviceGroupElement;
import com.sitewhere.device.persistence.rdb.entity.RdbDeviceStatus;
import com.sitewhere.device.persistence.rdb.entity.RdbDeviceSummary;
import com.sitewhere.device.persistence.rdb.entity.RdbDeviceType;
import com.sitewhere.device.persistence.rdb.entity.RdbLocation;
import com.sitewhere.device.persistence.rdb.entity.RdbZone;
import com.sitewhere.device.persistence.rdb.entity.RdbZoneBoundary;
import com.sitewhere.device.spi.microservice.IDeviceManagementTenantEngine;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.rdb.RdbTenantComponent;
import com.sitewhere.rdb.spi.IRdbEntityManagerProvider;
import com.sitewhere.rdb.spi.IRdbQueryProvider;
import com.sitewhere.rdb.spi.ITransactionCallback;
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
import com.sitewhere.rest.model.search.area.AreaSearchCriteria;
import com.sitewhere.rest.model.search.customer.CustomerSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceCommandSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceSearchCriteria;
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
import com.sitewhere.spi.common.ILocation;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.customer.ICustomerType;
import com.sitewhere.spi.customer.request.ICustomerCreateRequest;
import com.sitewhere.spi.customer.request.ICustomerTypeCreateRequest;
import com.sitewhere.spi.device.DeviceAlarmState;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAlarm;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceAssignmentSummary;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.IDeviceSummary;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.ICommandParameter;
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
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.ITreeNode;
import com.sitewhere.spi.search.area.IAreaSearchCriteria;
import com.sitewhere.spi.search.customer.ICustomerSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceAlarmSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceAssignmentSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceCommandSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceStatusSearchCriteria;
import com.sitewhere.spi.search.device.IZoneSearchCriteria;

/**
 * Device management implementation that uses a relational database for
 * persistence.
 */
public class RdbDeviceManagement extends RdbTenantComponent implements IDeviceManagement {

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#createDeviceType(com.
     * sitewhere.spi.device.request.IDeviceTypeCreateRequest)
     */
    @Override
    public RdbDeviceType createDeviceType(IDeviceTypeCreateRequest request) throws SiteWhereException {
	RdbDeviceType existing = getDeviceTypeByToken(request.getToken());
	if (existing != null) {
	    throw new SiteWhereException(
		    String.format("Another device type is already using token '%s'.", request.getToken()));
	}
	DeviceType deviceType = DeviceManagementPersistence.deviceTypeCreateLogic(request);
	RdbDeviceType created = new RdbDeviceType();
	RdbDeviceType.copy(deviceType, created);
	return getEntityManagerProvider().persist(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getDeviceType(java.
     * util.UUID)
     */
    @Override
    public RdbDeviceType getDeviceType(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().findById(id, RdbDeviceType.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getDeviceTypeByToken(
     * java.lang.String)
     */
    @Override
    public RdbDeviceType getDeviceTypeByToken(String token) throws SiteWhereException {
	Query query = getEntityManagerProvider().query(Queries.QUERY_DEVICE_TYPE_BY_TOKEN);
	query.setParameter("token", token);
	return getEntityManagerProvider().findOne(query, RdbDeviceType.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#updateDeviceType(java
     * .util.UUID, com.sitewhere.spi.device.request.IDeviceTypeCreateRequest)
     */
    @Override
    public RdbDeviceType updateDeviceType(UUID id, IDeviceTypeCreateRequest request) throws SiteWhereException {
	RdbDeviceType existing = getEntityManagerProvider().findById(id, RdbDeviceType.class);
	if (existing != null) {
	    DeviceType updates = new DeviceType();

	    // Use common update logic.
	    DeviceManagementPersistence.deviceTypeUpdateLogic(request, updates);
	    RdbDeviceType.copy(updates, existing);
	    return getEntityManagerProvider().merge(existing);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#listDeviceTypes(com.
     * sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<RdbDeviceType> listDeviceTypes(ISearchCriteria criteria) throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbDeviceType>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbDeviceType> root)
		    throws SiteWhereException {
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbDeviceType> addSort(CriteriaBuilder cb, Root<RdbDeviceType> root,
		    CriteriaQuery<RdbDeviceType> query) {
		return query.orderBy(cb.asc(root.get("name")));
	    }
	}, RdbDeviceType.class);
    }

    /**
     * Validate that device type does not have existing devices before deleting it.
     * 
     * @param existing
     * @throws SiteWhereException
     */
    protected void validateNoExistingDevices(RdbDeviceType existing) throws SiteWhereException {
	DeviceSearchCriteria criteria = new DeviceSearchCriteria(1, 0, null, null);
	criteria.setDeviceTypeToken(existing.getToken());
	ISearchResults<RdbDevice> devices = listDevices(criteria);
	if (devices.getNumResults() > 0) {
	    throw new SiteWhereException(
		    String.format("Device type is used by %s devices. Devices must be deleted before deleting type.",
			    String.valueOf(devices.getNumResults())));
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#deleteDeviceType(java
     * .util.UUID)
     */
    @Override
    public RdbDeviceType deleteDeviceType(UUID id) throws SiteWhereException {
	RdbDeviceType existing = getDeviceType(id);
	if (existing == null) {
	    throw new SiteWhereException("Invalid device type id.");
	}
	validateNoExistingDevices(existing);
	return getEntityManagerProvider().remove(id, RdbDeviceType.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#createDeviceCommand(
     * com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public RdbDeviceCommand createDeviceCommand(IDeviceCommandCreateRequest request) throws SiteWhereException {
	// Look up device type.
	if (request.getDeviceTypeToken() == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}
	IDeviceType deviceType = getDeviceTypeByToken(request.getDeviceTypeToken());

	// Look up existing commands.
	DeviceCommandSearchCriteria criteria = new DeviceCommandSearchCriteria(1, 0);
	criteria.setDeviceTypeToken(request.getDeviceTypeToken());
	ISearchResults<? extends IDeviceCommand> all = listDeviceCommands(criteria);

	DeviceCommand command = DeviceManagementPersistence.deviceCommandCreateLogic(deviceType, request,
		all.getResults());
	RdbDeviceCommand created = new RdbDeviceCommand();
	RdbDeviceCommand.copy(command, created);
	created = getEntityManagerProvider().persist(created);

	// Create and add parameters.
	for (ICommandParameter param : request.getParameters()) {
	    RdbCommandParameter rdbParam = new RdbCommandParameter(null, param.getName(), param.getType(),
		    param.isRequired());
	    rdbParam.setDeviceCommand(created);
	    created.getParameters().add(rdbParam);
	}
	return getEntityManagerProvider().merge(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getDeviceCommand(java
     * .util.UUID)
     */
    @Override
    public RdbDeviceCommand getDeviceCommand(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().findById(id, RdbDeviceCommand.class);
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#
     * getDeviceCommandByToken(java.util.UUID, java.lang.String)
     */
    @Override
    public RdbDeviceCommand getDeviceCommandByToken(UUID deviceTypeId, String token) throws SiteWhereException {
	Query query = getEntityManagerProvider().query(Queries.QUERY_DEVICE_COMMAND_BY_TOKEN);
	query.setParameter("token", token);
	return getEntityManagerProvider().findOne(query, RdbDeviceCommand.class);
    }

    /**
     * Merge parameter updates into persistent state.
     * 
     * @param request
     * @param existing
     * @throws SiteWhereException
     */
    protected void mergeDeviceCommandParameters(IDeviceCommandCreateRequest request, RdbDeviceCommand existing)
	    throws SiteWhereException {
	Map<String, ICommandParameter> updatedByName = new HashMap<>();
	for (ICommandParameter updated : request.getParameters()) {
	    updatedByName.put(updated.getName(), updated);
	}

	List<RdbCommandParameter> toDelete = new ArrayList<>();
	Map<String, RdbCommandParameter> existingByName = new HashMap<>();
	for (RdbCommandParameter rdb : existing.getParameters()) {
	    if (updatedByName.get(rdb.getName()) == null) {
		toDelete.add(rdb);
	    }
	    existingByName.put(rdb.getName(), rdb);
	}
	// Deleting parameters that are no longer used.
	for (RdbCommandParameter parameter : toDelete) {
	    getLogger().debug(
		    String.format("Deleting parameter '%s' with id %s.", parameter.getName(), parameter.getId()));
	    getEntityManagerProvider().remove(parameter.getId(), RdbCommandParameter.class);
	    existing.getParameters().remove(parameter);
	}

	for (ICommandParameter updated : request.getParameters()) {
	    if (existingByName.get(updated.getName()) == null) {
		getLogger().debug(String.format("Creating parameter '%s'.", updated.getName()));
		RdbCommandParameter created = new RdbCommandParameter(updated);
		created.setDeviceCommand(existing);
		existing.getParameters().add(getEntityManagerProvider().persist(created));
	    }
	    // Update existing parameter.
	    else {
		getLogger().debug(String.format("Updating parameter '%s'.", updated.getName()));
		RdbCommandParameter rdb = existingByName.get(updated.getName());
		rdb.setName(updated.getName());
		rdb.setType(updated.getType());
		rdb.setRequired(updated.isRequired());
		getEntityManagerProvider().persist(rdb);
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#updateDeviceCommand(
     * java.util.UUID, com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public RdbDeviceCommand updateDeviceCommand(UUID id, IDeviceCommandCreateRequest request)
	    throws SiteWhereException {
	RdbDeviceCommand existing = getEntityManagerProvider().findById(id, RdbDeviceCommand.class);
	if (existing == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceCommandId, ErrorLevel.ERROR);
	}
	// Look up device type.
	IDeviceType deviceType = request.getDeviceTypeToken() != null
		? getDeviceTypeByToken(request.getDeviceTypeToken())
		: null;

	// Look up existing commands.
	DeviceCommandSearchCriteria criteria = new DeviceCommandSearchCriteria(1, 0);
	criteria.setDeviceTypeToken(request.getDeviceTypeToken());
	ISearchResults<? extends IDeviceCommand> all = listDeviceCommands(criteria);

	// Use common update logic.
	return getEntityManagerProvider().runInTransaction(new ITransactionCallback<RdbDeviceCommand>() {

	    @Override
	    public RdbDeviceCommand process() throws SiteWhereException {
		DeviceCommand updates = new DeviceCommand();
		DeviceManagementPersistence.deviceCommandUpdateLogic(deviceType, request, updates, all.getResults());
		mergeDeviceCommandParameters(request, existing);
		RdbDeviceCommand.copy(updates, existing);
		return getEntityManagerProvider().merge(existing);
	    }
	});
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#listDeviceCommands(
     * com.sitewhere.spi.search.device.IDeviceCommandSearchCriteria)
     */
    @Override
    public ISearchResults<RdbDeviceCommand> listDeviceCommands(IDeviceCommandSearchCriteria criteria)
	    throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbDeviceCommand>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbDeviceCommand> root)
		    throws SiteWhereException {
		if (criteria.getDeviceTypeToken() != null) {
		    IDeviceType deviceType = getDeviceTypeByToken(criteria.getDeviceTypeToken());
		    if (deviceType == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
		    }
		    predicates.add(cb.equal(root.get("deviceTypeId"), deviceType.getId()));
		}
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbDeviceCommand> addSort(CriteriaBuilder cb, Root<RdbDeviceCommand> root,
		    CriteriaQuery<RdbDeviceCommand> query) {
		return query.orderBy(cb.asc(root.get("name")));
	    }
	}, RdbDeviceCommand.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#deleteDeviceCommand(
     * java.util.UUID)
     */
    @Override
    public RdbDeviceCommand deleteDeviceCommand(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().remove(id, RdbDeviceCommand.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#createDeviceStatus(
     * com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public RdbDeviceStatus createDeviceStatus(IDeviceStatusCreateRequest request) throws SiteWhereException {
	// Look up device type.
	if (request.getDeviceTypeToken() == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}
	IDeviceType deviceType = getDeviceTypeByToken(request.getDeviceTypeToken());

	// Get list of existing statuses.
	DeviceStatusSearchCriteria criteria = new DeviceStatusSearchCriteria(1, 0);
	criteria.setDeviceTypeToken(deviceType.getToken());
	ISearchResults<? extends IDeviceStatus> existing = listDeviceStatuses(criteria);

	// Use common logic so all backend implementations work the same.
	DeviceStatus status = DeviceManagementPersistence.deviceStatusCreateLogic(deviceType, request,
		existing.getResults());

	RdbDeviceStatus created = new RdbDeviceStatus();
	RdbDeviceStatus.copy(status, created);
	return getEntityManagerProvider().persist(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getDeviceStatus(java.
     * util.UUID)
     */
    @Override
    public RdbDeviceStatus getDeviceStatus(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().findById(id, RdbDeviceStatus.class);
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#
     * getDeviceStatusByToken(java.util.UUID, java.lang.String)
     */
    @Override
    public RdbDeviceStatus getDeviceStatusByToken(UUID deviceTypeId, String token) throws SiteWhereException {
	Query query = getEntityManagerProvider().query(Queries.QUERY_DEVICE_STATUS_BY_TOKEN);
	query.setParameter("token", token);
	return getEntityManagerProvider().findOne(query, RdbDeviceStatus.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#updateDeviceStatus(
     * java.util.UUID, com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public RdbDeviceStatus updateDeviceStatus(UUID id, IDeviceStatusCreateRequest request) throws SiteWhereException {
	RdbDeviceStatus existing = getEntityManagerProvider().findById(id, RdbDeviceStatus.class);
	if (existing != null) {
	    // Look up device type.
	    IDeviceType deviceType = null;
	    if (request.getDeviceTypeToken() == null) {
		deviceType = getDeviceTypeByToken(request.getDeviceTypeToken());
	    }

	    // Get list of existing statuses.
	    DeviceStatusSearchCriteria criteria = new DeviceStatusSearchCriteria(1, 0);
	    criteria.setDeviceTypeToken(deviceType.getToken());
	    ISearchResults<? extends IDeviceStatus> all = listDeviceStatuses(criteria);

	    // Use common update logic.
	    DeviceStatus updates = new DeviceStatus();
	    DeviceManagementPersistence.deviceStatusUpdateLogic(deviceType, request, updates, all.getResults());
	    RdbDeviceStatus.copy(updates, existing);
	    return getEntityManagerProvider().merge(existing);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#listDeviceStatuses(
     * com.sitewhere.spi.search.device.IDeviceStatusSearchCriteria)
     */
    @Override
    public ISearchResults<RdbDeviceStatus> listDeviceStatuses(IDeviceStatusSearchCriteria criteria)
	    throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbDeviceStatus>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbDeviceStatus> root)
		    throws SiteWhereException {
		if (criteria.getDeviceTypeToken() != null) {
		    IDeviceType deviceType = getDeviceTypeByToken(criteria.getDeviceTypeToken());
		    if (deviceType == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
		    }
		    predicates.add(cb.equal(root.get("deviceTypeId"), deviceType.getId()));
		}
		if (criteria.getCode() != null) {
		    predicates.add(cb.equal(root.get("code"), criteria.getCode()));
		}
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbDeviceStatus> addSort(CriteriaBuilder cb, Root<RdbDeviceStatus> root,
		    CriteriaQuery<RdbDeviceStatus> query) {
		return query.orderBy(cb.asc(root.get("name")));
	    }
	}, RdbDeviceStatus.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#deleteDeviceStatus(
     * java.util.UUID)
     */
    @Override
    public RdbDeviceStatus deleteDeviceStatus(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().remove(id, RdbDeviceStatus.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#createDevice(com.
     * sitewhere.spi.device.request.IDeviceCreateRequest)
     */
    @Override
    public RdbDevice createDevice(IDeviceCreateRequest request) throws SiteWhereException {
	RdbDeviceType deviceType = getDeviceTypeByToken(request.getDeviceTypeToken());
	if (deviceType == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}

	RdbDevice parent = null;
	if (request.getParentDeviceToken() != null) {
	    parent = getDeviceByToken(request.getParentDeviceToken());
	    if (parent == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidDeviceToken, ErrorLevel.ERROR);
	    }
	}

	Device newDevice = DeviceManagementPersistence.deviceCreateLogic(request, deviceType, parent);
	RdbDevice created = new RdbDevice();
	RdbDevice.copy(newDevice, created);
	return getEntityManagerProvider().persist(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getDevice(java.util.
     * UUID)
     */
    @Override
    public RdbDevice getDevice(UUID deviceId) throws SiteWhereException {
	return getEntityManagerProvider().findById(deviceId, RdbDevice.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getDeviceByToken(java
     * .lang.String)
     */
    @Override
    public RdbDevice getDeviceByToken(String token) throws SiteWhereException {
	Query query = getEntityManagerProvider().query(Queries.QUERY_DEVICE_BY_TOKEN);
	query.setParameter("token", token);
	return getEntityManagerProvider().findOne(query, RdbDevice.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#updateDevice(java.
     * util.UUID, com.sitewhere.spi.device.request.IDeviceCreateRequest)
     */
    @Override
    public RdbDevice updateDevice(UUID id, IDeviceCreateRequest request) throws SiteWhereException {
	RdbDevice existing = getEntityManagerProvider().findById(id, RdbDevice.class);
	if (existing != null) {
	    // Look up device type.
	    RdbDeviceType deviceType = null;
	    if (request.getDeviceTypeToken() != null) {
		deviceType = getDeviceTypeByToken(request.getDeviceTypeToken());
	    }

	    // Look up parent device.
	    RdbDevice parent = null;
	    if (request.getParentDeviceToken() != null) {
		parent = getDeviceByToken(request.getParentDeviceToken());
	    }

	    // Use common update logic.
	    Device updates = new Device();
	    DeviceManagementPersistence.deviceUpdateLogic(request, deviceType, parent, updates);
	    RdbDevice.copy(updates, existing);
	    return getEntityManagerProvider().merge(existing);
	}
	throw new SiteWhereSystemException(ErrorCode.InvalidDeviceId, ErrorLevel.ERROR);
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#listDevices(com.
     * sitewhere.spi.search.device.IDeviceSearchCriteria)
     */
    @Override
    public ISearchResults<RdbDevice> listDevices(IDeviceSearchCriteria criteria) throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbDevice>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbDevice> root)
		    throws SiteWhereException {
		if (criteria.isExcludeAssigned()) {
		    Path<List<UUID>> path = root.get("activeDeviceAssignmentIds");
		    predicates.add(cb.not(cb.size(path).isNull()));
		    predicates.add(cb.not(cb.size(path).in(0)));
		}

		if (!StringUtils.isEmpty(criteria.getDeviceTypeToken())) {
		    IDeviceType deviceType = getDeviceTypeByToken(criteria.getDeviceTypeToken());
		    if (deviceType == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
		    }
		    Path<UUID> path = root.get("deviceTypeId");
		    predicates.add(cb.equal(path, deviceType.getId()));
		}
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbDevice> addSort(CriteriaBuilder cb, Root<RdbDevice> root,
		    CriteriaQuery<RdbDevice> query) {
		return query.orderBy(cb.desc(root.get("createdDate")));
	    }
	}, RdbDevice.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#listDeviceSummaries(
     * com.sitewhere.spi.search.device.IDeviceSearchCriteria)
     */
    @Override
    public ISearchResults<? extends IDeviceSummary> listDeviceSummaries(IDeviceSearchCriteria criteria)
	    throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbDeviceSummary>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbDeviceSummary> root)
		    throws SiteWhereException {
		if (criteria.isExcludeAssigned()) {
		    Path<List<UUID>> path = root.get("activeDeviceAssignmentIds");
		    predicates.add(cb.not(cb.size(path).isNull()));
		    predicates.add(cb.not(cb.size(path).in(0)));
		}

		if (!StringUtils.isEmpty(criteria.getDeviceTypeToken())) {
		    IDeviceType deviceType = getDeviceTypeByToken(criteria.getDeviceTypeToken());
		    if (deviceType == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
		    }
		    Path<UUID> path = root.get("deviceTypeId");
		    predicates.add(cb.equal(path, deviceType.getId()));
		}
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbDeviceSummary> addSort(CriteriaBuilder cb, Root<RdbDeviceSummary> root,
		    CriteriaQuery<RdbDeviceSummary> query) {
		return query.orderBy(cb.desc(root.get("createdDate")));
	    }
	}, RdbDeviceSummary.class);
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#
     * createDeviceElementMapping(java.util.UUID,
     * com.sitewhere.spi.device.IDeviceElementMapping)
     */
    @Override
    public IDevice createDeviceElementMapping(UUID deviceId, IDeviceElementMapping mapping) throws SiteWhereException {
	IDevice device = getApiDeviceById(deviceId);
	return DeviceManagementPersistence.deviceElementMappingCreateLogic(this, device, mapping);
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#
     * deleteDeviceElementMapping(java.util.UUID, java.lang.String)
     */
    @Override
    public IDevice deleteDeviceElementMapping(UUID deviceId, String path) throws SiteWhereException {
	IDevice device = getApiDeviceById(deviceId);
	return DeviceManagementPersistence.deviceElementMappingDeleteLogic(this, device, path);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#deleteDevice(java.
     * util.UUID)
     */
    @Override
    public RdbDevice deleteDevice(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().remove(id, RdbDevice.class);
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#
     * createDeviceAssignment(com.sitewhere.spi.device.request.
     * IDeviceAssignmentCreateRequest)
     */
    @Override
    public RdbDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request)
	    throws SiteWhereException {
	// Require valid device.
	RdbDevice device = getDeviceByToken(request.getDeviceToken());
	if (device == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceToken, ErrorLevel.ERROR);
	}

	// Look up customer if specified.
	RdbCustomer customer = null;
	if (request.getCustomerToken() != null) {
	    customer = getCustomerByToken(request.getCustomerToken());
	    if (customer == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidCustomerToken, ErrorLevel.ERROR);
	    }
	}

	// Look up area if specified.
	RdbArea area = null;
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
		area, asset, device);

	RdbDeviceAssignment created = new RdbDeviceAssignment();
	RdbDeviceAssignment.copy(newAssignment, created);
	return getEntityManagerProvider().persist(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getDeviceAssignment(
     * java.util.UUID)
     */
    @Override
    public RdbDeviceAssignment getDeviceAssignment(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().findById(id, RdbDeviceAssignment.class);
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#
     * getDeviceAssignmentByToken(java.lang.String)
     */
    @Override
    public RdbDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
	Query query = getEntityManagerProvider().query(Queries.QUERY_DEVICE_ASSIGNMENT_BY_TOKEN);
	query.setParameter("token", token);
	return getEntityManagerProvider().findOne(query, RdbDeviceAssignment.class);
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#
     * getActiveDeviceAssignments(java.util.UUID)
     */
    @Override
    public List<RdbDeviceAssignment> getActiveDeviceAssignments(UUID deviceId) throws SiteWhereException {
	Query query = getEntityManagerProvider().query(Queries.QUERY_DEVICE_ASSIGNMENT_BY_DEVICE_AND_STATUS);
	query.setParameter("deviceId", deviceId);
	query.setParameter("status", DeviceAssignmentStatus.Active);
	return getEntityManagerProvider().findMany(query, RdbDeviceAssignment.class);
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#
     * updateDeviceAssignment(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest)
     */
    @Override
    public RdbDeviceAssignment updateDeviceAssignment(UUID id, IDeviceAssignmentCreateRequest request)
	    throws SiteWhereException {
	RdbDeviceAssignment existing = getEntityManagerProvider().findById(id, RdbDeviceAssignment.class);
	if (existing != null) {
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

	    // Use common update logic.
	    DeviceAssignment updates = new DeviceAssignment();
	    DeviceManagementPersistence.deviceAssignmentUpdateLogic(device, customer, area, asset, request, updates);
	    RdbDeviceAssignment.copy(updates, existing);
	    return getEntityManagerProvider().merge(existing);
	}
	throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentId, ErrorLevel.ERROR);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#listDeviceAssignments
     * (com.sitewhere.spi.search.device.IDeviceAssignmentSearchCriteria)
     */
    @Override
    public ISearchResults<RdbDeviceAssignment> listDeviceAssignments(IDeviceAssignmentSearchCriteria criteria)
	    throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbDeviceAssignment>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbDeviceAssignment> root)
		    throws SiteWhereException {
		if ((criteria.getAssignmentStatuses() != null) && (criteria.getAssignmentStatuses().size() > 0)) {
		    Path<DeviceAssignmentStatus> path = root.get("status");
		    predicates.add(path.in(criteria.getAssignmentStatuses()));
		}
		if ((criteria.getDeviceTokens() != null) && (criteria.getDeviceTokens().size() > 0)) {
		    try {
			List<UUID> ids = DeviceManagementUtils.getDeviceIds(criteria.getDeviceTokens(),
				RdbDeviceManagement.this);
			Path<UUID> path = root.get("deviceId");
			predicates.add(path.in(ids));
		    } catch (SiteWhereException e) {
			throw new SiteWhereException("Unable to look up device ids.", e);
		    }
		}
		if ((criteria.getCustomerTokens() != null) && (criteria.getCustomerTokens().size() > 0)) {
		    try {
			List<UUID> ids = DeviceManagementUtils.getCustomerIds(criteria.getCustomerTokens(),
				RdbDeviceManagement.this);
			Path<UUID> path = root.get("customerId");
			predicates.add(path.in(ids));
		    } catch (SiteWhereException e) {
			throw new SiteWhereException("Unable to look up customer ids.", e);
		    }
		}
		if ((criteria.getAreaTokens() != null) && (criteria.getAreaTokens().size() > 0)) {
		    try {
			List<UUID> ids = DeviceManagementUtils.getAreaIds(criteria.getAreaTokens(),
				RdbDeviceManagement.this);
			Path<UUID> path = root.get("areaId");
			predicates.add(path.in(ids));
		    } catch (SiteWhereException e) {
			throw new SiteWhereException("Unable to look up area ids.", e);
		    }
		}
		if ((criteria.getAssetTokens() != null) && (criteria.getAssetTokens().size() > 0)) {
		    try {
			List<UUID> ids = getAssetIds(criteria.getAssetTokens());
			Path<UUID> path = root.get("assetId");
			predicates.add(path.in(ids));
		    } catch (SiteWhereException e) {
			throw new SiteWhereException("Unable to look up asset ids.", e);
		    }
		}
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbDeviceAssignment> addSort(CriteriaBuilder cb, Root<RdbDeviceAssignment> root,
		    CriteriaQuery<RdbDeviceAssignment> query) {
		return query.orderBy(cb.desc(root.get("createdDate")));
	    }
	}, RdbDeviceAssignment.class);
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#
     * listDeviceAssignmentSummaries(com.sitewhere.spi.search.device.
     * IDeviceAssignmentSearchCriteria)
     */
    @Override
    public ISearchResults<? extends IDeviceAssignmentSummary> listDeviceAssignmentSummaries(
	    IDeviceAssignmentSearchCriteria criteria) throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria,
		new IRdbQueryProvider<RdbDeviceAssignmentSummary>() {

		    /*
		     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
		     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
		     */
		    @Override
		    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates,
			    Root<RdbDeviceAssignmentSummary> root) throws SiteWhereException {
			if ((criteria.getAssignmentStatuses() != null)
				&& (criteria.getAssignmentStatuses().size() > 0)) {
			    Path<DeviceAssignmentStatus> path = root.get("status");
			    predicates.add(path.in(criteria.getAssignmentStatuses()));
			}
			if ((criteria.getDeviceTokens() != null) && (criteria.getDeviceTokens().size() > 0)) {
			    try {
				List<UUID> ids = DeviceManagementUtils.getDeviceIds(criteria.getDeviceTokens(),
					RdbDeviceManagement.this);
				Path<UUID> path = root.get("deviceId");
				predicates.add(path.in(ids));
			    } catch (SiteWhereException e) {
				throw new SiteWhereException("Unable to look up device ids.", e);
			    }
			}
			if ((criteria.getCustomerTokens() != null) && (criteria.getCustomerTokens().size() > 0)) {
			    try {
				List<UUID> ids = DeviceManagementUtils.getCustomerIds(criteria.getCustomerTokens(),
					RdbDeviceManagement.this);
				Path<UUID> path = root.get("customerId");
				predicates.add(path.in(ids));
			    } catch (SiteWhereException e) {
				throw new SiteWhereException("Unable to look up customer ids.", e);
			    }
			}
			if ((criteria.getAreaTokens() != null) && (criteria.getAreaTokens().size() > 0)) {
			    try {
				List<UUID> ids = DeviceManagementUtils.getAreaIds(criteria.getAreaTokens(),
					RdbDeviceManagement.this);
				Path<UUID> path = root.get("areaId");
				predicates.add(path.in(ids));
			    } catch (SiteWhereException e) {
				throw new SiteWhereException("Unable to look up area ids.", e);
			    }
			}
			if ((criteria.getAssetTokens() != null) && (criteria.getAssetTokens().size() > 0)) {
			    try {
				List<UUID> ids = getAssetIds(criteria.getAssetTokens());
				Path<UUID> path = root.get("assetId");
				predicates.add(path.in(ids));
			    } catch (SiteWhereException e) {
				throw new SiteWhereException("Unable to look up asset ids.", e);
			    }
			}
		    }

		    /*
		     * @see
		     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
		     * CriteriaBuilder, javax.persistence.criteria.Root,
		     * javax.persistence.criteria.CriteriaQuery)
		     */
		    @Override
		    public CriteriaQuery<RdbDeviceAssignmentSummary> addSort(CriteriaBuilder cb,
			    Root<RdbDeviceAssignmentSummary> root, CriteriaQuery<RdbDeviceAssignmentSummary> query) {
			return query.orderBy(cb.desc(root.get("createdDate")));
		    }
		}, RdbDeviceAssignmentSummary.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#endDeviceAssignment(
     * java.util.UUID)
     */
    @Override
    public RdbDeviceAssignment endDeviceAssignment(UUID id) throws SiteWhereException {
	RdbDeviceAssignment existing = getEntityManagerProvider().findById(id, RdbDeviceAssignment.class);
	if (existing != null) {
	    existing.setReleasedDate(Calendar.getInstance().getTime());
	    existing.setStatus(DeviceAssignmentStatus.Released);
	    return getEntityManagerProvider().merge(existing);
	}
	return null;
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#
     * deleteDeviceAssignment(java.util.UUID)
     */
    @Override
    public RdbDeviceAssignment deleteDeviceAssignment(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().remove(id, RdbDeviceAssignment.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#createDeviceAlarm(com
     * .sitewhere.spi.device.request.IDeviceAlarmCreateRequest)
     */
    @Override
    public RdbDeviceAlarm createDeviceAlarm(IDeviceAlarmCreateRequest request) throws SiteWhereException {
	IDeviceAssignment assignment = getDeviceAssignmentByToken(request.getDeviceAssignmentToken());
	if (assignment == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
	}

	// Use common logic to load assignment from request.
	DeviceAlarm alarm = DeviceManagementPersistence.deviceAlarmCreateLogic(assignment, request);
	RdbDeviceAlarm created = new RdbDeviceAlarm();
	RdbDeviceAlarm.copy(alarm, created);
	return getEntityManagerProvider().persist(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#updateDeviceAlarm(
     * java.util.UUID, com.sitewhere.spi.device.request.IDeviceAlarmCreateRequest)
     */
    @Override
    public IDeviceAlarm updateDeviceAlarm(UUID id, IDeviceAlarmCreateRequest request) throws SiteWhereException {
	RdbDeviceAlarm existing = getEntityManagerProvider().findById(id, RdbDeviceAlarm.class);
	if (existing != null) {
	    IDeviceAssignment assignment = getDeviceAssignmentByToken(request.getDeviceAssignmentToken());

	    // Use common update logic.
	    DeviceAlarm updates = new DeviceAlarm();
	    DeviceManagementPersistence.deviceAlarmUpdateLogic(assignment, request, updates);
	    RdbDeviceAlarm.copy(updates, existing);
	    return getEntityManagerProvider().merge(existing);
	}
	throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAlarmId, ErrorLevel.ERROR);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getDeviceAlarm(java.
     * util.UUID)
     */
    @Override
    public IDeviceAlarm getDeviceAlarm(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().findById(id, RdbDeviceAlarm.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#searchDeviceAlarms(
     * com.sitewhere.spi.search.device.IDeviceAlarmSearchCriteria)
     */
    @Override
    public ISearchResults<RdbDeviceAlarm> searchDeviceAlarms(IDeviceAlarmSearchCriteria criteria)
	    throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbDeviceAlarm>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbDeviceAlarm> root)
		    throws SiteWhereException {
		if (criteria.getDeviceId() != null) {
		    Path<UUID> path = root.get("deviceId");
		    predicates.add(cb.equal(path, criteria.getDeviceId()));
		}
		if (criteria.getDeviceAssignmentId() != null) {
		    Path<UUID> path = root.get("deviceAssignmentId");
		    predicates.add(cb.equal(path, criteria.getDeviceAssignmentId()));
		}
		if (criteria.getCustomerId() != null) {
		    Path<UUID> path = root.get("customerId");
		    predicates.add(cb.equal(path, criteria.getCustomerId()));
		}
		if (criteria.getAreaId() != null) {
		    Path<UUID> path = root.get("areaId");
		    predicates.add(cb.equal(path, criteria.getAreaId()));
		}
		if (criteria.getAssetId() != null) {
		    Path<UUID> path = root.get("assetId");
		    predicates.add(cb.equal(path, criteria.getAssetId()));
		}
		if (criteria.getState() != null) {
		    Path<DeviceAlarmState> path = root.get("state");
		    predicates.add(cb.equal(path, criteria.getState()));
		}
		if (criteria.getTriggeringEventId() != null) {
		    Path<UUID> path = root.get("triggeringEventId");
		    predicates.add(cb.equal(path, criteria.getTriggeringEventId()));
		}
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbDeviceAlarm> addSort(CriteriaBuilder cb, Root<RdbDeviceAlarm> root,
		    CriteriaQuery<RdbDeviceAlarm> query) {
		return query.orderBy(cb.desc(root.get("createdDate")));
	    }
	}, RdbDeviceAlarm.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#deleteDeviceAlarm(
     * java.util.UUID)
     */
    @Override
    public IDeviceAlarm deleteDeviceAlarm(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().remove(id, RdbDeviceAlarm.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#createCustomerType(
     * com.sitewhere.spi.customer.request.ICustomerTypeCreateRequest)
     */
    @Override
    public ICustomerType createCustomerType(ICustomerTypeCreateRequest request) throws SiteWhereException {
	List<RdbCustomerType> contained = new ArrayList<>();
	if (request.getContainedCustomerTypeTokens() != null) {
	    for (String token : request.getContainedCustomerTypeTokens()) {
		contained.add(getCustomerTypeByToken(token));
	    }
	}

	// Use common logic to load assignment from request.
	CustomerType customerType = DeviceManagementPersistence.customerTypeCreateLogic(request);
	RdbCustomerType created = new RdbCustomerType();
	RdbCustomerType.copy(customerType, created);
	created.setContainedCustomerTypes(contained);
	return getEntityManagerProvider().persist(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getCustomerType(java.
     * util.UUID)
     */
    @Override
    public ICustomerType getCustomerType(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().findById(id, RdbCustomerType.class);
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#
     * getCustomerTypeByToken(java.lang.String)
     */
    @Override
    public RdbCustomerType getCustomerTypeByToken(String token) throws SiteWhereException {
	Query query = getEntityManagerProvider().query(Queries.QUERY_CUSTOMER_TYPE_BY_TOKEN);
	query.setParameter("token", token);
	return getEntityManagerProvider().findOne(query, RdbCustomerType.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#updateCustomerType(
     * java.util.UUID,
     * com.sitewhere.spi.customer.request.ICustomerTypeCreateRequest)
     */
    @Override
    public ICustomerType updateCustomerType(UUID id, ICustomerTypeCreateRequest request) throws SiteWhereException {
	RdbCustomerType existing = getEntityManagerProvider().findById(id, RdbCustomerType.class);
	if (existing != null) {
	    List<RdbCustomerType> contained = null;
	    if (request.getContainedCustomerTypeTokens() != null) {
		contained = new ArrayList<>();
		for (String token : request.getContainedCustomerTypeTokens()) {
		    contained.add(getCustomerTypeByToken(token));
		}
	    }

	    // Use common update logic.
	    CustomerType updates = new CustomerType();
	    DeviceManagementPersistence.customerTypeUpdateLogic(request, updates);
	    RdbCustomerType.copy(updates, existing);
	    if (contained != null) {
		existing.setContainedCustomerTypes(contained);
	    }
	    return getEntityManagerProvider().merge(existing);
	}
	throw new SiteWhereSystemException(ErrorCode.InvalidCustomerTypeToken, ErrorLevel.ERROR);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#listCustomerTypes(com
     * .sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<RdbCustomerType> listCustomerTypes(ISearchCriteria criteria) throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbCustomerType>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbCustomerType> root)
		    throws SiteWhereException {
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbCustomerType> addSort(CriteriaBuilder cb, Root<RdbCustomerType> root,
		    CriteriaQuery<RdbCustomerType> query) {
		return query.orderBy(cb.asc(root.get("name")));
	    }
	}, RdbCustomerType.class);
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#
     * getContainedCustomerTypes(java.util.UUID)
     */
    @Override
    public List<? extends ICustomerType> getContainedCustomerTypes(UUID customerTypeId) throws SiteWhereException {
	RdbCustomerType customerType = getEntityManagerProvider().findById(customerTypeId, RdbCustomerType.class);
	if (customerType == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidCustomerTypeToken, ErrorLevel.ERROR);
	}
	return customerType.getContainedCustomerTypes();
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#deleteCustomerType(
     * java.util.UUID)
     */
    @Override
    public ICustomerType deleteCustomerType(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().remove(id, RdbCustomerType.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#createCustomer(com.
     * sitewhere.spi.customer.request.ICustomerCreateRequest)
     */
    @Override
    public RdbCustomer createCustomer(ICustomerCreateRequest request) throws SiteWhereException {
	// Require customer type.
	RdbCustomerType customerType = getCustomerTypeByToken(request.getCustomerTypeToken());
	if (customerType == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidCustomerTypeToken, ErrorLevel.ERROR);
	}

	// Look up parent customer if provided.
	RdbCustomer parentCustomer = null;
	if (request.getParentToken() != null) {
	    parentCustomer = getCustomerByToken(request.getParentToken());
	    if (parentCustomer == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidCustomerToken, ErrorLevel.ERROR);
	    }
	}

	// Use common logic to load assignment from request.
	Customer customer = DeviceManagementPersistence.customerCreateLogic(request, customerType, parentCustomer);
	RdbCustomer created = new RdbCustomer();
	RdbCustomer.copy(customer, created);
	return getEntityManagerProvider().persist(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getCustomer(java.util
     * .UUID)
     */
    @Override
    public ICustomer getCustomer(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().findById(id, RdbCustomer.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getCustomerByToken(
     * java.lang.String)
     */
    @Override
    public RdbCustomer getCustomerByToken(String token) throws SiteWhereException {
	Query query = getEntityManagerProvider().query(Queries.QUERY_CUSTOMER_BY_TOKEN);
	query.setParameter("token", token);
	return getEntityManagerProvider().findOne(query, RdbCustomer.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getCustomerChildren(
     * java.lang.String)
     */
    @Override
    public List<RdbCustomer> getCustomerChildren(String token) throws SiteWhereException {
	RdbCustomer existing = getCustomerByToken(token);
	if (existing == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidCustomerToken, ErrorLevel.ERROR);
	}
	Query query = getEntityManagerProvider().query(Queries.QUERY_CUSTOMER_BY_PARENT_ID);
	query.setParameter("parentId", existing.getId());
	return getEntityManagerProvider().findMany(query, RdbCustomer.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#updateCustomer(java.
     * util.UUID, com.sitewhere.spi.customer.request.ICustomerCreateRequest)
     */
    @Override
    public ICustomer updateCustomer(UUID id, ICustomerCreateRequest request) throws SiteWhereException {
	RdbCustomer existing = getEntityManagerProvider().findById(id, RdbCustomer.class);
	if (existing != null) {
	    // Look up customer type if updated.
	    RdbCustomerType customerType = null;
	    if (request.getCustomerTypeToken() != null) {
		customerType = getCustomerTypeByToken(request.getCustomerTypeToken());
		if (customerType == null) {
		    throw new SiteWhereSystemException(ErrorCode.InvalidCustomerTypeToken, ErrorLevel.ERROR);
		}
	    }

	    // Look up parent customer if provided.
	    RdbCustomer parentCustomer = null;
	    if (request.getParentToken() != null) {
		parentCustomer = getCustomerByToken(request.getParentToken());
		if (parentCustomer == null) {
		    throw new SiteWhereSystemException(ErrorCode.InvalidCustomerToken, ErrorLevel.ERROR);
		}
	    }

	    // Use common update logic.
	    Customer updates = new Customer();
	    DeviceManagementPersistence.customerUpdateLogic(request, customerType, parentCustomer, updates);
	    RdbCustomer.copy(updates, existing);
	    return getEntityManagerProvider().merge(existing);
	}
	throw new SiteWhereSystemException(ErrorCode.InvalidCustomerToken, ErrorLevel.ERROR);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#listCustomers(com.
     * sitewhere.spi.search.customer.ICustomerSearchCriteria)
     */
    @Override
    public ISearchResults<RdbCustomer> listCustomers(ICustomerSearchCriteria criteria) throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbCustomer>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbCustomer> root)
		    throws SiteWhereException {
		if ((criteria.getRootOnly() != null) && (criteria.getRootOnly().booleanValue() == true)) {
		    Path<UUID> path = root.get("parentId");
		    predicates.add(cb.isNull(path));
		} else if (criteria.getParentCustomerToken() != null) {
		    RdbCustomer parentCustomer = getCustomerByToken(criteria.getParentCustomerToken());
		    if (parentCustomer == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidCustomerToken, ErrorLevel.ERROR);
		    }
		    Path<UUID> path = root.get("parentId");
		    predicates.add(cb.equal(path, parentCustomer.getId()));
		}
		if (criteria.getCustomerTypeToken() != null) {
		    RdbCustomerType customerType = getCustomerTypeByToken(criteria.getCustomerTypeToken());
		    if (customerType == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidCustomerTypeToken, ErrorLevel.ERROR);
		    }
		    Path<UUID> path = root.get("customerTypeId");
		    predicates.add(cb.equal(path, customerType.getId()));
		}
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbCustomer> addSort(CriteriaBuilder cb, Root<RdbCustomer> root,
		    CriteriaQuery<RdbCustomer> query) {
		return query.orderBy(cb.asc(root.get("name")));
	    }
	}, RdbCustomer.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getCustomersTree()
     */
    @Override
    public List<? extends ITreeNode> getCustomersTree() throws SiteWhereException {
	ISearchResults<RdbCustomer> all = listCustomers(new CustomerSearchCriteria(1, 0));
	return TreeBuilder.buildTree(all.getResults());
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#deleteCustomer(java.
     * util.UUID)
     */
    @Override
    public RdbCustomer deleteCustomer(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().remove(id, RdbCustomer.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#createAreaType(com.
     * sitewhere.spi.area.request.IAreaTypeCreateRequest)
     */
    @Override
    public RdbAreaType createAreaType(IAreaTypeCreateRequest request) throws SiteWhereException {
	List<RdbAreaType> contained = new ArrayList<>();
	if (request.getContainedAreaTypeTokens() != null) {
	    for (String token : request.getContainedAreaTypeTokens()) {
		contained.add(getAreaTypeByToken(token));
	    }
	}

	// Use common logic to load assignment from request.
	AreaType areaType = DeviceManagementPersistence.areaTypeCreateLogic(request);
	RdbAreaType created = new RdbAreaType();
	RdbAreaType.copy(areaType, created);
	created.setContainedAreaTypes(contained);
	return getEntityManagerProvider().persist(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getAreaType(java.util
     * .UUID)
     */
    @Override
    public IAreaType getAreaType(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().findById(id, RdbAreaType.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getAreaTypeByToken(
     * java.lang.String)
     */
    @Override
    public RdbAreaType getAreaTypeByToken(String token) throws SiteWhereException {
	Query query = getEntityManagerProvider().query(Queries.QUERY_AREA_TYPE_BY_TOKEN);
	query.setParameter("token", token);
	return getEntityManagerProvider().findOne(query, RdbAreaType.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#updateAreaType(java.
     * util.UUID, com.sitewhere.spi.area.request.IAreaTypeCreateRequest)
     */
    @Override
    public RdbAreaType updateAreaType(UUID id, IAreaTypeCreateRequest request) throws SiteWhereException {
	RdbAreaType existing = getEntityManagerProvider().findById(id, RdbAreaType.class);
	if (existing != null) {
	    List<RdbAreaType> contained = null;
	    if (request.getContainedAreaTypeTokens() != null) {
		contained = new ArrayList<>();
		for (String token : request.getContainedAreaTypeTokens()) {
		    contained.add(getAreaTypeByToken(token));
		}
	    }

	    // Use common update logic.
	    AreaType updates = new AreaType();
	    DeviceManagementPersistence.areaTypeUpdateLogic(request, updates);
	    RdbAreaType.copy(updates, existing);
	    if (contained != null) {
		existing.setContainedAreaTypes(contained);
	    }
	    return getEntityManagerProvider().merge(existing);
	}
	throw new SiteWhereSystemException(ErrorCode.InvalidAreaTypeToken, ErrorLevel.ERROR);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#listAreaTypes(com.
     * sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<RdbAreaType> listAreaTypes(ISearchCriteria criteria) throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbAreaType>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbAreaType> root)
		    throws SiteWhereException {
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbAreaType> addSort(CriteriaBuilder cb, Root<RdbAreaType> root,
		    CriteriaQuery<RdbAreaType> query) {
		return query.orderBy(cb.asc(root.get("name")));
	    }
	}, RdbAreaType.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getContainedAreaTypes
     * (java.util.UUID)
     */
    @Override
    public List<? extends IAreaType> getContainedAreaTypes(UUID areaTypeId) throws SiteWhereException {
	RdbAreaType areaType = getEntityManagerProvider().findById(areaTypeId, RdbAreaType.class);
	if (areaType == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAreaTypeToken, ErrorLevel.ERROR);
	}
	return areaType.getContainedAreaTypes();
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#deleteAreaType(java.
     * util.UUID)
     */
    @Override
    public IAreaType deleteAreaType(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().remove(id, RdbAreaType.class);
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#createArea(com.
     * sitewhere.spi.area.request.IAreaCreateRequest)
     */
    @Override
    public RdbArea createArea(IAreaCreateRequest request) throws SiteWhereException {
	// Require area type.
	RdbAreaType areaType = getAreaTypeByToken(request.getAreaTypeToken());
	if (areaType == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAreaTypeToken, ErrorLevel.ERROR);
	}

	// Look up parent area if provided.
	RdbArea parentArea = null;
	if (request.getParentToken() != null) {
	    parentArea = getAreaByToken(request.getParentToken());
	    if (parentArea == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
	    }
	}

	// Use common logic to load assignment from request.
	Area area = DeviceManagementPersistence.areaCreateLogic(request, areaType, parentArea);
	RdbArea created = new RdbArea();
	RdbArea.copy(area, created);
	created = getEntityManagerProvider().persist(created);

	// Parse bounds into entities.
	for (ILocation location : request.getBounds()) {
	    RdbAreaBoundary boundary = new RdbAreaBoundary();
	    RdbLocation.copy(location, boundary);
	    boundary.setAreaId(created.getId());
	    created.getBounds().add(boundary);
	}
	return getEntityManagerProvider().merge(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getArea(java.util.
     * UUID)
     */
    @Override
    public RdbArea getArea(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().findById(id, RdbArea.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getAreaByToken(java.
     * lang.String)
     */
    @Override
    public RdbArea getAreaByToken(String token) throws SiteWhereException {
	Query query = getEntityManagerProvider().query(Queries.QUERY_AREA_BY_TOKEN);
	query.setParameter("token", token);
	return getEntityManagerProvider().findOne(query, RdbArea.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getAreaChildren(java.
     * lang.String)
     */
    @Override
    public List<RdbArea> getAreaChildren(String token) throws SiteWhereException {
	RdbArea existing = getAreaByToken(token);
	if (existing == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
	}
	Query query = getEntityManagerProvider().query(Queries.QUERY_AREA_BY_PARENT_ID);
	query.setParameter("parentId", existing.getId());
	return getEntityManagerProvider().findMany(query, RdbArea.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#updateArea(java.util.
     * UUID, com.sitewhere.spi.area.request.IAreaCreateRequest)
     */
    @Override
    public RdbArea updateArea(UUID id, IAreaCreateRequest request) throws SiteWhereException {
	return getEntityManagerProvider().runInTransaction(new ITransactionCallback<RdbArea>() {

	    /*
	     * @see com.sitewhere.rdb.spi.ITransactionCallback#process()
	     */
	    @Override
	    public RdbArea process() throws SiteWhereException {
		RdbArea existing = getEntityManagerProvider().findById(id, RdbArea.class);
		if (existing != null) {
		    // Look up area type if updated.
		    RdbAreaType areaType = null;
		    if (request.getAreaTypeToken() != null) {
			areaType = getAreaTypeByToken(request.getAreaTypeToken());
			if (areaType == null) {
			    throw new SiteWhereSystemException(ErrorCode.InvalidAreaTypeToken, ErrorLevel.ERROR);
			}
		    }

		    // Look up parent area if provided.
		    RdbArea parentArea = null;
		    if (request.getParentToken() != null) {
			parentArea = getAreaByToken(request.getParentToken());
			if (parentArea == null) {
			    throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
			}
		    }

		    // Use common update logic.
		    Area updates = new Area();
		    DeviceManagementPersistence.areaUpdateLogic(request, areaType, parentArea, updates);
		    RdbArea.copy(updates, existing);

		    // Parse bounds into entities.
		    if (request.getBounds() != null) {
			for (RdbAreaBoundary bound : existing.getBounds()) {
			    getEntityManagerProvider().remove(bound.getId(), RdbAreaBoundary.class);
			}
			existing.getBounds().clear();

			for (ILocation location : request.getBounds()) {
			    RdbAreaBoundary boundary = new RdbAreaBoundary();
			    RdbLocation.copy(location, boundary);
			    boundary.setAreaId(existing.getId());
			    existing.getBounds().add(boundary);
			}
		    }
		    return getEntityManagerProvider().merge(existing);
		}
		throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
	    }
	});
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#listAreas(com.
     * sitewhere.spi.search.area.IAreaSearchCriteria)
     */
    @Override
    public ISearchResults<RdbArea> listAreas(IAreaSearchCriteria criteria) throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbArea>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbArea> root)
		    throws SiteWhereException {
		if ((criteria.getRootOnly() != null) && (criteria.getRootOnly().booleanValue() == true)) {
		    Path<UUID> path = root.get("parentId");
		    predicates.add(cb.isNull(path));
		} else if (criteria.getParentAreaToken() != null) {
		    RdbArea parentArea = getAreaByToken(criteria.getParentAreaToken());
		    if (parentArea == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
		    }
		    Path<UUID> path = root.get("parentId");
		    predicates.add(cb.equal(path, parentArea.getId()));
		}
		if (criteria.getAreaTypeToken() != null) {
		    RdbAreaType areaType = getAreaTypeByToken(criteria.getAreaTypeToken());
		    if (areaType == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidAreaTypeToken, ErrorLevel.ERROR);
		    }
		    Path<UUID> path = root.get("areaTypeId");
		    predicates.add(cb.equal(path, areaType.getId()));
		}
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbArea> addSort(CriteriaBuilder cb, Root<RdbArea> root,
		    CriteriaQuery<RdbArea> query) {
		return query.orderBy(cb.asc(root.get("name")));
	    }
	}, RdbArea.class);
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#getAreasTree()
     */
    @Override
    public List<? extends ITreeNode> getAreasTree() throws SiteWhereException {
	ISearchResults<RdbArea> all = listAreas(new AreaSearchCriteria(1, 0));
	return TreeBuilder.buildTree(all.getResults());
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#deleteArea(java.util.
     * UUID)
     */
    @Override
    public IArea deleteArea(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().remove(id, RdbArea.class);
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#createZone(com.
     * sitewhere.spi.area.request.IZoneCreateRequest)
     */
    @Override
    public RdbZone createZone(IZoneCreateRequest request) throws SiteWhereException {
	IZone existing = getZoneByToken(request.getToken());
	if (existing != null) {
	    throw new SiteWhereException(
		    String.format("Another zone with token '%s' already exists", request.getToken()));
	}

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

	// Use common logic to load zone from request.
	Zone zone = DeviceManagementPersistence.zoneCreateLogic(request, area);
	RdbZone created = new RdbZone();
	RdbZone.copy(zone, created);
	created = getEntityManagerProvider().persist(created);

	// Parse bounds into entities.
	for (ILocation location : request.getBounds()) {
	    RdbZoneBoundary boundary = new RdbZoneBoundary();
	    RdbLocation.copy(location, boundary);
	    boundary.setZoneId(created.getId());
	    created.getBounds().add(boundary);
	}
	return getEntityManagerProvider().merge(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getZone(java.util.
     * UUID)
     */
    @Override
    public IZone getZone(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().findById(id, RdbZone.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getZoneByToken(java.
     * lang.String)
     */
    @Override
    public IZone getZoneByToken(String token) throws SiteWhereException {
	Query query = getEntityManagerProvider().query(Queries.QUERY_ZONE_BY_TOKEN);
	query.setParameter("token", token);
	return getEntityManagerProvider().findOne(query, RdbZone.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#updateZone(java.util.
     * UUID, com.sitewhere.spi.area.request.IZoneCreateRequest)
     */
    @Override
    public RdbZone updateZone(UUID id, IZoneCreateRequest request) throws SiteWhereException {
	return getEntityManagerProvider().runInTransaction(new ITransactionCallback<RdbZone>() {

	    /*
	     * @see com.sitewhere.rdb.spi.ITransactionCallback#process()
	     */
	    @Override
	    public RdbZone process() throws SiteWhereException {
		RdbZone existing = getEntityManagerProvider().findById(id, RdbZone.class);
		if (existing != null) {
		    // Use common update logic.
		    Zone updates = new Zone();
		    DeviceManagementPersistence.zoneUpdateLogic(request, updates);
		    RdbZone.copy(updates, existing);

		    // Parse bounds into entities.
		    if (request.getBounds() != null) {
			for (RdbZoneBoundary bound : existing.getBounds()) {
			    getEntityManagerProvider().remove(bound.getId(), RdbZoneBoundary.class);
			}
			existing.getBounds().clear();

			for (ILocation location : request.getBounds()) {
			    RdbZoneBoundary boundary = new RdbZoneBoundary();
			    RdbLocation.copy(location, boundary);
			    boundary.setZoneId(existing.getId());
			    existing.getBounds().add(boundary);
			}
		    }
		    return getEntityManagerProvider().merge(existing);
		}
		throw new SiteWhereSystemException(ErrorCode.InvalidZoneToken, ErrorLevel.ERROR);
	    }
	});
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#listZones(com.
     * sitewhere.spi.search.device.IZoneSearchCriteria)
     */
    @Override
    public ISearchResults<RdbZone> listZones(IZoneSearchCriteria criteria) throws SiteWhereException {
	RdbArea area = getAreaByToken(criteria.getAreaToken());
	if (area == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
	}

	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbZone>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbZone> root)
		    throws SiteWhereException {
		if (criteria.getAreaToken() != null) {

		    Path<UUID> path = root.get("areaId");
		    predicates.add(cb.equal(path, area.getId()));
		}
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbZone> addSort(CriteriaBuilder cb, Root<RdbZone> root,
		    CriteriaQuery<RdbZone> query) {
		return query.orderBy(cb.asc(root.get("name")));
	    }
	}, RdbZone.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#deleteZone(java.util.
     * UUID)
     */
    @Override
    public RdbZone deleteZone(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().remove(id, RdbZone.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#createDeviceGroup(com
     * .sitewhere.spi.device.request.IDeviceGroupCreateRequest)
     */
    @Override
    public RdbDeviceGroup createDeviceGroup(IDeviceGroupCreateRequest request) throws SiteWhereException {
	// Use common logic.
	DeviceGroup group = DeviceManagementPersistence.deviceGroupCreateLogic(request);
	RdbDeviceGroup created = new RdbDeviceGroup();
	RdbDeviceGroup.copy(group, created);
	return getEntityManagerProvider().persist(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getDeviceGroup(java.
     * util.UUID)
     */
    @Override
    public RdbDeviceGroup getDeviceGroup(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().findById(id, RdbDeviceGroup.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#getDeviceGroupByToken
     * (java.lang.String)
     */
    @Override
    public RdbDeviceGroup getDeviceGroupByToken(String token) throws SiteWhereException {
	Query query = getEntityManagerProvider().query(Queries.QUERY_DEVICE_GROUP_BY_TOKEN);
	query.setParameter("token", token);
	return getEntityManagerProvider().findOne(query, RdbDeviceGroup.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#updateDeviceGroup(
     * java.util.UUID, com.sitewhere.spi.device.request.IDeviceGroupCreateRequest)
     */
    @Override
    public RdbDeviceGroup updateDeviceGroup(UUID id, IDeviceGroupCreateRequest request) throws SiteWhereException {
	RdbDeviceGroup existing = getEntityManagerProvider().findById(id, RdbDeviceGroup.class);
	if (existing != null) {
	    // Use common update logic.
	    DeviceGroup updates = new DeviceGroup();
	    DeviceManagementPersistence.deviceGroupUpdateLogic(request, updates);
	    RdbDeviceGroup.copy(updates, existing);
	    return getEntityManagerProvider().merge(existing);
	}
	throw new SiteWhereSystemException(ErrorCode.InvalidDeviceGroupId, ErrorLevel.ERROR);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#listDeviceGroups(com.
     * sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<RdbDeviceGroup> listDeviceGroups(ISearchCriteria criteria) throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbDeviceGroup>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbDeviceGroup> root)
		    throws SiteWhereException {
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbDeviceGroup> addSort(CriteriaBuilder cb, Root<RdbDeviceGroup> root,
		    CriteriaQuery<RdbDeviceGroup> query) {
		return query.orderBy(cb.asc(root.get("name")));
	    }
	}, RdbDeviceGroup.class);
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#
     * listDeviceGroupsWithRole(java.lang.String,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<RdbDeviceGroup> listDeviceGroupsWithRole(String role, ISearchCriteria criteria)
	    throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbDeviceGroup>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbDeviceGroup> root)
		    throws SiteWhereException {
		Path<List<String>> path = root.get("roles");
		predicates.add(path.in(role));
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbDeviceGroup> addSort(CriteriaBuilder cb, Root<RdbDeviceGroup> root,
		    CriteriaQuery<RdbDeviceGroup> query) {
		return query.orderBy(cb.asc(root.get("name")));
	    }
	}, RdbDeviceGroup.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.device.IDeviceManagement#deleteDeviceGroup(
     * java.util.UUID)
     */
    @Override
    public IDeviceGroup deleteDeviceGroup(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().remove(id, RdbDeviceGroup.class);
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#
     * addDeviceGroupElements(java.util.UUID, java.util.List, boolean)
     */
    @Override
    public List<RdbDeviceGroupElement> addDeviceGroupElements(UUID groupId,
	    List<IDeviceGroupElementCreateRequest> elements, boolean ignoreDuplicates) throws SiteWhereException {
	RdbDeviceGroup group = getDeviceGroup(groupId);
	if (group == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceGroupId, ErrorLevel.ERROR);
	}
	List<RdbDeviceGroupElement> results = new ArrayList<>();
	for (IDeviceGroupElementCreateRequest request : elements) {
	    // Look up referenced device if provided.
	    RdbDevice device = null;
	    if (request.getDeviceToken() != null) {
		device = getDeviceByToken(request.getDeviceToken());
		if (device == null) {
		    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceToken, ErrorLevel.ERROR);
		}
	    }
	    // Look up referenced nested group if provided.
	    RdbDeviceGroup nested = null;
	    if (request.getNestedGroupToken() != null) {
		nested = getDeviceGroupByToken(request.getNestedGroupToken());
		if (nested == null) {
		    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceGroupToken, ErrorLevel.ERROR);
		}
	    }

	    // Create element and associate it with group.
	    RdbDeviceGroupElement created = new RdbDeviceGroupElement();
	    DeviceGroupElement element = DeviceManagementPersistence.deviceGroupElementCreateLogic(request, group,
		    device, nested);
	    RdbDeviceGroupElement.copy(element, created);
	    results.add(getEntityManagerProvider().persist(created));
	}

	group.setElements(results);
	getEntityManagerProvider().persist(group);
	return results;
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#
     * removeDeviceGroupElements(java.util.List)
     */
    @Override
    public List<IDeviceGroupElement> removeDeviceGroupElements(List<UUID> elementIds) throws SiteWhereException {
	List<IDeviceGroupElement> deleted = new ArrayList<IDeviceGroupElement>();
	for (UUID elementId : elementIds) {
	    RdbDeviceGroupElement match = getEntityManagerProvider().findById(elementId, RdbDeviceGroupElement.class);
	    if (match != null) {
		deleted.add(getEntityManagerProvider().remove(elementId, RdbDeviceGroupElement.class));
	    }
	}
	return deleted;
    }

    /*
     * @see com.sitewhere.microservice.api.device.IDeviceManagement#
     * listDeviceGroupElements(java.util.UUID,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<RdbDeviceGroupElement> listDeviceGroupElements(UUID groupId, ISearchCriteria criteria)
	    throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbDeviceGroupElement>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbDeviceGroupElement> root)
		    throws SiteWhereException {
		Path<UUID> path = root.get("groupId");
		predicates.add(cb.equal(path, groupId));
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbDeviceGroupElement> addSort(CriteriaBuilder cb, Root<RdbDeviceGroupElement> root,
		    CriteriaQuery<RdbDeviceGroupElement> query) {
		return query.orderBy(cb.desc(root.get("createdDate")));
	    }
	}, RdbDeviceGroupElement.class);
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
     * Get asset management implementation from microservice.
     *
     * @return
     */
    public IAssetManagement getAssetManagement() {
	return ((DeviceManagementMicroservice) getTenantEngine().getMicroservice()).getAssetManagementApiChannel();
    }

    /**
     * Look up a list of asset tokens to get the corresponding list of asset ids.
     *
     * @param tokens
     * @return
     * @throws SiteWhereException
     */
    protected List<UUID> getAssetIds(List<String> tokens) throws SiteWhereException {
	List<UUID> result = new ArrayList<>();
	for (String token : tokens) {
	    IAsset asset = getAssetManagement().getAssetByToken(token);
	    result.add(asset.getId());
	}
	return result;
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

    /*
     * @see com.sitewhere.rdb.spi.IRdbTenantComponent#getEntityManagerProvider()
     */
    @Override
    public IRdbEntityManagerProvider getEntityManagerProvider() {
	return ((IDeviceManagementTenantEngine) getTenantEngine()).getRdbEntityManagerProvider();
    }
}
