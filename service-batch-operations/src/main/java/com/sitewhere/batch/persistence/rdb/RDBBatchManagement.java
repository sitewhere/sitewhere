/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.persistence.rdb;

import com.sitewhere.batch.persistence.BatchManagementPersistence;
import com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice;
import com.sitewhere.rdb.RDBTenantComponent;
import com.sitewhere.rdb.multitenancy.MultiTenantContext;
import com.sitewhere.rest.model.batch.BatchElement;
import com.sitewhere.rest.model.batch.BatchOperation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchManagement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.batch.request.IBatchElementCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.batch.IBatchOperationSearchCriteria;
import com.sitewhere.spi.search.device.*;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * Batch management implementation that uses Relational database for persistence.
 *
 * @author Luciano Baez
 */
public class RDBBatchManagement extends RDBTenantComponent<BatchManagementRDBClient> implements IBatchManagement {

    public RDBBatchManagement() {
        super(LifecycleComponentType.DataStore);
    }

    /** Injected with global SiteWhere relational database client */
    private BatchManagementRDBClient dbClient;

    @Override
    public BatchManagementRDBClient getRDBClient() throws SiteWhereException {
        String tenantId = this.getTenantEngine().getTenant().getId().toString();
        MultiTenantContext.setTenantId(tenantId);
        return dbClient;
    }

    @Override
    public void ensureIndexes() throws SiteWhereException { }

    @Override
    public IBatchOperation createBatchOperation(IBatchOperationCreateRequest request) throws SiteWhereException {
        BatchOperation batch = BatchManagementPersistence.batchOperationCreateLogic(request);
        com.sitewhere.rdb.entities.BatchOperation created = new com.sitewhere.rdb.entities.BatchOperation();
        BeanUtils.copyProperties(batch, created);
        created = getRDBClient().getDbManager().getBatchOperationRepository().save(created);
        return created;
    }

    @Override
    public IBatchOperation updateBatchOperation(UUID batchOperationId, IBatchOperationUpdateRequest request) throws SiteWhereException {
        com.sitewhere.rdb.entities.BatchOperation batchOperation = null;
        Optional<com.sitewhere.rdb.entities.BatchOperation> opt = getRDBClient().getDbManager().getBatchOperationRepository().findById(batchOperationId);

        if(opt.isPresent()) {
            batchOperation = opt.get();
            BatchOperation target = new BatchOperation();
            BatchManagementPersistence.batchOperationUpdateLogic(request, target);
            target.setId(batchOperation.getId());
            BeanUtils.copyProperties(target, batchOperation);
            batchOperation = getRDBClient().getDbManager().getBatchOperationRepository().save(batchOperation);
        }
        return batchOperation;
    }

    @Override
    public IBatchOperation getBatchOperation(UUID batchOperationId) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.BatchOperation> opt = getRDBClient().getDbManager().getBatchOperationRepository().findById(batchOperationId);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IBatchOperation getBatchOperationByToken(String token) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.BatchOperation> opt = getRDBClient().getDbManager().getBatchOperationRepository().findByToken(token);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public ISearchResults<IBatchOperation> listBatchOperations(IBatchOperationSearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.DESC,"createdDate");
        Specification<com.sitewhere.rdb.entities.BatchOperation> specification = new Specification<com.sitewhere.rdb.entities.BatchOperation>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.BatchOperation> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return null;
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.BatchOperation> result = getRDBClient().getDbManager().getBatchOperationRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.BatchOperation> page = getRDBClient().getDbManager().getBatchOperationRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public IBatchOperation deleteBatchOperation(UUID batchOperationId) throws SiteWhereException {

        Optional<com.sitewhere.rdb.entities.BatchOperation> opt = getRDBClient().getDbManager().getBatchOperationRepository().findById(batchOperationId);
        if(opt.isPresent()) {
            getRDBClient().getDbManager().getBatchOperationRepository().deleteById(batchOperationId);
        }
        return opt.get();
    }

    @Override
    public IBatchElement createBatchElement(UUID batchOperationId, IBatchElementCreateRequest request) throws SiteWhereException {
        IBatchOperation operation = getBatchOperation(batchOperationId);
        IDevice device = getDeviceManagement().getDeviceByToken(request.getDeviceToken());
        BatchElement element = BatchManagementPersistence.batchElementCreateLogic(operation, device);

        com.sitewhere.rdb.entities.BatchElement created = new com.sitewhere.rdb.entities.BatchElement();
        BeanUtils.copyProperties(element, created);
        getRDBClient().getDbManager().getBatchOperationElementRepository().save(created);
        return created;
    }

    @Override
    public ISearchResults<IBatchElement> listBatchElements(UUID batchOperationId, IBatchElementSearchCriteria criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.DESC,"createdDate");
        Specification<com.sitewhere.rdb.entities.BatchElement> specification = new Specification<com.sitewhere.rdb.entities.BatchElement>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.BatchElement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return null;
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.BatchElement> result = getRDBClient().getDbManager().getBatchOperationElementRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.BatchElement> page = getRDBClient().getDbManager().getBatchOperationElementRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public IBatchElement updateBatchElement(UUID elementId, IBatchElementCreateRequest request) throws SiteWhereException {

        com.sitewhere.rdb.entities.BatchElement batchElement = null;
        Optional<com.sitewhere.rdb.entities.BatchElement> opt = getRDBClient().getDbManager().getBatchOperationElementRepository().findById(elementId);

        if(opt.isPresent()) {
            batchElement = opt.get();
            BatchElement target = new BatchElement();
            BatchManagementPersistence.batchElementUpdateLogic(request, target);
            target.setId(batchElement.getId());
            BeanUtils.copyProperties(target, batchElement);
            batchElement = getRDBClient().getDbManager().getBatchOperationElementRepository().save(batchElement);
        }
        return batchElement;
    }

    @Override
    public IBatchOperation createBatchCommandInvocation(IBatchCommandInvocationRequest request) throws SiteWhereException {
        String uuid = ((request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString());
        IBatchOperationCreateRequest generic = BatchManagementPersistence.batchCommandInvocationCreateLogic(request,
                uuid);
        return createBatchOperation(generic);
    }

    public IDeviceManagement getDeviceManagement() {
        return ((IBatchOperationsMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiChannel();
    }


    public BatchManagementRDBClient getDbClient() {
        return dbClient;
    }

    public void setDbClient(BatchManagementRDBClient dbClient) {
        this.dbClient = dbClient;
    }
}
