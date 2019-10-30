/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.rdb;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.device.DeviceManagementUtils;
import com.sitewhere.device.microservice.DeviceManagementMicroservice;
import com.sitewhere.device.persistence.DeviceManagementPersistence;
import com.sitewhere.device.persistence.TreeBuilder;
import com.sitewhere.rdb.RDBTenantComponent;
import com.sitewhere.rdb.multitenancy.MultiTenantContext;
import com.sitewhere.rest.model.area.Area;
import com.sitewhere.rest.model.area.AreaType;
import com.sitewhere.rest.model.area.Zone;
import com.sitewhere.rest.model.common.Location;
import com.sitewhere.rest.model.customer.Customer;
import com.sitewhere.rest.model.customer.CustomerType;
import com.sitewhere.rest.model.device.*;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.rest.model.search.area.AreaSearchCriteria;
import com.sitewhere.rest.model.search.customer.CustomerSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
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
import com.sitewhere.spi.device.*;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.*;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.ITreeNode;
import com.sitewhere.spi.search.area.IAreaSearchCriteria;
import com.sitewhere.spi.search.customer.ICustomerSearchCriteria;
import com.sitewhere.spi.search.device.*;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * Device management implementation that uses Relational database for persistence.
 *
 * @author Luciano Baez
 */
public class RDBDeviceManagement extends RDBTenantComponent<DeviceManagementRDBClient> implements IDeviceManagement {

    public RDBDeviceManagement() {
        super(LifecycleComponentType.DataStore);
    }

    /** Injected with global SiteWhere relational database client */
    private DeviceManagementRDBClient dbClient;

    public DeviceManagementRDBClient getDbClient() {
        return dbClient;
    }

    public void setDbClient(DeviceManagementRDBClient dbClient) {
        this.dbClient = dbClient;
    }

    @Override
    public IDeviceType createDeviceType(IDeviceTypeCreateRequest request) throws SiteWhereException {
        DeviceType deviceType = DeviceManagementPersistence.deviceTypeCreateLogic(request);
        com.sitewhere.rdb.entities.DeviceType created = new com.sitewhere.rdb.entities.DeviceType();
        BeanUtils.copyProperties(deviceType, created);
        created = getRDBClient().getDbManager().getDeviceTypeRepository().save(created);
        return created;
    }

    @Override
    public IDeviceType getDeviceType(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceType> opt = getRDBClient().getDbManager().getDeviceTypeRepository().findById(id);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IDeviceType getDeviceTypeByToken(String token) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceType> opt = getRDBClient().getDbManager().getDeviceTypeRepository().findByToken(token);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IDeviceType updateDeviceType(UUID id, IDeviceTypeCreateRequest request) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceType> opt = getRDBClient().getDbManager().getDeviceTypeRepository().findById(id);
        com.sitewhere.rdb.entities.DeviceType updated = null;
        if(opt.isPresent()) {
            updated = opt.get();
            DeviceType deviceType = new DeviceType();
            deviceType.setId(id);
            DeviceManagementPersistence.deviceTypeUpdateLogic(request, deviceType);
            BeanUtils.copyProperties(deviceType, updated);
            updated = getRDBClient().getDbManager().getDeviceTypeRepository().save(updated);
        }
        return updated;
    }

