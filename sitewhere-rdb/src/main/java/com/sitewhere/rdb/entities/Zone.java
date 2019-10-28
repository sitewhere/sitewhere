/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.entities;

import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.common.ILocation;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "zone")
public class Zone implements IZone {

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
    @OneToMany(cascade= {CascadeType.ALL},fetch=FetchType.EAGER, mappedBy = "zone")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Location> bounds = new ArrayList<Location>();

    /** Border color */
    @Column(name = "border_color")
    private String borderColor;

    /** Fill color */
    @Column(name = "fill_color")
    private String fillColor;

    /** Opacity */
    @Column(name = "opacity")
    private Double opacity;

    /** Token */
    @Column(name = "token")
    private String token;

    /** Date entity was created */
    @Column(name = "created_date")
    private Date createdDate;

    /** Username for creator */
    @Column(name = "created_by")
    private String createdBy;

    /** Date entity was last updated */
    @Column(name = "updated_date")
    private Date updatedDate;

    /** Username that updated entity */
    @Column(name = "updated_by")
    private String updatedBy;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name="zone_metadata")
    @MapKeyColumn(name="prop_key")
    @Column(name="prop_value")
    private Map<String, String> metadata = new HashMap<>();

    @Override
    public UUID getAreaId() {
        return areaId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getBorderColor() {
        return borderColor;
    }

    @Override
    public Double getBorderOpacity() {
        return null;
    }

    @Override
    public String getFillColor() {
        return fillColor;
    }

    @Override
    public Double getFillOpacity() {
        return null;
    }

    public Double getOpacity() {
        return opacity;
    }

    @Override
    public List<? extends ILocation> getBounds() {
        return bounds;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public Date getCreatedDate() {
        return createdDate;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public Date getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setAreaId(UUID areaId) {
        this.areaId = areaId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBounds(List<Location> bounds) {
        this.bounds = bounds;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public void setOpacity(Double opacity) {
        this.opacity = opacity;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

}
