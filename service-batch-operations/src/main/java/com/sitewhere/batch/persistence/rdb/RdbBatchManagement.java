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
package com.sitewhere.batch.persistence.rdb;

import java.util.List;
import java.util.UUID;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.sitewhere.batch.persistence.BatchManagementPersistence;
import com.sitewhere.batch.persistence.rdb.entity.Queries;
import com.sitewhere.batch.persistence.rdb.entity.RdbBatchElement;
import com.sitewhere.batch.persistence.rdb.entity.RdbBatchOperation;
import com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice;
import com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine;
import com.sitewhere.microservice.api.batch.IBatchManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.rdb.RdbTenantComponent;
import com.sitewhere.rdb.spi.IRdbEntityManagerProvider;
import com.sitewhere.rdb.spi.IRdbQueryProvider;
import com.sitewhere.rest.model.batch.BatchElement;
import com.sitewhere.rest.model.batch.BatchOperation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.batch.request.IBatchElementCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.batch.IBatchOperationSearchCriteria;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;

/**
 * Implementation of {@link IBatchManagement} that stores data in RDB.
 */
public class RdbBatchManagement extends RdbTenantComponent implements IBatchManagement {

    /*
     * @see
     * com.sitewhere.microservice.api.batch.IBatchManagement#createBatchOperation(
     * com.sitewhere.spi.batch.request.IBatchOperationCreateRequest)
     */
    @Override
    public RdbBatchOperation createBatchOperation(IBatchOperationCreateRequest request) throws SiteWhereException {
	// Use common logic so all backend implementations work the same.
	BatchOperation batch = BatchManagementPersistence.batchOperationCreateLogic(request);
	RdbBatchOperation created = new RdbBatchOperation();
	RdbBatchOperation.copy(batch, created);
	return getEntityManagerProvider().persist(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.batch.IBatchManagement#updateBatchOperation(
     * java.util.UUID, com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest)
     */
    @Override
    public RdbBatchOperation updateBatchOperation(UUID batchOperationId, IBatchOperationUpdateRequest request)
	    throws SiteWhereException {
	RdbBatchOperation existing = getEntityManagerProvider().findById(batchOperationId, RdbBatchOperation.class);
	if (existing != null) {
	    BatchOperation updates = new BatchOperation();

	    // Use common update logic.
	    BatchManagementPersistence.batchOperationUpdateLogic(request, updates);
	    RdbBatchOperation.copy(updates, existing);
	    return getEntityManagerProvider().merge(existing);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.batch.IBatchManagement#getBatchOperation(java.
     * util.UUID)
     */
    @Override
    public RdbBatchOperation getBatchOperation(UUID batchOperationId) throws SiteWhereException {
	return getEntityManagerProvider().findById(batchOperationId, RdbBatchOperation.class);
    }

    /*
     * @see com.sitewhere.microservice.api.batch.IBatchManagement#
     * getBatchOperationByToken(java.lang.String)
     */
    @Override
    public RdbBatchOperation getBatchOperationByToken(String token) throws SiteWhereException {
	Query query = getEntityManagerProvider().query(Queries.QUERY_BATCH_OPERATION_BY_TOKEN);
	query.setParameter("token", token);
	return getEntityManagerProvider().findOne(query, RdbBatchOperation.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.batch.IBatchManagement#deleteBatchOperation(
     * java.util.UUID)
     */
    @Override
    public RdbBatchOperation deleteBatchOperation(UUID batchOperationId) throws SiteWhereException {
	return getEntityManagerProvider().remove(batchOperationId, RdbBatchOperation.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.batch.IBatchManagement#listBatchOperations(com
     * .sitewhere.spi.search.batch.IBatchOperationSearchCriteria)
     */
    @Override
    public ISearchResults<? extends IBatchOperation> listBatchOperations(IBatchOperationSearchCriteria criteria)
	    throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbBatchOperation>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbBatchOperation> root)
		    throws SiteWhereException {
		// No search options available.
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbBatchOperation> addSort(CriteriaBuilder cb, Root<RdbBatchOperation> root,
		    CriteriaQuery<RdbBatchOperation> query) {
		return query.orderBy(cb.desc(root.get("processingStartedDate")));
	    }
	}, RdbBatchOperation.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.batch.IBatchManagement#createBatchElement(java
     * .util.UUID, com.sitewhere.spi.batch.request.IBatchElementCreateRequest)
     */
    @Override
    public RdbBatchElement createBatchElement(UUID batchOperationId, IBatchElementCreateRequest request)
	    throws SiteWhereException {
	IBatchOperation batch = getBatchOperation(batchOperationId);
	if (batch == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidBatchOperationId, ErrorLevel.ERROR);
	}

	IDevice device = getDeviceManagement().getDeviceByToken(request.getDeviceToken());
	if (device == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceToken, ErrorLevel.ERROR);
	}

	// Use common logic so all backend implementations work the same.
	BatchElement element = BatchManagementPersistence.batchElementCreateLogic(batch, device);
	RdbBatchElement created = new RdbBatchElement();
	RdbBatchElement.copy(element, created);
	return getEntityManagerProvider().persist(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.batch.IBatchManagement#updateBatchElement(java
     * .util.UUID, com.sitewhere.spi.batch.request.IBatchElementCreateRequest)
     */
    @Override
    public RdbBatchElement updateBatchElement(UUID elementId, IBatchElementCreateRequest request)
	    throws SiteWhereException {
	RdbBatchElement existing = getEntityManagerProvider().findById(elementId, RdbBatchElement.class);
	if (existing != null) {
	    BatchElement updates = new BatchElement();

	    // Use common update logic.
	    BatchManagementPersistence.batchElementUpdateLogic(request, updates);
	    RdbBatchElement.copy(updates, existing);
	    return getEntityManagerProvider().merge(existing);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.batch.IBatchManagement#listBatchElements(java.
     * util.UUID, com.sitewhere.spi.search.device.IBatchElementSearchCriteria)
     */
    @Override
    public ISearchResults<? extends IBatchElement> listBatchElements(UUID batchOperationId,
	    IBatchElementSearchCriteria criteria) throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbBatchElement>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbBatchElement> root)
		    throws SiteWhereException {
		if (criteria.getProcessingStatus() != null) {
		    predicates.add(cb.equal(root.get("processingStatus"), criteria.getProcessingStatus()));
		}
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbBatchElement> addSort(CriteriaBuilder cb, Root<RdbBatchElement> root,
		    CriteriaQuery<RdbBatchElement> query) {
		return query.orderBy(cb.desc(root.get("processedDate")));
	    }
	}, RdbBatchElement.class);
    }

    /*
     * @see com.sitewhere.microservice.api.batch.IBatchManagement#
     * createBatchCommandInvocation(com.sitewhere.spi.batch.request.
     * IBatchCommandInvocationRequest)
     */
    @Override
    public RdbBatchOperation createBatchCommandInvocation(IBatchCommandInvocationRequest request)
	    throws SiteWhereException {
	IBatchOperationCreateRequest generic = BatchManagementPersistence.batchCommandInvocationCreateLogic(request);
	return createBatchOperation(generic);
    }

    /*
     * @see com.sitewhere.rdb.spi.IRdbTenantComponent#getEntityManagerProvider()
     */
    @Override
    public IRdbEntityManagerProvider getEntityManagerProvider() {
	return ((IBatchOperationsTenantEngine) getTenantEngine()).getRdbEntityManagerProvider();
    }

    protected IDeviceManagement getDeviceManagement() {
	return ((IBatchOperationsMicroservice) getMicroservice()).getDeviceManagement();
    }
}
