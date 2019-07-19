package com.sitewhere.rdb.entities;

import com.sitewhere.spi.area.IAreaType;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "area_type")
public class AreaType implements IAreaType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** Name */
    private String name;

    /** Description */
    private String description;

    /** List of contained area type ids */
    @ElementCollection
    private List<UUID> containedAreaTypeIds = new ArrayList<>();

    /** Background color */
    private String backgroundColor;

    /** Foreground color */
    private String foregroundColor;

    /** Border color */
    private String borderColor;

    /** Image URL */
    private String imageUrl;

    /** Icon */
    private String icon;

    /** Unique token */
    private String token;

    /** Date entity was created */
    private Date createdDate;

    /** Username for creator */
    private String createdBy;

    /** Date entity was last updated */
    private Date updatedDate;

    /** Username that updated entity */
    private String updatedBy;

    @ElementCollection
    @CollectionTable(name="area_type_metadata")
    @MapKeyColumn(name="propKey")
    @Column(name="propValue")
    private Map<String, String> metadata = new HashMap<>();

    @Override
    public List<UUID> getContainedAreaTypeIds() {
        return containedAreaTypeIds;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public String getForegroundColor() {
        return foregroundColor;
    }

    @Override
    public String getBorderColor() {
        return borderColor;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContainedAreaTypeIds(List<UUID> containedAreaTypeIds) {
        this.containedAreaTypeIds = containedAreaTypeIds;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
