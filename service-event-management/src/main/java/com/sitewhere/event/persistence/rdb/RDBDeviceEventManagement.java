package com.sitewhere.event.persistence.rdb;

import com.sitewhere.event.persistence.DeviceEventManagementPersistence;
import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.rdb.DbClient;
import com.sitewhere.rest.model.device.event.*;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.*;
import com.sitewhere.spi.device.event.request.*;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Simeon Chen
 */
public class RDBDeviceEventManagement extends TenantEngineLifecycleComponent implements IDeviceEventManagement {

    /** Injected with global SiteWhere relational database client */
    private DbClient dbClient;

    public DbClient getDbClient() {
        return dbClient;
    }

    public void setDbClient(DbClient dbClient) {
        this.dbClient = dbClient;
    }

    /**
     * Constructor
     */
    public RDBDeviceEventManagement() {
        super(LifecycleComponentType.DataStore);
    }

    @Override
    public IDeviceEventBatchResponse addDeviceEventBatch(UUID deviceAssignmentId, IDeviceEventBatch batch) throws SiteWhereException {
        IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
        return DeviceEventManagementPersistence.deviceEventBatchLogic(assignment, batch, this);
    }

    @Override
    public IDeviceEvent getDeviceEventById(UUID eventId) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.event.DeviceEvent> opt = dbClient.getDbManager().getDeviceEventRepository().findById(eventId);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IDeviceEvent getDeviceEventByAlternateId(String alternateId) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.event.DeviceEvent> opt = dbClient.getDbManager().getDeviceEventRepository().findByAlternateId(alternateId);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public List<IDeviceMeasurement> addDeviceMeasurements(UUID deviceAssignmentId, IDeviceMeasurementCreateRequest... requests) throws SiteWhereException {
        List<IDeviceMeasurement> result = new ArrayList<>();
        IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
        for (IDeviceMeasurementCreateRequest request : requests) {
            DeviceMeasurement measurements = DeviceEventManagementPersistence.deviceMeasurementCreateLogic(request, assignment);
            com.sitewhere.rdb.entities.event.DeviceMeasurement target = new com.sitewhere.rdb.entities.event.DeviceMeasurement();
            BeanUtils.copyProperties(measurements, target);
            target = dbClient.getDbManager().getDeviceMeasurementRepository().save(target);
            result.add(target);
        }
        return result;
    }

