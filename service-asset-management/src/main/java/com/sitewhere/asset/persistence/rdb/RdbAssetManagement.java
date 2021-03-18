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
package com.sitewhere.asset.persistence.rdb;

import java.util.List;
import java.util.UUID;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.sitewhere.asset.persistence.AssetManagementPersistence;
import com.sitewhere.asset.persistence.rdb.entity.Queries;
import com.sitewhere.asset.persistence.rdb.entity.RdbAsset;
import com.sitewhere.asset.persistence.rdb.entity.RdbAssetType;
import com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.rdb.RdbTenantComponent;
import com.sitewhere.rdb.spi.IRdbEntityManagerProvider;
import com.sitewhere.rdb.spi.IRdbQueryProvider;
import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.rest.model.asset.AssetType;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;
import com.sitewhere.spi.asset.request.IAssetTypeCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.asset.IAssetSearchCriteria;
import com.sitewhere.spi.search.asset.IAssetTypeSearchCritiera;

/**
 * Implementation of {@link IAssetManagement} that stores data in RDB.
 */
public class RdbAssetManagement extends RdbTenantComponent implements IAssetManagement {

    /*
     * @see com.sitewhere.microservice.api.asset.IAssetManagement#createAsset(com.
     * sitewhere.spi.asset.request.IAssetCreateRequest)
     */
    @Override
    public IAsset createAsset(IAssetCreateRequest request) throws SiteWhereException {
	// Look up asset type.
	IAssetType assetType = getAssetTypeByToken(request.getAssetTypeToken());
	if (assetType == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAssetTypeToken, ErrorLevel.ERROR);
	}

