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
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sitewhere.rdb.entities.RdbBrandedEntity;
import com.sitewhere.spi.customer.ICustomer;

@Entity
@Table(name = "customer", uniqueConstraints = @UniqueConstraint(columnNames = { "token" }))
@NamedQueries({
	@NamedQuery(name = Queries.QUERY_CUSTOMER_BY_TOKEN, query = "SELECT c FROM RdbCustomer c WHERE c.token = :token"),
	@NamedQuery(name = Queries.QUERY_CUSTOMER_BY_PARENT_ID, query = "SELECT c FROM RdbCustomer c WHERE c.parentId = :parentId") })
public class RdbCustomer extends RdbBrandedEntity implements ICustomer {

    /** Serial version UID */
    private static final long serialVersionUID = 5603208951111630899L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "customer_type_id", nullable = false)
    private UUID customerTypeId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_type_id", insertable = false, updatable = false)
    private RdbCustomerType customerType;

    @Column(name = "parent_id", nullable = true)
    private UUID parentId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private RdbCustomer parent;

    /** Area name */
    @Column(name = "name")
    private String name;

    /** Area description */
    @Column(name = "description")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "customer_metadata", joinColumns = @JoinColumn(name = "customer_type_id"))
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
     * @see com.sitewhere.spi.customer.ICustomer#getCustomerTypeId()
     */
    @Override
    public UUID getCustomerTypeId() {
	return customerTypeId;
    }

    public void setCustomerTypeId(UUID customerTypeId) {
	this.customerTypeId = customerTypeId;
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

    public RdbCustomerType getCustomerType() {
	return customerType;
    }

    public void setCustomerType(RdbCustomerType customerType) {
	this.customerType = customerType;
    }

    public RdbCustomer getParent() {
	return parent;
    }

    public void setParent(RdbCustomer parent) {
	this.parent = parent;
    }

    public static void copy(ICustomer source, RdbCustomer target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getCustomerTypeId() != null) {
	    target.setCustomerTypeId(source.getCustomerTypeId());
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
