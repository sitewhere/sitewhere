/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.persistence.rdb;

import com.sitewhere.asset.persistence.AssetManagementPersistence;
import com.sitewhere.rdb.RDBTenantComponent;
import com.sitewhere.rdb.multitenancy.MultiTenantContext;
import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.rest.model.asset.AssetType;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;
import com.sitewhere.spi.asset.request.IAssetTypeCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.asset.IAssetSearchCriteria;
import com.sitewhere.spi.search.asset.IAssetTypeSearchCritiera;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link IAssetManagement} that stores data in RDB.
 */
public class RDBAssetManagement  extends RDBTenantComponent<AssetManagementRDBClient> implements IAssetManagement {


    public RDBAssetManagement() {
        super(LifecycleComponentType.DataStore);
    }

    /** Injected with global SiteWhere relational database client */
    private AssetManagementRDBClient dbClient;

    public AssetManagementRDBClient getDbClient() {
        return dbClient;
    }

    public void setDbClient(AssetManagementRDBClient dbClient) {
        this.dbClient = dbClient;
    }

    @Override
    public AssetManagementRDBClient getRDBClient() throws SiteWhereException {
        String tenantId = this.getTenantEngine().getTenant().getId().toString();
        MultiTenantContext.setTenantId(tenantId);
        return dbClient;
    }

    @Override
    public void ensureIndexes() throws SiteWhereException {

    }

    @Override
    public IAsset createAsset(IAssetCreateRequest request) throws SiteWhereException {

        // Look up asset type.
        IAssetType assetType = getAssetTypeByToken(request.getAssetTypeToken());
        if (assetType == null) {
            throw new SiteWhereSystemException(ErrorCode.InvalidAreaTypeToken, ErrorLevel.ERROR);
        }

        // Use common logic so all backend implementations work the same.
        Asset asset = AssetManagementPersistence.assetCreateLogic(assetType, request);
        com.sitewhere.rdb.entities.Asset created = new com.sitewhere.rdb.entities.Asset();

        BeanUtils.copyProperties(asset, created);
        created = getRDBClient().getDbManager().getAssetRepository().save(created);
        return created;
    }

    @Override
    public IAsset updateAsset(UUID assetId, IAssetCreateRequest request) throws SiteWhereException {
        com.sitewhere.rdb.entities.Asset asset = null;
        Optional<com.sitewhere.rdb.entities.Asset> opt = getRDBClient().getDbManager().getAssetRepository().findById(assetId);
        if(opt.isPresent()) {
            asset = opt.get();

            IAssetType assetType = getAssetTypeByToken(request.getAssetTypeToken());
            Asset target = new Asset();

            // Use common update logic.
            AssetManagementPersistence.assetUpdateLogic(assetType, target, request);
            target.setId(asset.getId());
            BeanUtils.copyProperties(target, asset);

            asset = getRDBClient().getDbManager().getAssetRepository().save(asset);
        }
        return asset;
    }

    @Override
    public IAsset getAsset(UUID assetId) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.Asset> opt = getRDBClient().getDbManager().getAssetRepository().findById(assetId);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IAsset getAssetByToken(String token) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.Asset> opt = getRDBClient().getDbManager().getAssetRepository().findByToken(token);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IAsset deleteAsset(UUID assetId) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.Asset> opt = getRDBClient().getDbManager().getAssetRepository().findById(assetId);
        if(opt.isPresent()) {
            getRDBClient().getDbManager().getAssetRepository().deleteById(assetId);
        }
        return opt.get();
    }

    @Override
    public ISearchResults<IAsset> listAssets(IAssetSearchCriteria criteria) throws SiteWhereException {

        Sort sort = new Sort(Sort.Direction.DESC,"createdDate");
        Specification<com.sitewhere.rdb.entities.Asset> specification = new Specification<com.sitewhere.rdb.entities.Asset>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.Asset> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return null;
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.Asset> result = getRDBClient().getDbManager().getAssetRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.Asset> page = getRDBClient().getDbManager().getAssetRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }

    @Override
    public IAssetType createAssetType(IAssetTypeCreateRequest request) throws SiteWhereException {
        AssetType assetType = AssetManagementPersistence.assetTypeCreateLogic(request);
        com.sitewhere.rdb.entities.AssetType created = new com.sitewhere.rdb.entities.AssetType();
        BeanUtils.copyProperties(assetType, created);
        created = getRDBClient().getDbManager().getAssetTypeRepository().save(created);
        return created;
    }

    @Override
    public IAssetType updateAssetType(UUID assetTypeId, IAssetTypeCreateRequest request) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.AssetType> opt = getRDBClient().getDbManager().getAssetTypeRepository().findById(assetTypeId);
        com.sitewhere.rdb.entities.AssetType updated = null;
        if(opt.isPresent()) {
            updated = opt.get();
            AssetType assetType = new AssetType();
            assetType.setId(assetTypeId);
            AssetManagementPersistence.assetTypeUpdateLogic(assetType, request);
            BeanUtils.copyProperties(assetType, updated);
            updated = getRDBClient().getDbManager().getAssetTypeRepository().save(updated);
        }
        return updated;
    }

    @Override
    public IAssetType getAssetType(UUID assetTypeId) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.AssetType> opt = getRDBClient().getDbManager().getAssetTypeRepository().findById(assetTypeId);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IAssetType getAssetTypeByToken(String token) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.AssetType> opt = getRDBClient().getDbManager().getAssetTypeRepository().findByToken(token);
        if(opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public IAssetType deleteAssetType(UUID assetTypeId) throws SiteWhereException {
        Optional<com.sitewhere.rdb.entities.AssetType> opt = getRDBClient().getDbManager().getAssetTypeRepository().findById(assetTypeId);
        if(opt.isPresent()) {
            getRDBClient().getDbManager().getAssetRepository().deleteById(assetTypeId);
        }
        return opt.get();
    }

    @Override
    public ISearchResults<IAssetType> listAssetTypes(IAssetTypeSearchCritiera criteria) throws SiteWhereException {
        Sort sort = new Sort(Sort.Direction.DESC,"createdDate");
        Specification<com.sitewhere.rdb.entities.AssetType> specification = new Specification<com.sitewhere.rdb.entities.AssetType>() {
            @Override
            public Predicate toPredicate(Root<com.sitewhere.rdb.entities.AssetType> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return null;
            }
        };
        if (criteria.getPageSize() == 0) {
            List<com.sitewhere.rdb.entities.AssetType> result = getRDBClient().getDbManager().getAssetTypeRepository().findAll(specification, sort);
            return new SearchResultsConverter().convert(result);
        } else {
            int pageIndex = Math.max(0, criteria.getPageNumber() - 1);
            Page<com.sitewhere.rdb.entities.AssetType> page = getRDBClient().getDbManager().getAssetTypeRepository().findAll(specification, PageRequest.of(pageIndex, criteria.getPageSize(), sort));
            return new SearchResultsConverter().convert(page.getContent());
        }
    }
}
