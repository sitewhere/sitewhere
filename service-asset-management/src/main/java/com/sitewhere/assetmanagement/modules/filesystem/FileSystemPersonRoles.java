/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.assetmanagement.modules.filesystem;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Holds information about roles for a {@link FileSystemPersonAsset}.
 * 
 * @author Derek
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class FileSystemPersonRoles {

    /** List of roles */
    @XmlElement(name = "role")
    private List<String> roles;

    public List<String> getRoles() {
	return roles;
    }

    public void setRoles(List<String> roles) {
	this.roles = roles;
    }
}