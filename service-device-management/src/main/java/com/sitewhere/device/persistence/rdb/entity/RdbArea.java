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
package com.sitewhere.device.persistence.rdb.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sitewhere.rdb.entities.RdbBrandedEntity;
import com.sitewhere.spi.area.IArea;

@Entity
@Table(name = "area", uniqueConstraints = @UniqueConstraint(columnNames = { "token" }))
@NamedQueries({
	@NamedQuery(name = Queries.QUERY_AREA_BY_TOKEN, query = "SELECT a FROM RdbArea a WHERE a.token = :token"),
	@NamedQuery(name = Queries.QUERY_AREA_BY_PARENT_ID, query = "SELECT a FROM RdbArea a WHERE a.parentId = :parentId") })
public class RdbArea extends RdbBrandedEntity implements IArea {

    /** Serial version UID */
    private static final long serialVersionUID = -2015031008103232060L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "area_type_id", nullable = false)
    private UUID areaTypeId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "area_type_id", insertable = false, updatable = false)
    private RdbAreaType areaType;

    @Column(name = "parent_id", nullable = true)
    private UUID parentId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private RdbArea parent;

    /** Area name */
    @Column(name = "name")
    private String name;

    /** Area description */
    @Column(name = "description")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "area_metadata", joinColumns = @JoinColumn(name = "area_id"))
    @MapKeyColumn(name = "prop_key")
    @Column(name = "prop_value")
    private Map<String, String> metadata = new HashMap<>();

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "area", orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<RdbAreaBoundary> bounds = new ArrayList<>();

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
     * @see com.sitewhere.spi.area.IArea#getAreaTypeId()
     */
    @Override
    public UUID getAreaTypeId() {
	return areaTypeId;
    }

    public void setAreaTypeId(UUID areaTypeId) {
	this.areaTypeId = areaTypeId;
    }

    /*
     * @see com.sitewhere.spi.common.ITreeEntity#getParentId()
     */
    @Override
    public UUID getParentId() {
	return parentId;
    }

    public void setParentId(UUID parentId) {
	this.parentId = parentId;
    }

    /*
     * @see com.sitewhere.spi.common.IAccessible#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.common.IAccessible#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
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

    /*
     * @see com.sitewhere.spi.area.IBoundedEntity#getBounds()
     */
    @Override
    public List<RdbAreaBoundary> getBounds() {
	return bounds;
    }

    public void setBounds(List<RdbAreaBoundary> bounds) {
	this.bounds = bounds;
    }

    public RdbAreaType getAreaType() {
	return areaType;
    }

    public void setAreaType(RdbAreaType areaType) {
	this.areaType = areaType;
    }

    public RdbArea getParent() {
	return parent;
    }

    public void setParent(RdbArea parent) {
	this.parent = parent;
    }

    public static void copy(IArea source, RdbArea target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getAreaTypeId() != null) {
	    target.setAreaTypeId(source.getAreaTypeId());
	}
	if (source.getParentId() != null) {
	    target.setParentId(source.getParentId());
	}
	if (source.getName() != null) {
	    target.setName(source.getName());
	}
	if (source.getDescription() != null) {
	    target.setDescription(source.getDescription());
	}
	if (source.getMetadata() != null) {
	    target.setMetadata(source.getMetadata());
	}
	RdbBrandedEntity.copy(source, target);
    }
}