    @Override
    public ISearchResults<IDeviceMeasurement> listDeviceMeasurementsForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.DESC,"eventDate");
        Specification<com.sitewhere.rdb.entities.event.DeviceMeasurement> specification = new Specification<com.sitewhere.rdb.entities.event.DeviceMeasurement>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.event.DeviceMeasurement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                Path path = root.get("id");
                predicates.add(path.in(entityIds));

                if(criteria.getStartDate() != null) {
                    Path eventDate = root.get("eventDate");
                    predicates.add(cb.greaterThanOrEqualTo(eventDate, criteria.getStartDate()));
                }
                if(criteria.getStartDate() != null) {
                    Path eventDate = root.get("eventDate");
                    predicates.add(cb.lessThanOrEqualTo(eventDate, criteria.getEndDate()));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.event.DeviceMeasurement> result = dbClient.getDbManager().getDeviceMeasurementRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.event.DeviceMeasurement> page = dbClient.getDbManager().getDeviceMeasurementRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public List<IDeviceLocation> addDeviceLocations(UUID deviceAssignmentId, IDeviceLocationCreateRequest... requests) throws SiteWhereException {
        List<IDeviceLocation> result = new ArrayList<>();
        IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
        for (IDeviceLocationCreateRequest request : requests) {
            DeviceLocation location = DeviceEventManagementPersistence.deviceLocationCreateLogic(assignment, request);
            com.sitewhere.rdb.entities.event.DeviceLocation target = new com.sitewhere.rdb.entities.event.DeviceLocation();
            BeanUtils.copyProperties(location, target);
            target = dbClient.getDbManager().getDeviceLocationRepository().save(target);
            result.add(target);
        }
        return result;
    }

    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocationsForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.DESC,"eventDate");
        Specification<com.sitewhere.rdb.entities.event.DeviceLocation> specification = new Specification<com.sitewhere.rdb.entities.event.DeviceLocation>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.event.DeviceLocation> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                Path path = root.get("id");
                predicates.add(path.in(entityIds));
                Path eventType = root.get("eventType");
                predicates.add(cb.equal(eventType, DeviceEventType.Location.name()));

                if(criteria.getStartDate() != null) {
                    Path eventDate = root.get("eventDate");
                    predicates.add(cb.greaterThanOrEqualTo(eventDate, criteria.getStartDate()));
                }
                if(criteria.getStartDate() != null) {
                    Path eventDate = root.get("eventDate");
                    predicates.add(cb.lessThanOrEqualTo(eventDate, criteria.getEndDate()));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.event.DeviceLocation> result = dbClient.getDbManager().getDeviceLocationRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.event.DeviceLocation> page = dbClient.getDbManager().getDeviceLocationRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public List<IDeviceAlert> addDeviceAlerts(UUID deviceAssignmentId, IDeviceAlertCreateRequest... requests) throws SiteWhereException {
        List<IDeviceAlert> result = new ArrayList<>();
        IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
        for (IDeviceAlertCreateRequest request : requests) {
            DeviceAlert alert = DeviceEventManagementPersistence.deviceAlertCreateLogic(assignment, request);
            com.sitewhere.rdb.entities.event.DeviceAlert target = new com.sitewhere.rdb.entities.event.DeviceAlert();
            BeanUtils.copyProperties(alert, target);
            target = dbClient.getDbManager().getDeviceAlertRepository().save(target);
            result.add(target);
        }
        return result;
    }

    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlertsForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.DESC,"eventDate");
        Specification<com.sitewhere.rdb.entities.event.DeviceAlert> specification = new Specification<com.sitewhere.rdb.entities.event.DeviceAlert>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.event.DeviceAlert> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                Path path = root.get("id");
                predicates.add(path.in(entityIds));
                Path eventType = root.get("eventType");
                predicates.add(cb.equal(eventType, DeviceEventType.Alert.name()));
                if(criteria.getStartDate() != null) {
                    Path eventDate = root.get("eventDate");
                    predicates.add(cb.greaterThanOrEqualTo(eventDate, criteria.getStartDate()));
                }
                if(criteria.getStartDate() != null) {
                    Path eventDate = root.get("eventDate");
                    predicates.add(cb.lessThanOrEqualTo(eventDate, criteria.getEndDate()));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.event.DeviceAlert> result = dbClient.getDbManager().getDeviceAlertRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.event.DeviceAlert> page = dbClient.getDbManager().getDeviceAlertRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public List<IDeviceCommandInvocation> addDeviceCommandInvocations(UUID deviceAssignmentId, IDeviceCommandInvocationCreateRequest... requests) throws SiteWhereException {
        List<IDeviceCommandInvocation> result = new ArrayList<>();
        IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
        for (IDeviceCommandInvocationCreateRequest request : requests) {
            DeviceCommandInvocation ci = DeviceEventManagementPersistence.deviceCommandInvocationCreateLogic(assignment,
                    request);
            com.sitewhere.rdb.entities.event.DeviceCommandInvocation target = new com.sitewhere.rdb.entities.event.DeviceCommandInvocation();
            BeanUtils.copyProperties(ci, target);
            target = dbClient.getDbManager().getDeviceCommandInvocationRepository().save(target);
            result.add(target);
        }
        return result;
    }

    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.DESC,"eventDate");
        Specification<com.sitewhere.rdb.entities.event.DeviceCommandInvocation> specification = new Specification<com.sitewhere.rdb.entities.event.DeviceCommandInvocation>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.event.DeviceCommandInvocation> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                Path path = root.get("id");
                predicates.add(path.in(entityIds));
                Path eventType = root.get("eventType");
                predicates.add(cb.equal(eventType, DeviceEventType.CommandInvocation.name()));
                if(criteria.getStartDate() != null) {
                    Path eventDate = root.get("eventDate");
                    predicates.add(cb.greaterThanOrEqualTo(eventDate, criteria.getStartDate()));
                }
                if(criteria.getStartDate() != null) {
                    Path eventDate = root.get("eventDate");
                    predicates.add(cb.lessThanOrEqualTo(eventDate, criteria.getEndDate()));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.event.DeviceCommandInvocation> result = dbClient.getDbManager().getDeviceCommandInvocationRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.event.DeviceCommandInvocation> page = dbClient.getDbManager().getDeviceCommandInvocationRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(UUID invocationId) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.DESC,"eventDate");
        Specification<com.sitewhere.rdb.entities.event.DeviceCommandResponse> specification = new Specification<com.sitewhere.rdb.entities.event.DeviceCommandResponse>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.event.DeviceCommandResponse> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                Path eventId = root.get("originatingEventId");
                predicates.add(cb.equal(eventId, invocationId));
                Path eventType = root.get("eventType");
                predicates.add(cb.equal(eventType, DeviceEventType.CommandResponse.name()));
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        List<com.sitewhere.rdb.entities.event.DeviceCommandResponse> result = dbClient.getDbManager().getDeviceCommandResponseRepository().findAll(specification, sort);
        return new SearchResultsConverter().convert(result);
    }

    @Override
    public List<IDeviceCommandResponse> addDeviceCommandResponses(UUID deviceAssignmentId, IDeviceCommandResponseCreateRequest... requests) throws SiteWhereException {
        List<IDeviceCommandResponse> result = new ArrayList<>();
        IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
        for (IDeviceCommandResponseCreateRequest request : requests) {
            DeviceCommandResponse response = DeviceEventManagementPersistence
                    .deviceCommandResponseCreateLogic(assignment, request);
            com.sitewhere.rdb.entities.event.DeviceCommandResponse target = new com.sitewhere.rdb.entities.event.DeviceCommandResponse();
            BeanUtils.copyProperties(response, target);
            target = dbClient.getDbManager().getDeviceCommandResponseRepository().save(target);
            result.add(target);
        }
        return result;
    }

    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.DESC,"eventDate");
        Specification<com.sitewhere.rdb.entities.event.DeviceCommandResponse> specification = new Specification<com.sitewhere.rdb.entities.event.DeviceCommandResponse>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.event.DeviceCommandResponse> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                Path path = root.get("id");
                predicates.add(path.in(entityIds));
                Path eventType = root.get("eventType");
                predicates.add(cb.equal(eventType, DeviceEventType.CommandResponse.name()));
                if(criteria.getStartDate() != null) {
                    Path eventDate = root.get("eventDate");
                    predicates.add(cb.greaterThanOrEqualTo(eventDate, criteria.getStartDate()));
                }
                if(criteria.getStartDate() != null) {
                    Path eventDate = root.get("eventDate");
                    predicates.add(cb.lessThanOrEqualTo(eventDate, criteria.getEndDate()));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.event.DeviceCommandResponse> result = dbClient.getDbManager().getDeviceCommandResponseRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.event.DeviceCommandResponse> page = dbClient.getDbManager().getDeviceCommandResponseRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public List<IDeviceStateChange> addDeviceStateChanges(UUID deviceAssignmentId, IDeviceStateChangeCreateRequest... requests) throws SiteWhereException {
        List<IDeviceStateChange> result = new ArrayList<>();
        IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
        for (IDeviceStateChangeCreateRequest request : requests) {
            DeviceStateChange state = DeviceEventManagementPersistence.deviceStateChangeCreateLogic(assignment, request);
            com.sitewhere.rdb.entities.event.DeviceStateChange target = new com.sitewhere.rdb.entities.event.DeviceStateChange();
            BeanUtils.copyProperties(state, target);
            target = dbClient.getDbManager().getDeviceStateChangeRepository().save(target);
            result.add(target);
        }
        return result;
    }

    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForIndex(DeviceEventIndex index, List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.DESC,"eventDate");
        Specification<com.sitewhere.rdb.entities.event.DeviceStateChange> specification = new Specification<com.sitewhere.rdb.entities.event.DeviceStateChange>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.event.DeviceStateChange> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                Path path = root.get("id");
                predicates.add(path.in(entityIds));
                Path eventType = root.get("eventType");
                predicates.add(cb.equal(eventType, DeviceEventType.StateChange.name()));
                if(criteria.getStartDate() != null) {
                    Path eventDate = root.get("eventDate");
                    predicates.add(cb.greaterThanOrEqualTo(eventDate, criteria.getStartDate()));
                }
                if(criteria.getStartDate() != null) {
                    Path eventDate = root.get("eventDate");
                    predicates.add(cb.lessThanOrEqualTo(eventDate, criteria.getEndDate()));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.event.DeviceStateChange> result = dbClient.getDbManager().getDeviceStateChangeRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.event.DeviceStateChange> page = dbClient.getDbManager().getDeviceStateChangeRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    /**
     * Assert that a device assignment exists and throw an exception if not.
     *
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment assertDeviceAssignmentById(UUID id) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.DeviceAssignment> opt = dbClient.getDbManager().getDeviceAssignmentRepository().findById(id);
        if (!opt.isPresent()) {
            throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentId, ErrorLevel.ERROR);
        }
        return opt.get();
    }

    protected IDeviceManagement getDeviceManagement() {
        return ((IEventManagementMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiChannel();
    }
}
