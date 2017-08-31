/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.assetmanagement.modules.filesystem;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * XML binding for a location asset stored on the filesystem.
 * 
 * @author Derek Adams
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class FileSystemLocationAsset {

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private double lat;

    @XmlAttribute
    private double lon;

    @XmlAttribute
    private double elevation;

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

    public double getLat() {
	return lat;
    }

    public void setLat(double lat) {
	this.lat = lat;
    }

    public double getLon() {
	return lon;
    }

    public void setLon(double lon) {
	this.lon = lon;
    }

    public double getElevation() {
	return elevation;
    }

    public void setElevation(double elevation) {
	this.elevation = elevation;
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
