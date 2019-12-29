/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.rdb.entity;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.sitewhere.rdb.entities.RdbBrandedEntity;
import com.sitewhere.spi.area.IAreaType;

@Entity
@Table(name = "area_type")
@NamedQuery(name = Queries.QUERY_AREA_TYPE_BY_TOKEN, query = "SELECT a FROM RdbAreaType a WHERE a.token = :token")
public class RdbAreaType extends RdbBrandedEntity implements IAreaType {

    /** Serial version UID */
    private static final long serialVersionUID = -6738406465092817226L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    /** Name */
    @Column(name = "name")
    private String name;

    /** Description */
    @Column(name = "description")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "contained_area_types", joinColumns = {
	    @JoinColumn(name = "parent_area_type_id") }, inverseJoinColumns = {
		    @JoinColumn(name = "child_area_type_id") })
    List<RdbAreaType> containedAreaTypes;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "containedAreaTypes")
    private List<RdbAreaType> containingAreaTypes;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "area_type_metadata", joinColumns = @JoinColumn(name = "area_type_id"))
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

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    public List<RdbAreaType> getContainedAreaTypes() {
	return containedAreaTypes;
    }

    public void setContainedAreaTypes(List<RdbAreaType> containedAreaTypes) {
	this.containedAreaTypes = containedAreaTypes;
    }

    public List<RdbAreaType> getContainingAreaTypes() {
	return containingAreaTypes;
    }

    public void setContainingAreaTypes(List<RdbAreaType> containingAreaTypes) {
	this.containingAreaTypes = containingAreaTypes;
    }

    public static void copy(IAreaType source, RdbAreaType target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
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
