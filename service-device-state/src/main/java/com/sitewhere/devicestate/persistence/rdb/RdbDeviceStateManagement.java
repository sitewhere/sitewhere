/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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
import com.sitewhere.devicestate.persistence.DeviceStatePersistence;
import com.sitewhere.devicestate.persistence.rdb.entity.RdbDeviceState;
import com.sitewhere.devicestate.persistence.rdb.entity.RdbRecentStateEvent;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.state.IDeviceStateManagement;
import com.sitewhere.rdb.RdbTenantComponent;
import com.sitewhere.rdb.spi.IRdbEntityManagerProvider;
import com.sitewhere.rdb.spi.IRdbQueryProvider;
import com.sitewhere.rest.model.device.state.DeviceState;
import com.sitewhere.rest.model.device.state.RecentStateEvent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.state.IRecentStateEvent;
import com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest;
import com.sitewhere.spi.device.state.request.IRecentStateEventCreateRequest;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IDeviceStateSearchCriteria;
import com.sitewhere.spi.search.device.IRecentStateEventSearchCriteria;

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
	getEntityManagerProvider().persist(created);
	return created;
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
     * @see com.sitewhere.microservice.api.state.IDeviceStateManagement#
     * createRecentStateEvent(com.sitewhere.spi.device.state.request.
     * IRecentStateEventCreateRequest)
     */
    @Override
    public RdbRecentStateEvent createRecentStateEvent(IRecentStateEventCreateRequest request)
	    throws SiteWhereException {
	RecentStateEvent state = DeviceStatePersistence.recentStateEventCreateLogic(request);
	RdbRecentStateEvent created = new RdbRecentStateEvent();
	RdbRecentStateEvent.copy(state, created);
	getEntityManagerProvider().persist(created);
	return created;
    }

    /*
     * @see com.sitewhere.microservice.api.state.IDeviceStateManagement#
     * getRecentStateEvent(java.util.UUID)
     */
    @Override
    public RdbRecentStateEvent getRecentStateEvent(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().findById(id, RdbRecentStateEvent.class);
    }

    /*
     * @see com.sitewhere.microservice.api.state.IDeviceStateManagement#
     * updateRecentStateEvent(java.util.UUID,
     * com.sitewhere.spi.device.state.request.IRecentStateEventCreateRequest)
     */
    @Override
    public RdbRecentStateEvent updateRecentStateEvent(UUID id, IRecentStateEventCreateRequest request)
	    throws SiteWhereException {
	RdbRecentStateEvent existing = getEntityManagerProvider().findById(id, RdbRecentStateEvent.class);
	if (existing != null) {
	    RecentStateEvent updates = new RecentStateEvent();

	    // Use common update logic.
	    DeviceStatePersistence.recentStateEventUpdateLogic(request, updates);
	    RdbRecentStateEvent.copy(updates, existing);
	    return getEntityManagerProvider().merge(existing);
	}
	return null;
    }

    /*
     * @see com.sitewhere.microservice.api.state.IDeviceStateManagement#
     * searchRecentStateEvents(com.sitewhere.spi.search.device.
     * IRecentStateEventSearchCriteria)
     */
    @Override
    public ISearchResults<RdbRecentStateEvent> searchRecentStateEvents(IRecentStateEventSearchCriteria criteria)
	    throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbRecentStateEvent>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbRecentStateEvent> root)
		    throws SiteWhereException {
		if (criteria.getDeviceStateId() != null) {
		    predicates.add(cb.equal(root.get("deviceStateId"), criteria.getDeviceStateId()));
		}
		if (criteria.getEventType() != null) {
		    predicates.add(cb.equal(root.get("eventType"), criteria.getEventType()));
		}
		if (criteria.getClassifier() != null) {
		    predicates.add(cb.equal(root.get("classifier"), criteria.getClassifier()));
		}
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbRecentStateEvent> addSort(CriteriaBuilder cb, Root<RdbRecentStateEvent> root,
		    CriteriaQuery<RdbRecentStateEvent> query) {
		return query.orderBy(cb.desc(root.get("createdDate")));
	    }
	}, RdbRecentStateEvent.class);
    }

    /*
     * @see com.sitewhere.microservice.api.state.IDeviceStateManagement#
     * deleteRecentStateEvent(java.util.UUID)
     */
    @Override
    public IRecentStateEvent deleteRecentStateEvent(UUID id) throws SiteWhereException {
	return getEntityManagerProvider().remove(id, RdbRecentStateEvent.class);
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
    public IDeviceManagement getDeviceManagement() {
	return ((DeviceStateMicroservice) getTenantEngine().getMicroservice()).getDeviceManagement();
    }

    /**
     * Get asset management implementation from microservice.
     * 
     * @return
     */
    public IAssetManagement getAssetManagement() {
	return ((DeviceStateMicroservice) getTenantEngine().getMicroservice()).getAssetManagement();
    }
}