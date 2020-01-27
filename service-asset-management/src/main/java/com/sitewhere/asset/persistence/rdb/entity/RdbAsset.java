/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.persistence.rdb.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.sitewhere.rdb.entities.RdbBrandedEntity;
import com.sitewhere.spi.asset.IAsset;

@Entity
@Table(name = "assets")
@NamedQuery(name = Queries.QUERY_ASSET_BY_TOKEN, query = "SELECT a FROM RdbAsset a WHERE a.token = :token")
public class RdbAsset extends RdbBrandedEntity implements IAsset {

    /** Serial version UID */
    private static final long serialVersionUID = -4935240460368316399L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    /** Asset type id */
    @Column(name = "asset_type_id")
    private UUID assetTypeId;

    /** Asset name */
    @Column(name = "name")
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "assets_metadata", joinColumns = @JoinColumn(name = "asset_id"))
    @MapKeyColumn(name = "prop_key")
    @Column(name = "prop_value")
    private Map<String, String> metadata = new HashMap<>();

    /*
     * @see com.sitewhere.spi.common.IPersistentEntity#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.asset.IAsset#getAssetTypeId()
     */
    @Override
    public UUID getAssetTypeId() {
	return assetTypeId;
    }

    public void setAssetTypeId(UUID assetTypeId) {
	this.assetTypeId = assetTypeId;
    }

    /*
     * @see com.sitewhere.spi.asset.IAsset#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.common.IMetadataProvider#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return metadata;
    }

    /*
     * @see
     * com.sitewhere.rdb.entities.RdbPersistentEntity#setMetadata(java.util.Map)
     */
    @Override
    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    public static void copy(IAsset source, RdbAsset target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getAssetTypeId() != null) {
	    target.setAssetTypeId(source.getAssetTypeId());
	}
	if (source.getName() != null) {
	    target.setName(source.getName());
	}
	RdbBrandedEntity.copy(source, target);
    }
}
