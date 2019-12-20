/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.persistence.rdb;

import java.util.Optional;
import java.util.UUID;

import com.sitewhere.asset.persistence.AssetManagementPersistence;
import com.sitewhere.asset.persistence.rdb.entity.RdbAsset;
import com.sitewhere.asset.persistence.rdb.entity.RdbAssetType;
import com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.rdb.RdbTenantComponent;
import com.sitewhere.rdb.spi.IRdbEntityManagerProvider;
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
public class RdbAssetManagement extends RdbTenantComponent<AssetManagementRdbClient> implements IAssetManagement {

    /*
     * @see com.sitewhere.microservice.api.asset.IAssetManagement#createAsset(com.
     * sitewhere.spi.asset.request.IAssetCreateRequest)
     */
    @Override
    public IAsset createAsset(IAssetCreateRequest request) throws SiteWhereException {
	// Look up asset type.
	IAssetType assetType = getAssetTypeByToken(request.getAssetTypeToken());
	if (assetType != null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAssetTypeToken, ErrorLevel.ERROR);
	}

	// Use common logic so all backend implementations work the same.
	Asset asset = AssetManagementPersistence.assetCreateLogic(assetType, request);
	RdbAsset created = new RdbAsset();
	RdbAsset.copy(asset, created);
	created = getClient().getAssetRepository().save(created);
	return created;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#updateAsset(java.util.
     * UUID, com.sitewhere.spi.asset.request.IAssetCreateRequest)
     */
    @Override
    public IAsset updateAsset(UUID assetId, IAssetCreateRequest request) throws SiteWhereException {
	Optional<RdbAsset> opt = getClient().getAssetRepository().findById(assetId);
	if (opt.isPresent()) {
	    RdbAsset updated = opt.get();

	    IAssetType assetType = getAssetTypeByToken(request.getAssetTypeToken());
	    Asset updates = new Asset();

	    // Use common update logic.
	    AssetManagementPersistence.assetUpdateLogic(assetType, updates, request);
	    RdbAsset.copy(updates, updated);
	    return getClient().getAssetRepository().save(updated);
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
	Optional<RdbAsset> opt = getClient().getAssetRepository().findById(assetId);
	if (opt.isPresent()) {
	    return opt.get();
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#getAssetByToken(java.
     * lang.String)
     */
    @Override
    public IAsset getAssetByToken(String token) throws SiteWhereException {
	Optional<RdbAsset> opt = getClient().getAssetRepository().findByToken(token);
	if (opt.isPresent()) {
	    return opt.get();
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#deleteAsset(java.util.
     * UUID)
     */
    @Override
    public IAsset deleteAsset(UUID assetId) throws SiteWhereException {
	Optional<RdbAsset> opt = getClient().getAssetRepository().findById(assetId);
	if (opt.isPresent()) {
	    getClient().getAssetRepository().deleteById(assetId);
	}
	return opt.get();
    }

    /*
     * @see com.sitewhere.microservice.api.asset.IAssetManagement#listAssets(com.
     * sitewhere.spi.search.asset.IAssetSearchCriteria)
     */
    @Override
    public ISearchResults<IAsset> listAssets(IAssetSearchCriteria criteria) throws SiteWhereException {
	// Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
	// Specification<Asset> specification = new Specification<Asset>() {
	// @Override
	// public Predicate toPredicate(Root<Asset> root, CriteriaQuery<?> query,
	// CriteriaBuilder cb) {
	// List<Predicate> predicates = new ArrayList();
	// if (criteria.getAssetTypeToken() != null) {
	// Path path = root.get("assetTypeId");
	// predicates.add(cb.equal(path, criteria.getAssetTypeToken()));
	// }
	// return query.where(predicates.toArray(new
	// Predicate[predicates.size()])).getRestriction();
	// }
	// };
	// if (criteria.getPageSize() == 0) {
	// List<Asset> result = getClient().getAssetRepository().findAll(specification,
	// sort);
	// return new SearchResultsConverter<IAsset>().convert(result, result.size());
	// } else {
	// int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
	// Page<Asset> page = getClient().getAssetRepository().findAll(specification,
	// PageRequest.of(pageIndex, criteria.getPageSize(), sort));
	// return new SearchResultsConverter<IAsset>().convert(page.getContent(),
	// page.getTotalElements());
	// }
	return null;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#createAssetType(com.
     * sitewhere.spi.asset.request.IAssetTypeCreateRequest)
     */
    @Override
    public IAssetType createAssetType(IAssetTypeCreateRequest request) throws SiteWhereException {
	AssetType assetType = AssetManagementPersistence.assetTypeCreateLogic(request);
	RdbAssetType created = new RdbAssetType();
	RdbAssetType.copy(assetType, created);
	created = getClient().getAssetTypeRepository().save(created);
	return created;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#updateAssetType(java.
     * util.UUID, com.sitewhere.spi.asset.request.IAssetTypeCreateRequest)
     */
    @Override
    public IAssetType updateAssetType(UUID assetTypeId, IAssetTypeCreateRequest request) throws SiteWhereException {
	Optional<RdbAssetType> opt = getClient().getAssetTypeRepository().findById(assetTypeId);
	if (opt.isPresent()) {
	    RdbAssetType updated = opt.get();
	    AssetType assetType = new AssetType();
	    assetType.setId(assetTypeId);

	    // Use common asset type update logic.
	    AssetManagementPersistence.assetTypeUpdateLogic(assetType, request);
	    RdbAssetType.copy(assetType, updated);
	    updated = getClient().getAssetTypeRepository().save(updated);
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
	Optional<RdbAssetType> opt = getClient().getAssetTypeRepository().findById(assetTypeId);
	if (opt.isPresent()) {
	    return opt.get();
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#getAssetTypeByToken(
     * java.lang.String)
     */
    @Override
    public IAssetType getAssetTypeByToken(String token) throws SiteWhereException {
	Optional<RdbAssetType> opt = getClient().getAssetTypeRepository().findByToken(token);
	if (opt.isPresent()) {
	    return opt.get();
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#deleteAssetType(java.
     * util.UUID)
     */
    @Override
    public IAssetType deleteAssetType(UUID assetTypeId) throws SiteWhereException {
	Optional<RdbAssetType> opt = getClient().getAssetTypeRepository().findById(assetTypeId);
	if (opt.isPresent()) {
	    getClient().getAssetRepository().deleteById(assetTypeId);
	}
	return opt.get();
    }

    /*
     * @see
     * com.sitewhere.microservice.api.asset.IAssetManagement#listAssetTypes(com.
     * sitewhere.spi.search.asset.IAssetTypeSearchCritiera)
     */
    @Override
    public ISearchResults<IAssetType> listAssetTypes(IAssetTypeSearchCritiera criteria) throws SiteWhereException {
	// Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
	// Specification<AssetType> specification = new Specification<AssetType>() {
	// @Override
	// public Predicate toPredicate(Root<AssetType> root, CriteriaQuery<?> query,
	// CriteriaBuilder cb) {
	// return null;
	// }
	// };
	// if (criteria.getPageSize() == 0) {
	// List<AssetType> result =
	// getClient().getAssetTypeRepository().findAll(specification, sort);
	// return new SearchResultsConverter<IAssetType>().convert(result,
	// result.size());
	// } else {
	// int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
	// Page<AssetType> page =
	// getClient().getAssetTypeRepository().findAll(specification,
	// PageRequest.of(pageIndex, criteria.getPageSize(), sort));
	// return new SearchResultsConverter<IAssetType>().convert(page.getContent(),
	// page.getTotalElements());
	// }
	return null;
    }

    /*
     * @see
     * com.sitewhere.rdb.RdbTenantComponent#createRdbClient(com.sitewhere.rdb.spi.
     * IRdbEntityManagerProvider)
     */
    @Override
    public AssetManagementRdbClient createRdbClient(IRdbEntityManagerProvider provider) throws SiteWhereException {
	return new AssetManagementRdbClient();
    }

    /*
     * @see com.sitewhere.rdb.spi.IRdbTenantComponent#getEntityManagerProvider()
     */
    @Override
    public IRdbEntityManagerProvider getEntityManagerProvider() {
	return ((IAssetManagementTenantEngine) getTenantEngine()).getRdbEntityManagerProvider();
    }
}