	// Use common logic so all backend implementations work the same.
	Asset asset = AssetManagementPersistence.assetCreateLogic(assetType, request);
	RdbAsset created = new RdbAsset();
	RdbAsset.copy(asset, created);
	return getEntityManagerProvider().persist(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#updateAsset(java.util.
     * UUID, com.sitewhere.spi.asset.request.IAssetCreateRequest)
     */
    @Override
    public IAsset updateAsset(UUID assetId, IAssetCreateRequest request) throws SiteWhereException {
	RdbAsset existing = getEntityManagerProvider().findById(assetId, RdbAsset.class);
	if (existing != null) {
	    IAssetType assetType = getAssetTypeByToken(request.getAssetTypeToken());
	    Asset updates = new Asset();

	    // Use common update logic.
	    AssetManagementPersistence.assetUpdateLogic(assetType, updates, request);
	    RdbAsset.copy(updates, existing);
	    return getEntityManagerProvider().merge(existing);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#getAsset(java.util.
     * UUID)
     */
    @Override
    public IAsset getAsset(UUID assetId) throws SiteWhereException {
	return getEntityManagerProvider().findById(assetId, RdbAsset.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#getAssetByToken(java.
     * lang.String)
     */
    @Override
    public IAsset getAssetByToken(String token) throws SiteWhereException {
	Query query = getEntityManagerProvider().query(Queries.QUERY_ASSET_BY_TOKEN);
	query.setParameter("token", token);
	return getEntityManagerProvider().findOne(query, RdbAsset.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#deleteAsset(java.util.
     * UUID)
     */
    @Override
    public IAsset deleteAsset(UUID assetId) throws SiteWhereException {
	return getEntityManagerProvider().remove(assetId, RdbAsset.class);
    }

    /*
     * @see com.sitewhere.microservice.api.asset.IAssetManagement#listAssets(com.
     * sitewhere.spi.search.asset.IAssetSearchCriteria)
     */
    @Override
    public ISearchResults<RdbAsset> listAssets(IAssetSearchCriteria criteria) throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbAsset>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbAsset> root)
		    throws SiteWhereException {
		if (criteria.getAssetTypeToken() != null) {
		    IAssetType assetType = getAssetTypeByToken(criteria.getAssetTypeToken());
		    if (assetType == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidAssetTypeToken, ErrorLevel.ERROR);
		    }
		    predicates.add(cb.equal(root.get("assetTypeId"), assetType.getId()));
		}
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbAsset> addSort(CriteriaBuilder cb, Root<RdbAsset> root,
		    CriteriaQuery<RdbAsset> query) {
		return query.orderBy(cb.desc(root.get("createdDate")));
	    }
	}, RdbAsset.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#createAssetType(com.
     * sitewhere.spi.asset.request.IAssetTypeCreateRequest)
     */
    @Override
    public IAssetType createAssetType(IAssetTypeCreateRequest request) throws SiteWhereException {
	IAssetType existing = getAssetTypeByToken(request.getToken());
	if (existing != null) {
	    throw new SiteWhereException(
		    String.format("Another asset type is already using token '%s'.", request.getToken()));
	}
	
	// Use common logic so all backend implementations work the same.
	AssetType assetType = AssetManagementPersistence.assetTypeCreateLogic(request);
	RdbAssetType created = new RdbAssetType();
	RdbAssetType.copy(assetType, created);
	return getEntityManagerProvider().persist(created);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#updateAssetType(java.
     * util.UUID, com.sitewhere.spi.asset.request.IAssetTypeCreateRequest)
     */
    @Override
    public IAssetType updateAssetType(UUID assetTypeId, IAssetTypeCreateRequest request) throws SiteWhereException {
	RdbAssetType existing = getEntityManagerProvider().findById(assetTypeId, RdbAssetType.class);
	if (existing != null) {
	    AssetType updates = new AssetType();

	    // Use common update logic.
	    AssetManagementPersistence.assetTypeUpdateLogic(updates, request);
	    RdbAssetType.copy(updates, existing);
	    return getEntityManagerProvider().merge(existing);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#getAssetType(java.util.
     * UUID)
     */
    @Override
    public IAssetType getAssetType(UUID assetTypeId) throws SiteWhereException {
	return getEntityManagerProvider().findById(assetTypeId, RdbAssetType.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#getAssetTypeByToken(
     * java.lang.String)
     */
    @Override
    public IAssetType getAssetTypeByToken(String token) throws SiteWhereException {
	Query query = getEntityManagerProvider().query(Queries.QUERY_ASSET_TYPE_BY_TOKEN);
	query.setParameter("token", token);
	return getEntityManagerProvider().findOne(query, RdbAssetType.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#deleteAssetType(java.
     * util.UUID)
     */
    @Override
    public IAssetType deleteAssetType(UUID assetTypeId) throws SiteWhereException {
	return getEntityManagerProvider().remove(assetTypeId, RdbAssetType.class);
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#listAssetTypes(com.
     * sitewhere.spi.search.asset.IAssetTypeSearchCritiera)
     */
    @Override
    public ISearchResults<RdbAssetType> listAssetTypes(IAssetTypeSearchCritiera criteria) throws SiteWhereException {
	return getEntityManagerProvider().findWithCriteria(criteria, new IRdbQueryProvider<RdbAssetType>() {

	    /*
	     * @see com.sitewhere.rdb.spi.IRdbQueryProvider#addPredicates(javax.persistence.
	     * criteria.CriteriaBuilder, java.util.List, javax.persistence.criteria.Root)
	     */
	    @Override
	    public void addPredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<RdbAssetType> root)
		    throws SiteWhereException {
	    }

	    /*
	     * @see
	     * com.sitewhere.rdb.spi.IRdbQueryProvider#addSort(javax.persistence.criteria.
	     * CriteriaBuilder, javax.persistence.criteria.Root,
	     * javax.persistence.criteria.CriteriaQuery)
	     */
	    @Override
	    public CriteriaQuery<RdbAssetType> addSort(CriteriaBuilder cb, Root<RdbAssetType> root,
		    CriteriaQuery<RdbAssetType> query) {
		return query.orderBy(cb.asc(root.get("name")));
	    }
	}, RdbAssetType.class);
    }

    /*
     * @see com.sitewhere.rdb.spi.IRdbTenantComponent#getEntityManagerProvider()
     */
    @Override
    public IRdbEntityManagerProvider getEntityManagerProvider() {
	return ((IAssetManagementTenantEngine) getTenantEngine()).getRdbEntityManagerProvider();
    }
}
