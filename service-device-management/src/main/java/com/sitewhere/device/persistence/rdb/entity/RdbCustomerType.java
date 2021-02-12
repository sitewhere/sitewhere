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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sitewhere.rdb.entities.RdbBrandedEntity;
import com.sitewhere.spi.customer.ICustomerType;

@Entity
@Table(name = "customer_type", uniqueConstraints = @UniqueConstraint(columnNames = { "token" }))
@NamedQuery(name = Queries.QUERY_CUSTOMER_TYPE_BY_TOKEN, query = "SELECT c FROM RdbCustomerType c WHERE c.token = :token")
public class RdbCustomerType extends RdbBrandedEntity implements ICustomerType {

    /** Serial version UID */
    private static final long serialVersionUID = -547374923877751705L;

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

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "contained_customer_types", joinColumns = {
	    @JoinColumn(name = "parent_customer_type_id") }, inverseJoinColumns = {
		    @JoinColumn(name = "child_customer_type_id") })
    List<RdbCustomerType> containedCustomerTypes;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "containedCustomerTypes")
    private List<RdbCustomerType> containingCustomerTypes;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "customer_type_metadata", joinColumns = @JoinColumn(name = "customer_type_id"))
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

    /*
     * @see
     * com.sitewhere.rdb.entities.RdbPersistentEntity#setMetadata(java.util.Map)
     */
    @Override
    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    public List<RdbCustomerType> getContainedCustomerTypes() {
	return containedCustomerTypes;
    }

    public void setContainedCustomerTypes(List<RdbCustomerType> containedCustomerTypes) {
	this.containedCustomerTypes = containedCustomerTypes;
    }

    public List<RdbCustomerType> getContainingCustomerTypes() {
	return containingCustomerTypes;
    }

    public void setContainingCustomerTypes(List<RdbCustomerType> containingCustomerTypes) {
	this.containingCustomerTypes = containingCustomerTypes;
    }

    public static void copy(ICustomerType source, RdbCustomerType target) {
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