    @Override
    public ISearchResults<IDeviceType> listDeviceTypes(ISearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.DESC,"createdDate");
        Specification<com.sitewhere.rdb.entities.DeviceType> specification = new Specification<com.sitewhere.rdb.entities.DeviceType>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.DeviceType> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return null;
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.DeviceType> result = getRDBClient().getDbManager().getDeviceTypeRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.DeviceType> page = getRDBClient().getDbManager().getDeviceTypeRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }


    @Override
    public IDeviceType deleteDeviceType(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceType> opt = getRDBClient().getDbManager().getDeviceTypeRepository().findById(id);
        if(opt.isPresent()) {
            getRDBClient().getDbManager().getDeviceTypeRepository().deleteById(id);
        }
        return opt.get();
    }

    @Override
    public IDeviceCommand createDeviceCommand(IDeviceCommandCreateRequest request) throws SiteWhereException {
        // Validate device type token passed.
        if (request.getDeviceTypeToken() == null) {
            throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
        }
        Optional<com.sitewhere.rdb.entities.DeviceType> opt = getRDBClient().getDbManager().getDeviceTypeRepository().findByToken(request.getDeviceTypeToken());
        if(!opt.isPresent()) {
            throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
        }
        com.sitewhere.rdb.entities.DeviceType created = opt.get();

        DeviceCommandSearchCriteria criteria = new DeviceCommandSearchCriteria(1, 0);
        criteria.setDeviceTypeToken(created.getToken());
        ISearchResults<IDeviceCommand> existing = listDeviceCommands(criteria);

        DeviceType deviceType = new DeviceType();
        deviceType.setId(created.getId());

        // Use common logic so all backend implementations work the same.
        DeviceCommand command = DeviceManagementPersistence.deviceCommandCreateLogic(deviceType, request,
                existing.getResults());

        com.sitewhere.rdb.entities.DeviceCommand newCommand = new com.sitewhere.rdb.entities.DeviceCommand();
        BeanUtils.copyProperties(command, newCommand);

        newCommand = getRDBClient().getDbManager().getDeviceCommandRepository().save(newCommand);
        return newCommand;
    }

    @Override
    public IDeviceCommand getDeviceCommand(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceCommand> opt = getRDBClient().getDbManager().getDeviceCommandRepository().findById(id);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IDeviceCommand getDeviceCommandByToken(UUID deviceTypeId, String token) throws SiteWhereException {
        return null;
    }

    public IDeviceCommand getDeviceCommandByToken(String token) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceCommand> opt = getRDBClient().getDbManager().getDeviceCommandRepository().findByToken(token);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IDeviceCommand updateDeviceCommand(UUID id, IDeviceCommandCreateRequest request) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceCommand> opt = getRDBClient().getDbManager().getDeviceCommandRepository().findById(id);
        com.sitewhere.rdb.entities.DeviceCommand updated = new com.sitewhere.rdb.entities.DeviceCommand();
        if(opt.isPresent()) {
            // Validate device type token passed.
            IDeviceType deviceType = null;
            if (request.getDeviceTypeToken() != null) {
                deviceType = getDeviceTypeByToken(request.getDeviceTypeToken());
                if (deviceType == null) {
                    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
                }
            } else {
                deviceType = getDeviceType(opt.get().getDeviceTypeId());
            }

            DeviceCommandSearchCriteria criteria = new DeviceCommandSearchCriteria(1, 0);
            criteria.setDeviceTypeToken(deviceType.getToken());
            ISearchResults<IDeviceCommand> existing = listDeviceCommands(criteria);

            DeviceCommand target = new DeviceCommand();
            // Use common update logic.
            DeviceManagementPersistence.deviceCommandUpdateLogic(deviceType, request, target, existing.getResults());
            BeanUtils.copyProperties(target, updated);

            updated = getRDBClient().getDbManager().getDeviceCommandRepository().save(updated);
        }
        return updated;
    }

    @Override
    public ISearchResults<IDeviceCommand> listDeviceCommands(IDeviceCommandSearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.ASC,"name");
        Specification<com.sitewhere.rdb.entities.DeviceCommand> specification = new Specification<com.sitewhere.rdb.entities.DeviceCommand>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.DeviceCommand> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList();
                if (criteria.getDeviceTypeToken() != null) {
                    Path path = root.get("deviceTypeToken");
                    predicates.add(cb.equal(path, criteria.getDeviceTypeToken()));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.DeviceCommand> result = getRDBClient().getDbManager().getDeviceCommandRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.DeviceCommand> page = getRDBClient().getDbManager().getDeviceCommandRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public IDeviceCommand deleteDeviceCommand(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceCommand> opt = getRDBClient().getDbManager().getDeviceCommandRepository().findById(id);
        if(opt.isPresent()) {
            getRDBClient().getDbManager().getDeviceCommandRepository().deleteById(id);
        }
        return opt.get();
    }

    @Override
    public IDeviceStatus createDeviceStatus(IDeviceStatusCreateRequest request) throws SiteWhereException {
        if (request.getDeviceTypeToken() == null) {
            throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
        }
        IDeviceType deviceType = getDeviceTypeByToken(request.getDeviceTypeToken());
        if (deviceType == null) {
            throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
        }
        DeviceStatusSearchCriteria criteria = new DeviceStatusSearchCriteria(1, 0);
        criteria.setDeviceTypeToken(deviceType.getToken());
        ISearchResults<IDeviceStatus> existing = listDeviceStatuses(criteria);

        // Use common logic so all backend implementations work the same.
        DeviceStatus status = DeviceManagementPersistence.deviceStatusCreateLogic(deviceType, request,
                existing.getResults());

        com.sitewhere.rdb.entities.DeviceStatus created = new com.sitewhere.rdb.entities.DeviceStatus();
        BeanUtils.copyProperties(status, created);
        created = getRDBClient().getDbManager().getDeviceStatusRepository().save(created);
        return created;
    }

    @Override
    public IDeviceStatus getDeviceStatus(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceStatus> opt = getRDBClient().getDbManager().getDeviceStatusRepository().findById(id);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IDeviceStatus getDeviceStatusByToken(UUID deviceTypeId, String token) throws SiteWhereException {
        return null;
    }

    //@Override
    public IDeviceStatus getDeviceStatusByToken(String token) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceStatus> opt = getRDBClient().getDbManager().getDeviceStatusRepository().findByToken(token);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IDeviceStatus updateDeviceStatus(UUID id, IDeviceStatusCreateRequest request) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceStatus> opt = getRDBClient().getDbManager().getDeviceStatusRepository().findById(id);
        if(opt.isPresent()) {
            com.sitewhere.rdb.entities.DeviceStatus updated = opt.get();

            // Validate device type token passed.
            IDeviceType deviceType = null;
            if (request.getDeviceTypeToken() != null) {
                deviceType = getDeviceTypeByToken(request.getDeviceTypeToken());
                if (deviceType == null) {
                    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
                }
            } else {
                deviceType = getDeviceType(updated.getDeviceTypeId());
            }
            DeviceStatusSearchCriteria criteria = new DeviceStatusSearchCriteria(1, 0);
            criteria.setDeviceTypeToken(deviceType.getToken());
            ISearchResults<IDeviceStatus> existing = listDeviceStatuses(criteria);
            DeviceStatus target = new DeviceStatus();
            // Use common update logic.
            DeviceManagementPersistence.deviceStatusUpdateLogic(deviceType, request, target, existing.getResults());
            BeanUtils.copyProperties(target, updated);
            updated = getRDBClient().getDbManager().getDeviceStatusRepository().save(updated);
            return updated;
        }
        return null;
    }

    @Override
    public ISearchResults<IDeviceStatus> listDeviceStatuses(IDeviceStatusSearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.ASC,"name");
        Specification<com.sitewhere.rdb.entities.DeviceStatus> specification = new Specification<com.sitewhere.rdb.entities.DeviceStatus>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.DeviceStatus> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList();
                if (criteria.getDeviceTypeToken() != null) {
                    Path path = root.get("deviceTypeToken");
                    predicates.add(cb.equal(path, criteria.getDeviceTypeToken()));
                }
                if (criteria.getCode() != null) {
                    Path path = root.get("code");
                    predicates.add(cb.equal(path, criteria.getCode()));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.DeviceStatus> result = getRDBClient().getDbManager().getDeviceStatusRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.DeviceStatus> page = getRDBClient().getDbManager().getDeviceStatusRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public IDeviceStatus deleteDeviceStatus(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceStatus> opt = getRDBClient().getDbManager().getDeviceStatusRepository().findById(id);
        if(opt.isPresent()) {
            getRDBClient().getDbManager().getDeviceStatusRepository().deleteById(id);
        }
        return opt.get();
    }

    @Override
    public IDevice createDevice(IDeviceCreateRequest request) throws SiteWhereException {
        IDeviceType deviceType = getDeviceTypeByToken(request.getDeviceTypeToken());
        if (deviceType == null) {
            throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
        }
        Device newDevice = DeviceManagementPersistence.deviceCreateLogic(request, deviceType);
        com.sitewhere.rdb.entities.Device created = new com.sitewhere.rdb.entities.Device();
        BeanUtils.copyProperties(newDevice, created);
        created = getRDBClient().getDbManager().getDeviceRepository().save(created);
        return created;
    }

    @Override
    public IDevice getDevice(UUID deviceId) throws SiteWhereException {
        Optional< com.sitewhere.rdb.entities.Device> opt = getRDBClient().getDbManager().getDeviceRepository().findById(deviceId);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IDevice getDeviceByToken(String token) throws SiteWhereException {
        Optional< com.sitewhere.rdb.entities.Device> opt = getRDBClient().getDbManager().getDeviceRepository().findByToken(token);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IDevice updateDevice(UUID deviceId, IDeviceCreateRequest request) throws SiteWhereException {
        getLogger().info("Request:\n\n" + MarshalUtils.marshalJsonAsPrettyString(request));
        Optional<com.sitewhere.rdb.entities.Device> opt = getRDBClient().getDbManager().getDeviceRepository().findById(deviceId);
        com.sitewhere.rdb.entities.Device updated = new com.sitewhere.rdb.entities.Device();
        if(opt.isPresent()) {
            updated = opt.get();

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

            Device target = new Device();
            DeviceManagementPersistence.deviceUpdateLogic(request, deviceType, parent, target);
            getLogger().info("Updated:\n\n" + MarshalUtils.marshalJsonAsPrettyString(target));

            BeanUtils.copyProperties(target, updated);
            updated = getRDBClient().getDbManager().getDeviceRepository().save(updated);
            return updated;
        }
        return null;
    }

    @Override
    public ISearchResults<IDevice> listDevices(IDeviceSearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.ASC,"createdDate");
        IDeviceType deviceType = getDeviceTypeByToken(criteria.getDeviceTypeToken());
        Specification<com.sitewhere.rdb.entities.Device> specification = new Specification<com.sitewhere.rdb.entities.Device>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.Device> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList();
                if (criteria.isExcludeAssigned()) {
                    Path path = root.get("activeDeviceAssignmentIds");
                    predicates.add(cb.not(cb.size(path).isNull()));
                    predicates.add(cb.not(cb.size(path).in(0)));
                }
                if (criteria.getStartDate() != null) {
                    Path path = root.get("createdDate");
                    predicates.add(cb.greaterThanOrEqualTo(path, criteria.getStartDate()));
                }
                if (criteria.getEndDate() != null) {
                    Path path = root.get("createdDate");
                    predicates.add(cb.lessThanOrEqualTo(path, criteria.getEndDate()));
                }
                if (!StringUtils.isEmpty(criteria.getDeviceTypeToken())) {
                    Path path = root.get("deviceTypeToken");
                    predicates.add(cb.equal(path, deviceType.getId()));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.Device> result = getRDBClient().getDbManager().getDeviceRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.Device> page = getRDBClient().getDbManager().getDeviceRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public IDevice createDeviceElementMapping(UUID deviceId, IDeviceElementMapping mapping) throws SiteWhereException {
        IDevice device = getApiDeviceById(deviceId);
        return DeviceManagementPersistence.deviceElementMappingCreateLogic(this, device, mapping);
    }

    @Override
    public IDevice deleteDeviceElementMapping(UUID deviceId, String path) throws SiteWhereException {
        IDevice device = getApiDeviceById(deviceId);
        return DeviceManagementPersistence.deviceElementMappingDeleteLogic(this, device, path);
    }

    @Override
    public IDevice deleteDevice(UUID deviceId) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.Device> opt = getRDBClient().getDbManager().getDeviceRepository().findById(deviceId);
        if(opt.isPresent()) {
            getRDBClient().getDbManager().getDeviceRepository().deleteById(deviceId);
        }
        return opt.get();
    }

    @Override
    public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request) throws SiteWhereException {
        IDevice existing = getDeviceByToken(request.getDeviceToken());
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

        com.sitewhere.rdb.entities.DeviceAssignment created = new com.sitewhere.rdb.entities.DeviceAssignment();
        BeanUtils.copyProperties(newAssignment, created);
        created = getRDBClient().getDbManager().getDeviceAssignmentRepository().save(created);
        return created;
    }

    @Override
    public IDeviceAssignment getDeviceAssignment(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceAssignment> opt = getRDBClient().getDbManager().getDeviceAssignmentRepository().findById(id);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceAssignment> opt = getRDBClient().getDbManager().getDeviceAssignmentRepository().findByToken(token);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public List<IDeviceAssignment> getActiveDeviceAssignments(UUID deviceId) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.Device> opt = getRDBClient().getDbManager().getDeviceRepository().findById(deviceId);
        if(opt.isPresent()) {
            com.sitewhere.rdb.entities.Device device = opt.get();
            List<IDeviceAssignment> active = new ArrayList();
            List<UUID> uuids = device.getActiveDeviceAssignmentIds();
            for(UUID uuid : uuids) {
                Optional<com.sitewhere.rdb.entities.DeviceAssignment> opt2 = getRDBClient().getDbManager().getDeviceAssignmentRepository().findById(uuid);
                if(opt2.isPresent()) {
                    active.add(opt2.get());
                }
            }
            return active;
        }
        return null;
    }

    @Override
    public IDeviceAssignment updateDeviceAssignment(UUID id, IDeviceAssignmentCreateRequest request) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceAssignment> opt = getRDBClient().getDbManager().getDeviceAssignmentRepository().findById(id);
        if(opt.isPresent()) {
            com.sitewhere.rdb.entities.DeviceAssignment updated = opt.get();

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

            DeviceAssignment target = new DeviceAssignment();
            target.setId(updated.getId());
            DeviceManagementPersistence.deviceAssignmentUpdateLogic(device, customer, area, asset, request, target);
            BeanUtils.copyProperties(target, updated);
            updated = getRDBClient().getDbManager().getDeviceAssignmentRepository().save(updated);
            return updated;
        }
        return null;
    }

    @Override
    public ISearchResults<IDeviceAssignment> listDeviceAssignments(IDeviceAssignmentSearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.DESC,"activeDate");
        Specification<com.sitewhere.rdb.entities.DeviceAssignment> specification = new Specification<com.sitewhere.rdb.entities.DeviceAssignment>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.DeviceAssignment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList();

                if ((criteria.getAssignmentStatuses() != null) && (criteria.getAssignmentStatuses().size() > 0)) {
                    Path path = root.get("status");
                    predicates.add(path.in(criteria.getAssignmentStatuses()));
                }
                if ((criteria.getDeviceTokens() != null) && (criteria.getDeviceTokens().size() > 0)) {
                    try {
                        List<UUID> ids = DeviceManagementUtils.getDeviceIds(criteria.getDeviceTokens(), RDBDeviceManagement.this);
                        Path path = root.get("deviceId");
                        predicates.add(path.in(ids));
                    } catch (SiteWhereException e) {
                        e.printStackTrace();
                    }
                }
                if ((criteria.getCustomerTokens() != null) && (criteria.getCustomerTokens().size() > 0)) {
                    try {
                        List<UUID> ids = DeviceManagementUtils.getCustomerIds(criteria.getCustomerTokens(), RDBDeviceManagement.this);
                        Path path = root.get("customerId");
                        predicates.add(path.in(ids));
                    } catch (SiteWhereException e) {
                        e.printStackTrace();
                    }
                }
                if ((criteria.getAreaTokens() != null) && (criteria.getAreaTokens().size() > 0)) {
                    try {
                        List<UUID> ids = DeviceManagementUtils.getAreaIds(criteria.getAreaTokens(), RDBDeviceManagement.this);
                        Path path = root.get("areaId");
                        predicates.add(path.in(ids));
                    } catch (SiteWhereException e) {
                        e.printStackTrace();
                    }
                }
                if ((criteria.getAssetTokens() != null) && (criteria.getAssetTokens().size() > 0)) {
                    try {
                        List<UUID> ids = getAssetIds(criteria.getAssetTokens());
                        Path path = root.get("assetId");
                        predicates.add(path.in(ids));
                    } catch (SiteWhereException e) {
                        e.printStackTrace();
                    }
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.DeviceAssignment> result = getRDBClient().getDbManager().getDeviceAssignmentRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.DeviceAssignment> page = getRDBClient().getDbManager().getDeviceAssignmentRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public IDeviceAssignment endDeviceAssignment(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceAssignment> opt = getRDBClient().getDbManager().getDeviceAssignmentRepository().findById(id);
        com.sitewhere.rdb.entities.DeviceAssignment updated = null;
        if(opt.isPresent()) {
            updated = opt.get();
            updated.setReleasedDate(Calendar.getInstance().getTime());
            updated.setStatus(DeviceAssignmentStatus.Released);

            UUID deviceId = updated.getDeviceId();
            Optional<com.sitewhere.rdb.entities.Device> opt2 = getRDBClient().getDbManager().getDeviceRepository().findById(deviceId);
            if (opt2.isPresent()) {
                com.sitewhere.rdb.entities.Device device = opt2.get();
                DeviceAssignmentSearchCriteria criteria = new DeviceAssignmentSearchCriteria(1, 0);
                criteria.setDeviceTokens(Collections.singletonList(updated.getToken()));
                criteria.setAssignmentStatuses(Collections.singletonList(DeviceAssignmentStatus.Active));
                ISearchResults<IDeviceAssignment> matches = listDeviceAssignments(criteria);

                List<UUID> uuids = new ArrayList();
                for (IDeviceAssignment assignment : matches.getResults()) {
                    uuids.add(assignment.getId());
                }

                device.setActiveDeviceAssignmentIds(uuids);
                getRDBClient().getDbManager().getDeviceRepository().save(device);
            }
            return updated;
        }
        return null;
    }

    @Override
    public IDeviceAssignment deleteDeviceAssignment(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceAssignment> opt = getRDBClient().getDbManager().getDeviceAssignmentRepository().findById(id);
        if(opt.isPresent()) {
            getRDBClient().getDbManager().getDeviceAssignmentRepository().deleteById(id);
        }
        return opt.get();
    }

    @Override
    public IDeviceAlarm createDeviceAlarm(IDeviceAlarmCreateRequest request) throws SiteWhereException {
        IDeviceAssignment assignment = getDeviceAssignmentByToken(request.getDeviceAssignmentToken());
        if (assignment == null) {
            throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
        }

        // Use common logic to load alarm from request.
        DeviceAlarm newAlarm = DeviceManagementPersistence.deviceAlarmCreateLogic(assignment, request);
        com.sitewhere.rdb.entities.DeviceAlarm created = new com.sitewhere.rdb.entities.DeviceAlarm();
        BeanUtils.copyProperties(newAlarm, created);
        created = getRDBClient().getDbManager().getDeviceAlarmRepository().save(created);
        return created;
    }

    @Override
    public IDeviceAlarm updateDeviceAlarm(UUID id, IDeviceAlarmCreateRequest request) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceAlarm> opt = getRDBClient().getDbManager().getDeviceAlarmRepository().findById(id);
        if(opt.isPresent()) {
            com.sitewhere.rdb.entities.DeviceAlarm updated = opt.get();

            IDeviceAssignment assignment = null;
            if (request.getDeviceAssignmentToken() != null) {
                assignment = getDeviceAssignmentByToken(request.getDeviceAssignmentToken());
                if (assignment == null) {
                    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
                }
            }
            DeviceAlarm target = new DeviceAlarm();
            target.setId(updated.getId());
            DeviceManagementPersistence.deviceAlarmUpdateLogic(assignment, request, target);

            BeanUtils.copyProperties(target, updated);
            updated = getRDBClient().getDbManager().getDeviceAlarmRepository().save(updated);
            return updated;
        }
        return null;
    }

    @Override
    public IDeviceAlarm getDeviceAlarm(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceAlarm> opt = getRDBClient().getDbManager().getDeviceAlarmRepository().findById(id);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public ISearchResults<IDeviceAlarm> searchDeviceAlarms(IDeviceAlarmSearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.DESC,"triggeredDate");
        Specification<com.sitewhere.rdb.entities.DeviceAlarm> specification = new Specification<com.sitewhere.rdb.entities.DeviceAlarm>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.DeviceAlarm> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList();
                if (criteria.getDeviceId() != null) {
                    Path path = root.get("deviceId");
                    predicates.add(cb.equal(path, criteria.getDeviceId()));
                }
                if (criteria.getDeviceAssignmentId() != null) {
                    Path path = root.get("deviceAssignmentId");
                    predicates.add(cb.equal(path, criteria.getDeviceAssignmentId()));
                }
                if (criteria.getCustomerId() != null) {
                    Path path = root.get("customerId");
                    predicates.add(cb.equal(path, criteria.getCustomerId()));
                }
                if (criteria.getAreaId() != null) {
                    Path path = root.get("areaId");
                    predicates.add(cb.equal(path, criteria.getAreaId()));
                }
                if (criteria.getAssetId() != null) {
                    Path path = root.get("assetId");
                    predicates.add(cb.equal(path, criteria.getAssetId()));
                }
                if (criteria.getState() != null) {
                    Path path = root.get("state");
                    predicates.add(cb.equal(path, criteria.getState()));
                }
                if (criteria.getTriggeringEventId() != null) {
                    Path path = root.get("triggeringEventId");
                    predicates.add(cb.equal(path, criteria.getTriggeringEventId()));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.DeviceAlarm> result = getRDBClient().getDbManager().getDeviceAlarmRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.DeviceAlarm> page = getRDBClient().getDbManager().getDeviceAlarmRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public IDeviceAlarm deleteDeviceAlarm(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceAlarm> opt = getRDBClient().getDbManager().getDeviceAlarmRepository().findById(id);
        if(opt.isPresent()) {
            getRDBClient().getDbManager().getDeviceAlarmRepository().deleteById(id);
        }
        return opt.get();
    }

    @Override
    public ICustomerType createCustomerType(ICustomerTypeCreateRequest request) throws SiteWhereException {
        // Convert contained customer type tokens to ids.
        List<UUID> cctids = convertCustomerTypeTokensToIds(request.getContainedCustomerTypeTokens());

        // Use common logic so all backend implementations work the same.
        CustomerType type = DeviceManagementPersistence.customerTypeCreateLogic(request, cctids);
        com.sitewhere.rdb.entities.CustomerType created = new com.sitewhere.rdb.entities.CustomerType();

        BeanUtils.copyProperties(type, created);
        created = getRDBClient().getDbManager().getCustomerTypeRepository().save(created);
        return created;
    }

    @Override
    public ICustomerType getCustomerType(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.CustomerType> opt = getRDBClient().getDbManager().getCustomerTypeRepository().findById(id);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public ICustomerType getCustomerTypeByToken(String token) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.CustomerType> opt = getRDBClient().getDbManager().getCustomerTypeRepository().findByToken(token);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public ICustomerType updateCustomerType(UUID id, ICustomerTypeCreateRequest request) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.CustomerType> opt = getRDBClient().getDbManager().getCustomerTypeRepository().findById(id);
        com.sitewhere.rdb.entities.CustomerType updated = null;
        if(opt.isPresent()) {
            updated = opt.get();
            // Convert contained customer type tokens to ids.
            List<UUID> cctids = convertCustomerTypeTokensToIds(request.getContainedCustomerTypeTokens());

            CustomerType target = new CustomerType();
            target.setId(id);
            // Use common update logic.
            DeviceManagementPersistence.customerTypeUpdateLogic(request, cctids, target);

            BeanUtils.copyProperties(target, updated);
            updated = getRDBClient().getDbManager().getCustomerTypeRepository().save(updated);
            return updated;
        }
        return null;
    }

    @Override
    public ISearchResults<ICustomerType> listCustomerTypes(ISearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.ASC,"name");
        Specification<com.sitewhere.rdb.entities.CustomerType> specification = new Specification<com.sitewhere.rdb.entities.CustomerType>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.CustomerType> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
               return null;
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.CustomerType> result = getRDBClient().getDbManager().getCustomerTypeRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.CustomerType> page = getRDBClient().getDbManager().getCustomerTypeRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public ICustomerType deleteCustomerType(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.CustomerType> opt = getRDBClient().getDbManager().getCustomerTypeRepository().findById(id);
        if(opt.isPresent()) {
            getRDBClient().getDbManager().getCustomerTypeRepository().deleteById(id);
        }
        return opt.get();
    }

    @Override
    public ICustomer createCustomer(ICustomerCreateRequest request) throws SiteWhereException {
        // Look up customer type.
        ICustomerType customerType = getCustomerTypeByToken(request.getCustomerTypeToken());
        if (customerType == null) {
            throw new SiteWhereSystemException(ErrorCode.InvalidCustomerTypeToken, ErrorLevel.ERROR);
        }

        // Look up parent customer.
        ICustomer parentCustomer = (request.getParentToken() != null) ? getCustomerByToken(request.getParentToken())
                : null;

        // Use common logic so all backend implementations work the same.
        Customer customer = DeviceManagementPersistence.customerCreateLogic(request, customerType, parentCustomer);
        com.sitewhere.rdb.entities.Customer created = new com.sitewhere.rdb.entities.Customer();
        BeanUtils.copyProperties(customer, created);
        created = getRDBClient().getDbManager().getCustomerRepository().save(created);
        return created;
    }

    @Override
    public ICustomer getCustomer(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.Customer> opt = getRDBClient().getDbManager().getCustomerRepository().findById(id);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public ICustomer getCustomerByToken(String token) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.Customer> opt = getRDBClient().getDbManager().getCustomerRepository().findByToken(token);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public List<ICustomer> getCustomerChildren(String token) throws SiteWhereException {
        ICustomer existing = getCustomerByToken(token);
        if (existing == null) {
            throw new SiteWhereSystemException(ErrorCode.InvalidCustomerToken, ErrorLevel.ERROR);
        }
        Sort sort = new Sort(Sort.Direction.ASC,"name");
        Specification<com.sitewhere.rdb.entities.Customer> specification = new Specification<com.sitewhere.rdb.entities.Customer>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList();
                Path path = root.get("parentId");
                predicates.add(cb.equal(path, existing.getId()));
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        List<com.sitewhere.rdb.entities.Customer> result = getRDBClient().getDbManager().getCustomerRepository().findAll(specification, sort);
        List<ICustomer> list = new ArrayList();
        for(com.sitewhere.rdb.entities.Customer customer : result) {
            list.add(customer);
        }
        return list;
    }

    @Override
    public ICustomer updateCustomer(UUID id, ICustomerCreateRequest request) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.Customer> opt = getRDBClient().getDbManager().getCustomerRepository().findById(id);
        if(opt.isPresent()) {
            com.sitewhere.rdb.entities.Customer updated = opt.get();
            Customer target = new Customer();
            target.setId(updated.getId());
            // Use common update logic.
            DeviceManagementPersistence.customerUpdateLogic(request, target);
            BeanUtils.copyProperties(target, updated);
            updated = getRDBClient().getDbManager().getCustomerRepository().save(updated);
            return updated;
        }
        return null;
    }

    @Override
    public ISearchResults<ICustomer> listCustomers(ICustomerSearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.ASC,"name");
        Specification<com.sitewhere.rdb.entities.Customer> specification = new Specification<com.sitewhere.rdb.entities.Customer>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList();
                if ((criteria.getRootOnly() != null) && (criteria.getRootOnly().booleanValue() == true)) {
                    Path path = root.get("parentId");
                    predicates.add(cb.isNull(path));
                } else if (criteria.getParentCustomerToken() != null) {
                    Path path = root.get("parentId");
                    predicates.add(cb.equal(path, criteria.getParentCustomerToken()));
                }
                if (criteria.getParentCustomerToken() != null) {
                    Path path = root.get("customerTypeId");
                    predicates.add(cb.equal(path, criteria.getParentCustomerToken()));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.Customer> result = getRDBClient().getDbManager().getCustomerRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.Customer> page = getRDBClient().getDbManager().getCustomerRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public List<? extends ITreeNode> getCustomersTree() throws SiteWhereException {
        ISearchResults<ICustomer> all = listCustomers(new CustomerSearchCriteria(1, 0));
        return TreeBuilder.buildTree(all.getResults());
    }

    @Override
    public ICustomer deleteCustomer(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.Customer> opt = getRDBClient().getDbManager().getCustomerRepository().findById(id);
        if(opt.isPresent()) {
            getRDBClient().getDbManager().getCustomerRepository().deleteById(id);
        }
        return opt.get();
    }

    @Override
    public IAreaType createAreaType(IAreaTypeCreateRequest request) throws SiteWhereException {
        // Convert contained area type tokens to ids.
        List<UUID> catids = convertAreaTypeTokensToIds(request.getContainedAreaTypeTokens());

        // Use common logic so all backend implementations work the same.
        AreaType type = DeviceManagementPersistence.areaTypeCreateLogic(request, catids);
        com.sitewhere.rdb.entities.AreaType created = new com.sitewhere.rdb.entities.AreaType();
        BeanUtils.copyProperties(type, created);
        created = getRDBClient().getDbManager().getAreaTypeRepository().save(created);
        return created;
    }

    @Override
    public IAreaType getAreaType(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.AreaType> opt = getRDBClient().getDbManager().getAreaTypeRepository().findById(id);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IAreaType getAreaTypeByToken(String token) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.AreaType> opt = getRDBClient().getDbManager().getAreaTypeRepository().findByToken(token);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IAreaType updateAreaType(UUID id, IAreaTypeCreateRequest request) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.AreaType> opt = getRDBClient().getDbManager().getAreaTypeRepository().findById(id);
        if(opt.isPresent()) {
            com.sitewhere.rdb.entities.AreaType updated = opt.get();

            // Convert contained area type tokens to ids.
            List<UUID> catids = convertAreaTypeTokensToIds(request.getContainedAreaTypeTokens());

            AreaType target = new AreaType();
            target.setId(updated.getId());
            // Use common update logic.
            DeviceManagementPersistence.areaTypeUpdateLogic(request, catids, target);

            BeanUtils.copyProperties(target, updated);
            updated = getRDBClient().getDbManager().getAreaTypeRepository().save(updated);
            return updated;
        }
        return null;
    }

    @Override
    public ISearchResults<IAreaType> listAreaTypes(ISearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.ASC,"name");
        Specification<com.sitewhere.rdb.entities.AreaType> specification = new Specification<com.sitewhere.rdb.entities.AreaType>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.AreaType> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return null;
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.AreaType> result = getRDBClient().getDbManager().getAreaTypeRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.AreaType> page = getRDBClient().getDbManager().getAreaTypeRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public IAreaType deleteAreaType(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.AreaType> opt = getRDBClient().getDbManager().getAreaTypeRepository().findById(id);
        if(!opt.isPresent()) {
            throw new SiteWhereSystemException(ErrorCode.InvalidAreaTypeToken, ErrorLevel.ERROR);
        }
        com.sitewhere.rdb.entities.AreaType deleted = opt.get();
        getRDBClient().getDbManager().getAreaTypeRepository().delete(deleted);
        return deleted;
    }

    @Override
    public IArea createArea(IAreaCreateRequest request) throws SiteWhereException {
        // Look up area type.
        IAreaType areaType = getAreaTypeByToken(request.getAreaTypeToken());
        if (areaType == null) {
            throw new SiteWhereSystemException(ErrorCode.InvalidAreaTypeToken, ErrorLevel.ERROR);
        }

        // Look up parent area.
        IArea parentArea = (request.getParentToken() != null) ? getAreaByToken(request.getParentToken()) : null;

        // Use common logic so all backend implementations work the same.
        Area area = DeviceManagementPersistence.areaCreateLogic(request, areaType, parentArea);
        com.sitewhere.rdb.entities.Area created = new com.sitewhere.rdb.entities.Area();

        List<com.sitewhere.rdb.entities.Location> locations = new ArrayList();

        BeanUtils.copyProperties(area, created);
        //BeanUtils.copyProperties(area.getBounds(), locations);

        for (Location locationCommon: area.getBounds()) {
            com.sitewhere.rdb.entities.Location l = new com.sitewhere.rdb.entities.Location();
            BeanUtils.copyProperties(locationCommon, l);
            locations.add(l);
        }
        created.setBounds(locations);
        created = getRDBClient().getDbManager().getAreaRepository().save(created);
        return created;
    }

    @Override
    public IArea getArea(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.Area> opt = getRDBClient().getDbManager().getAreaRepository().findById(id);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IArea getAreaByToken(String token) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.Area> opt = getRDBClient().getDbManager().getAreaRepository().findByToken(token);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public List<IArea> getAreaChildren(String token) throws SiteWhereException {
        IArea existing = getAreaByToken(token);
        if (existing == null) {
            throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
        }
        Sort sort = new Sort(Sort.Direction.ASC,"name");
        Specification<com.sitewhere.rdb.entities.Area> specification = new Specification<com.sitewhere.rdb.entities.Area>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.Area> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList();
                Path path = root.get("parentId");
                predicates.add(cb.equal(path, existing.getId()));
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        List<com.sitewhere.rdb.entities.Area> list = getRDBClient().getDbManager().getAreaRepository().findAll(specification, sort);
        List<IArea> newList = new ArrayList();
        for(com.sitewhere.rdb.entities.Area a : list) {
            newList.add(a);
        }
        return newList;
    }

    @Override
    public IArea updateArea(UUID id, IAreaCreateRequest request) throws SiteWhereException {
        com.sitewhere.rdb.entities.Area area = null;
        Optional<com.sitewhere.rdb.entities.Area> opt = getRDBClient().getDbManager().getAreaRepository().findById(id);
        if(opt.isPresent()) {
            area = opt.get();

            Area target = new Area();
            // Use common update logic.
            DeviceManagementPersistence.areaUpdateLogic(request, target);
            target.setId(area.getId());
            BeanUtils.copyProperties(target, area);

            area = getRDBClient().getDbManager().getAreaRepository().save(area);
        }
        return area;
    }

    @Override
    public ISearchResults<IArea> listAreas(IAreaSearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.ASC,"name");
        Specification<com.sitewhere.rdb.entities.Area> specification = new Specification<com.sitewhere.rdb.entities.Area>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.Area> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList();
                if ((criteria.getRootOnly() != null) && (criteria.getRootOnly().booleanValue() == true)) {
                    Path path = root.get("parentId");
                    predicates.add(cb.isNull(path));
                } else if (criteria.getParentAreaToken() != null) {
                    try {
                        Optional<com.sitewhere.rdb.entities.Area> opt = getRDBClient().getDbManager().getAreaRepository().findByToken(criteria.getParentAreaToken());
                        com.sitewhere.rdb.entities.Area parent = opt.get();
                        Path path = root.get("parentId");
                        predicates.add(cb.equal(path, parent.getId()));
                    } catch (SiteWhereException e) {
                        e.printStackTrace();
                    }
                }
                if (criteria.getAreaTypeToken() != null) {
                    try {
                        Optional<com.sitewhere.rdb.entities.AreaType> opt = getRDBClient().getDbManager().getAreaTypeRepository().findByToken(criteria.getAreaTypeToken());
                        com.sitewhere.rdb.entities.AreaType type = opt.get();
                        Path path = root.get("areaTypeId");
                        predicates.add(cb.equal(path, type.getId()));
                    } catch (SiteWhereException e) {
                        e.printStackTrace();
                    }
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.Area> result = getRDBClient().getDbManager().getAreaRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.Area> page = getRDBClient().getDbManager().getAreaRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public List<? extends ITreeNode> getAreasTree() throws SiteWhereException {
        ISearchResults<IArea> all = listAreas(new AreaSearchCriteria(1, 0));
        return TreeBuilder.buildTree(all.getResults());
    }

    @Override
    public IArea deleteArea(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.Area> opt = getRDBClient().getDbManager().getAreaRepository().findById(id);
        if(opt.isPresent()) {
            getRDBClient().getDbManager().getAreaRepository().deleteById(id);
        }
        return opt.get();
    }

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
        com.sitewhere.rdb.entities.Zone created = new com.sitewhere.rdb.entities.Zone();
        BeanUtils.copyProperties(zone, created);

        List<com.sitewhere.rdb.entities.Location> locations = new ArrayList();
        BeanUtils.copyProperties(area, created);
        for (Location locationCommon: zone.getBounds()) {
            com.sitewhere.rdb.entities.Location l = new com.sitewhere.rdb.entities.Location();
            BeanUtils.copyProperties(locationCommon, l);
            locations.add(l);
        }
        created.setBounds(locations);
        created = getRDBClient().getDbManager().getZoneRepository().save(created);
        return created;
    }

    @Override
    public IZone getZone(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.Zone> opt = getRDBClient().getDbManager().getZoneRepository().findById(id);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IZone getZoneByToken(String zoneToken) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.Zone> opt = getRDBClient().getDbManager().getZoneRepository().findByToken(zoneToken);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IZone updateZone(UUID id, IZoneCreateRequest request) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.Zone> opt = getRDBClient().getDbManager().getZoneRepository().findById(id);
        if(opt.isPresent()) {
            com.sitewhere.rdb.entities.Zone updated = opt.get();
            Zone target = new Zone();
            target.setId(updated.getId());
            DeviceManagementPersistence.zoneUpdateLogic(request, target);
            BeanUtils.copyProperties(target, updated);
            updated = getRDBClient().getDbManager().getZoneRepository().save(updated);
            return updated;
        }
        return null;
    }

    @Override
    public ISearchResults<IZone> listZones(IZoneSearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.DESC,"createdDate");
        Specification<com.sitewhere.rdb.entities.Zone> specification = new Specification<com.sitewhere.rdb.entities.Zone>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.Zone> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList();
                if (criteria.getAreaToken() != null) {
                    Path path = root.get("areaId");
                    predicates.add(cb.equal(path, criteria.getAreaToken()));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.Zone> result = getRDBClient().getDbManager().getZoneRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.Zone> page = getRDBClient().getDbManager().getZoneRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public IZone deleteZone(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.Zone> opt = getRDBClient().getDbManager().getZoneRepository().findById(id);
        if(opt.isPresent()) {
            getRDBClient().getDbManager().getZoneRepository().deleteById(id);
        }
        return opt.get();
    }

    @Override
    public IDeviceGroup createDeviceGroup(IDeviceGroupCreateRequest request) throws SiteWhereException {
        // Use common logic so all backend implementations work the same.
        DeviceGroup group = DeviceManagementPersistence.deviceGroupCreateLogic(request);
        com.sitewhere.rdb.entities.DeviceGroup created = new com.sitewhere.rdb.entities.DeviceGroup();
        BeanUtils.copyProperties(group, created);
        created = getRDBClient().getDbManager().getDeviceGroupRepository().save(created);
        return created;
    }

    @Override
    public IDeviceGroup getDeviceGroup(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceGroup> opt = getRDBClient().getDbManager().getDeviceGroupRepository().findById(id);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IDeviceGroup getDeviceGroupByToken(String token) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceGroup> opt = getRDBClient().getDbManager().getDeviceGroupRepository().findByToken(token);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IDeviceGroup updateDeviceGroup(UUID id, IDeviceGroupCreateRequest request) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceGroup> opt = getRDBClient().getDbManager().getDeviceGroupRepository().findById(id);
        com.sitewhere.rdb.entities.DeviceGroup updated = null;
        if(opt.isPresent()) {
            updated = opt.get();
            DeviceGroup target = new DeviceGroup();
            target.setId(id);
            DeviceManagementPersistence.deviceGroupUpdateLogic(request, target);
            BeanUtils.copyProperties(target, updated);
            updated = getRDBClient().getDbManager().getDeviceGroupRepository().save(updated);
        }
        return updated;
    }

    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroups(ISearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.DESC,"createdDate");
        Specification<com.sitewhere.rdb.entities.DeviceGroup> specification = new Specification<com.sitewhere.rdb.entities.DeviceGroup>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.DeviceGroup> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return null;
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.DeviceGroup> result = getRDBClient().getDbManager().getDeviceGroupRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.DeviceGroup> page = getRDBClient().getDbManager().getDeviceGroupRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroupsWithRole(String role, ISearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.DESC,"createdDate");
        Specification<com.sitewhere.rdb.entities.DeviceGroup> specification = new Specification<com.sitewhere.rdb.entities.DeviceGroup>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.DeviceGroup> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList();
                Path path = root.get("roles");
                predicates.add(path.in(role));
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.DeviceGroup> result = getRDBClient().getDbManager().getDeviceGroupRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.DeviceGroup> page = getRDBClient().getDbManager().getDeviceGroupRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public IDeviceGroup deleteDeviceGroup(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceGroup> opt = getRDBClient().getDbManager().getDeviceGroupRepository().findById(id);
        if(opt.isPresent()) {
            getRDBClient().getDbManager().getDeviceGroupRepository().deleteById(id);
        }
        return opt.get();
    }

    @Override
    public List<IDeviceGroupElement> addDeviceGroupElements(UUID groupId, List<IDeviceGroupElementCreateRequest> elements, boolean ignoreDuplicates) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceGroup> opt = getRDBClient().getDbManager().getDeviceGroupRepository().findById(groupId);
        List<IDeviceGroupElement> results = new ArrayList();
        com.sitewhere.rdb.entities.DeviceGroup group = opt.get();
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

            com.sitewhere.rdb.entities.DeviceGroupElement created = new com.sitewhere.rdb.entities.DeviceGroupElement();
            DeviceGroupElement element = DeviceManagementPersistence.deviceGroupElementCreateLogic(request, group, device, nested);
            BeanUtils.copyProperties(element, created);
            created = getRDBClient().getDbManager().getDeviceGroupElementRepository().save(created);
            results.add(created);
        }
        return results;
    }

    @Override
    public List<IDeviceGroupElement> removeDeviceGroupElements(List<UUID> elementIds) throws SiteWhereException {
        List<IDeviceGroupElement> deleted = new ArrayList<IDeviceGroupElement>();
        for (UUID elementId : elementIds) {
            Optional<com.sitewhere.rdb.entities.DeviceGroupElement> opt = getRDBClient().getDbManager().getDeviceGroupElementRepository().findById(elementId);
            if(opt.isPresent()) {
                com.sitewhere.rdb.entities.DeviceGroupElement deleteEle = opt.get();
                getRDBClient().getDbManager().getDeviceGroupElementRepository().deleteById(deleteEle.getId());
                deleted.add(deleteEle);
            }
        }
        return deleted;
    }

    @Override
    public ISearchResults<IDeviceGroupElement> listDeviceGroupElements(UUID groupId, ISearchCriteria criteria) throws SiteWhereException {
        Specification<com.sitewhere.rdb.entities.DeviceGroupElement> specification = new Specification<com.sitewhere.rdb.entities.DeviceGroupElement>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.DeviceGroupElement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList();
                Path path = root.get("groupId");
                predicates.add(cb.equal(path, groupId));
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.DeviceGroupElement> result = getRDBClient().getDbManager().getDeviceGroupElementRepository().findAll(specification);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.DeviceGroupElement> page = getRDBClient().getDbManager().getDeviceGroupElementRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize()));
            return new SearchResultsConverter().convert(page.getContent());
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
        List<UUID> result = new ArrayList();
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
        List<UUID> cctids = new ArrayList();
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
        List<UUID> catids = new ArrayList();
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

    @Override
    public DeviceManagementRDBClient getRDBClient() throws SiteWhereException {
        String tenantId = this.getTenantEngine().getTenant().getId().toString();
        MultiTenantContext.setTenantId(tenantId);
        return dbClient;
    }

    @Override
    public void ensureIndexes() throws SiteWhereException { }
}
