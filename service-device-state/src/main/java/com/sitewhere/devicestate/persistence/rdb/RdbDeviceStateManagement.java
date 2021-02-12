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
package com.sitewhere.devicestate.persistence.rdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.sitewhere.devicestate.microservice.DeviceStateMicroservice;
import com.sitewhere.devicestate.microservice.DeviceStateTenantEngine;
import com.sitewhere.devicestate.persistence.DeviceStatePersistence;
import com.sitewhere.devicestate.persistence.rdb.entity.RdbDeviceState;
import com.sitewhere.devicestate.spi.IDeviceStateMergeStrategy;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.state.IDeviceStateManagement;
import com.sitewhere.rdb.RdbTenantComponent;
import com.sitewhere.rdb.spi.IRdbEntityManagerProvider;
import com.sitewhere.rdb.spi.IRdbQueryProvider;
import com.sitewhere.rest.model.device.state.DeviceState;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.state.IDeviceState;
import com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest;
import com.sitewhere.spi.device.state.request.IDeviceStateEventMergeRequest;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IDeviceStateSearchCriteria;

/**
 * Device state management implementation that uses a relational database for
 * persistence.
 */
public class RdbDeviceStateManagement extends RdbTenantComponent implements IDeviceStateManagement {

    /*
     * @see
     * com.sitewhere.microservice.api.state.IDeviceStateManagement#createDeviceState
     * (com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest)
     */
    @Override
    public RdbDeviceState createDeviceState(IDeviceStateCreateRequest request) throws SiteWhereException {
	DeviceState state = DeviceStatePersistence.deviceStateCreateLogic(request);
	RdbDeviceState created = new RdbDeviceState();
	RdbDeviceState.copy(state, created);
	return getEntityManagerProvider().persist(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.state.IDeviceStateManagement#getDeviceState(
     * java.util.UUID)
     */
    @Override
    public RdbDeviceState getDeviceState(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().findById(id, RdbDeviceState.class);
    }

    /*
     * @see com.sitewhere.microservice.api.state.IDeviceStateManagement#
     * getDeviceStateByDeviceAssignment(java.util.UUID)
     */
    @Override
    public IDeviceState getDeviceStateByDeviceAssignment(UUID assignmentId) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(1, 0);
	ISearchResults<RdbDeviceState> results = getEntityManagerProvider().findWithCriteria(criteria,
		new IRdbQueryProvider<RdbDeviceState>() {

		    /*
		     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
		     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
		     */
		    @Override
		    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbDeviceState> root)
			    throws SiteWhereException {
			Path<UUID> path = root.get("deviceAssignmentId");
			predicates.add(cb.equal(path, assignmentId));
		    }

		    /*
		     * @see
		     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
		     * CriteriaBuilder, javax.persistence.criteria.Root,
		     * javax.persistence.criteria.CriteriaQuery)
		     */
		    @Override
		    public CriteriaQuery<RdbDeviceState> addSort(CriteriaBuilder cb, Root<RdbDeviceState> root,
			    CriteriaQuery<RdbDeviceState> query) {
			return query.orderBy(cb.desc(root.get("lastInteractionDate")));
		    }
		}, RdbDeviceState.class);
	return results.getResults().size() > 0 ? results.getResults().get(0) : null;
    }

    /*
     * @see com.sitewhere.microservice.api.state.IDeviceStateManagement#
     * getDeviceStatesForDevice(java.util.UUID)
     */
    @Override
    public List<? extends IDeviceState> getDeviceStatesForDevice(UUID deviceId) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(1, 0);
	ISearchResults<RdbDeviceState> results = getEntityManagerProvider().findWithCriteria(criteria,
		new IRdbQueryProvider<RdbDeviceState>() {

		    /*
		     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
		     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
		     */
		    @Override
		    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbDeviceState> root)
			    throws SiteWhereException {
			Path<UUID> path = root.get("deviceId");
			predicates.add(cb.equal(path, deviceId));
		    }

		    /*
		     * @see
		     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
		     * CriteriaBuilder, javax.persistence.criteria.Root,
		     * javax.persistence.criteria.CriteriaQuery)
		     */
		    @Override
		    public CriteriaQuery<RdbDeviceState> addSort(CriteriaBuilder cb, Root<RdbDeviceState> root,
			    CriteriaQuery<RdbDeviceState> query) {
			return query.orderBy(cb.desc(root.get("lastInteractionDate")));
		    }
		}, RdbDeviceState.class);
	return results.getResults();
    }

    /*
     * @see
     * com.sitewhere.microservice.api.state.IDeviceStateManagement#updateDeviceState
     * (java.util.UUID,
     * com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest)
     */
    @Override
    public RdbDeviceState updateDeviceState(UUID id, IDeviceStateCreateRequest request) throws SiteWhereException {
	RdbDeviceState existing = getEntityManagerProvider().findById(id, RdbDeviceState.class);
	if (existing != null) {
	    DeviceState updates = new DeviceState();

	    // Use common update logic.
	    DeviceStatePersistence.deviceStateUpdateLogic(request, updates);
	    RdbDeviceState.copy(updates, existing);
	    return getEntityManagerProvider().merge(existing);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.state.IDeviceStateManagement#merge(java.util.
     * UUID, com.sitewhere.spi.device.state.request.IDeviceStateEventMergeRequest)
     */
    @Override
    public IDeviceState merge(UUID id, IDeviceStateEventMergeRequest events) throws SiteWhereException {
	return getDeviceStateMergeStrategy().merge(id, events);
    }

    /*
     * @see com.sitewhere.microservice.api.state.IDeviceStateManagement#
     * searchDeviceStates(com.sitewhere.spi.search.device.
     * IDeviceStateSearchCriteria)
     */
    @Override
    public ISearchResults<RdbDeviceState> searchDeviceStates(IDeviceStateSearchCriteria criteria)
	    throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbDeviceState>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbDeviceState> root)
		    throws SiteWhereException {
		if (criteria.getLastInteractionDateBefore() != null) {
		    Path<Date> path = root.get("lastInteractionDate");
		    predicates.add(cb.lessThan(path, criteria.getLastInteractionDateBefore()));
		}
		if ((criteria.getDeviceTokens() != null) && (criteria.getDeviceTokens().size() > 0)) {
		    try {
			List<UUID> ids = getDeviceIds(criteria.getDeviceTokens());
			Path<UUID> path = root.get("deviceId");
			predicates.add(path.in(ids));
		    } catch (SiteWhereException e) {
			throw new SiteWhereException("Unable to look up device ids.", e);
		    }
		}
		if ((criteria.getDeviceTypeTokens() != null) && (criteria.getDeviceTypeTokens().size() > 0)) {
		    try {
			List<UUID> ids = getDeviceTypeIds(criteria.getDeviceTokens());
			Path<UUID> path = root.get("deviceTypeId");
			predicates.add(path.in(ids));
		    } catch (SiteWhereException e) {
			throw new SiteWhereException("Unable to look up device type ids.", e);
		    }
		}
		if ((criteria.getDeviceAssignmentTokens() != null)
			&& (criteria.getDeviceAssignmentTokens().size() > 0)) {
		    try {
			List<UUID> ids = getDeviceAssignmentIds(criteria.getDeviceAssignmentTokens());
			Path<UUID> path = root.get("deviceAssignmentId");
			predicates.add(path.in(ids));
		    } catch (SiteWhereException e) {
			throw new SiteWhereException("Unable to look up device type ids.", e);
		    }
		}
		if ((criteria.getCustomerTokens() != null) && (criteria.getCustomerTokens().size() > 0)) {
		    try {
			List<UUID> ids = getCustomerIds(criteria.getCustomerTokens());
			Path<UUID> path = root.get("customerId");
			predicates.add(path.in(ids));
		    } catch (SiteWhereException e) {
			throw new SiteWhereException("Unable to look up customer ids.", e);
		    }
		}
		if ((criteria.getAreaTokens() != null) && (criteria.getAreaTokens().size() > 0)) {
		    try {
			List<UUID> ids = getAreaIds(criteria.getAreaTokens());
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
	    public CriteriaQuery<RdbDeviceState> addSort(CriteriaBuilder cb, Root<RdbDeviceState> root,
		    CriteriaQuery<RdbDeviceState> query) {
		return query.orderBy(cb.desc(root.get("lastInteractionDate")));
	    }
	}, RdbDeviceState.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.state.IDeviceStateManagement#deleteDeviceState
     * (java.util.UUID)
     */
    @Override
    public RdbDeviceState deleteDeviceState(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().remove(id, RdbDeviceState.class);
    }

    /*
     * @see com.sitewhere.rdb.spi.IRdbTenantComponent#getEntityManagerProvider()
     */
    @Override
    public IRdbEntityManagerProvider getEntityManagerProvider() {
	return ((IDeviceStateTenantEngine) getTenantEngine()).getRdbEntityManagerProvider();
    }

    /**
     * Look up a list of device tokens to get the corresponding list of device ids.
     * 
     * @param tokens
     * @return
     * @throws SiteWhereException
     */
    public List<UUID> getDeviceIds(List<String> tokens) throws SiteWhereException {
	List<UUID> result = new ArrayList<>();
	for (String token : tokens) {
	    IDevice device = getDeviceManagement().getDeviceByToken(token);
	    result.add(device.getId());
	}
	return result;
    }

    /**
     * Look up a list of device type tokens to get the corresponding list of device
     * type ids.
     * 
     * @param tokens
     * @return
     * @throws SiteWhereException
     */
    public List<UUID> getDeviceTypeIds(List<String> tokens) throws SiteWhereException {
	List<UUID> result = new ArrayList<>();
	for (String token : tokens) {
	    IDeviceType type = getDeviceManagement().getDeviceTypeByToken(token);
	    result.add(type.getId());
	}
	return result;
    }

    /**
     * Look up a list of device assignment tokens to get the corresponding list of
     * device assignment ids.
     * 
     * @param tokens
     * @return
     * @throws SiteWhereException
     */
    public List<UUID> getDeviceAssignmentIds(List<String> tokens) throws SiteWhereException {
	List<UUID> result = new ArrayList<>();
	for (String token : tokens) {
	    IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(token);
	    result.add(assignment.getId());
	}
	return result;
    }

    /**
     * Look up a list of customer tokens to get the corresponding list of customer
     * ids.
     * 
     * @param tokens
     * @return
     * @throws SiteWhereException
     */
    public List<UUID> getCustomerIds(List<String> tokens) throws SiteWhereException {
	List<UUID> result = new ArrayList<>();
	for (String token : tokens) {
	    ICustomer customer = getDeviceManagement().getCustomerByToken(token);
	    result.add(customer.getId());
	}
	return result;
    }

    /**
     * Look up a list of area tokens to get the corresponding list of area ids.
     * 
     * @param tokens
     * @return
     * @throws SiteWhereException
     */
    public List<UUID> getAreaIds(List<String> tokens) throws SiteWhereException {
	List<UUID> result = new ArrayList<>();
	for (String token : tokens) {
	    IArea area = getDeviceManagement().getAreaByToken(token);
	    result.add(area.getId());
	}
	return result;
    }

    /**
     * Look up a list of asset tokens to get the corresponding list of asset ids.
     * 
     * @param tokens
     * @return
     * @throws SiteWhereException
     */
    public List<UUID> getAssetIds(List<String> tokens) throws SiteWhereException {
	List<UUID> result = new ArrayList<>();
	for (String token : tokens) {
	    IAsset asset = getAssetManagement().getAssetByToken(token);
	    result.add(asset.getId());
	}
	return result;
    }

    /**
     * Get device management implementation from microservice.
     * 
     * @return
     */
    protected IDeviceManagement getDeviceManagement() {
	return ((DeviceStateMicroservice) getTenantEngine().getMicroservice()).getDeviceManagement();
    }

    /**
     * Get asset management implementation from microservice.
     * 
     * @return
     */
    protected IAssetManagement getAssetManagement() {
	return ((DeviceStateMicroservice) getTenantEngine().getMicroservice()).getAssetManagement();
    }

    /**
     * Get configured device state merge strategy.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    protected IDeviceStateMergeStrategy<RdbDeviceState> getDeviceStateMergeStrategy() {
	return (IDeviceStateMergeStrategy<RdbDeviceState>) ((DeviceStateTenantEngine) getTenantEngine())
		.getDeviceStateMergeStrategy();
    }
}