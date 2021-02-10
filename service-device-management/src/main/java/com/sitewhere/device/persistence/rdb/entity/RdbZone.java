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
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.sitewhere.rdb.entities.RdbBrandedEntity;
import com.sitewhere.rdb.entities.RdbPersistentEntity;
import com.sitewhere.spi.area.IZone;

@Entity
@Table(name = "zone", uniqueConstraints = @UniqueConstraint(columnNames = { "token" }))
@NamedQuery(name = Queries.QUERY_ZONE_BY_TOKEN, query = "SELECT z FROM RdbZone z WHERE z.token = :token")
public class RdbZone extends RdbPersistentEntity implements IZone {

    /** Serial version UID */
    private static final long serialVersionUID = -9147424055414475390L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    /** Id for associated area */
    @Column(name = "area_id")
    private UUID areaId;

    /** Displayed name */
    @Column(name = "name")
    private String name;

    /** Zone bounds */
    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "zone", orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<RdbZoneBoundary> bounds = new ArrayList<>();

    /** Border color */
    @Column(name = "border_color")
    private String borderColor;

    /** Fill color */
    @Column(name = "fill_color")
    private String fillColor;

    /** Opacity */
    @Column(name = "border_opacity")
    private Double borderOpacity;

    /** Opacity */
    @Column(name = "fill_opacity")
    private Double fillOpacity;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "zone_metadata", joinColumns = @JoinColumn(name = "zone_id"))
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
     * @see com.sitewhere.spi.area.IZone#getAreaId()
     */
    @Override
    public UUID getAreaId() {
	return areaId;
    }

    public void setAreaId(UUID areaId) {
	this.areaId = areaId;
    }

    /*
     * @see com.sitewhere.spi.area.IZone#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.area.IBoundedEntity#getBounds()
     */
    @Override
    public List<RdbZoneBoundary> getBounds() {
	return bounds;
    }

    public void setBounds(List<RdbZoneBoundary> bounds) {
	this.bounds = bounds;
    }

    /*
     * @see com.sitewhere.spi.area.IZone#getBorderColor()
     */
    @Override
    public String getBorderColor() {
	return borderColor;
    }

    public void setBorderColor(String borderColor) {
	this.borderColor = borderColor;
    }

    /*
     * @see com.sitewhere.spi.area.IZone#getFillColor()
     */
    @Override
    public String getFillColor() {
	return fillColor;
    }

    public void setFillColor(String fillColor) {
	this.fillColor = fillColor;
    }

    /*
     * @see com.sitewhere.spi.area.IZone#getBorderOpacity()
     */
    @Override
    public Double getBorderOpacity() {
	return borderOpacity;
    }

    public void setBorderOpacity(Double borderOpacity) {
	this.borderOpacity = borderOpacity;
    }

    /*
     * @see com.sitewhere.spi.area.IZone#getFillOpacity()
     */
    @Override
    public Double getFillOpacity() {
	return fillOpacity;
    }

    public void setFillOpacity(Double fillOpacity) {
	this.fillOpacity = fillOpacity;
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

    public static void copy(IZone source, RdbZone target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getAreaId() != null) {
	    target.setAreaId(source.getAreaId());
	}
	if (source.getName() != null) {
	    target.setName(source.getName());
	}
	if (source.getBorderColor() != null) {
	    target.setBorderColor(source.getBorderColor());
	}
	if (source.getFillColor() != null) {
	    target.setFillColor(source.getFillColor());
	}
	if (source.getFillOpacity() != null) {
	    target.setFillOpacity(source.getFillOpacity());
	}
	if (source.getMetadata() != null) {
	    target.setMetadata(source.getMetadata());
	}
	RdbBrandedEntity.copy(source, target);
    }
}
