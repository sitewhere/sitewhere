/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.asset.filesystem;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * XML binding for a hardware asset stored on the filesystem.
 * 
 * @author Derek Adams
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class FileSystemHardwareAsset {

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String sku;

    @XmlAttribute
    private boolean device;

    @XmlElement
    private String description;

    @XmlElement(name = "image-url")
    private String imageUrl;

    @XmlElement(name = "property")
    private List<FileSystemAssetProperty> properties = new ArrayList<FileSystemAssetProperty>();

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getSku() {
	return sku;
    }

    public void setSku(String sku) {
	this.sku = sku;
    }

    public boolean isDevice() {
	return device;
    }

    public void setDevice(boolean device) {
	this.device = device;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public List<FileSystemAssetProperty> getProperties() {
	return properties;
    }

    public void setProperties(List<FileSystemAssetProperty> properties) {
	this.properties = properties;
    }
}