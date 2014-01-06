/*
 * FileSystemPersonAsset.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
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
public class FileSystemPersonAsset {

	@XmlAttribute
	private String id;

	@XmlAttribute
	private String userName;

	@XmlAttribute
	private String name;

	@XmlAttribute
	private String emailAddress;

	@XmlElement(name = "roles")
	private FileSystemPersonRoles roles;

	@XmlElement(name = "photo-url")
	private String photoUrl;

	@XmlElement(name = "property")
	private List<FileSystemAssetProperty> properties = new ArrayList<FileSystemAssetProperty>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public FileSystemPersonRoles getRoles() {
		return roles;
	}

	public void setRoles(FileSystemPersonRoles roles) {
		this.roles = roles;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public List<FileSystemAssetProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<FileSystemAssetProperty> properties) {
		this.properties = properties;
	}
}