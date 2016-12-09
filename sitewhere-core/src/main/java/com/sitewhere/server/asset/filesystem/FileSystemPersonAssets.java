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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Object used to marshal an XML file on the filesystem so it can be used to
 * define a list of person assets.
 * 
 * @author Derek Adams
 */
@XmlRootElement(name = "person-assets")
@XmlAccessorType(XmlAccessType.FIELD)
public class FileSystemPersonAssets {

    @XmlElement(name = "person-asset")
    private List<FileSystemPersonAsset> personAssets = new ArrayList<FileSystemPersonAsset>();

    public List<FileSystemPersonAsset> getPersonAssets() {
	return personAssets;
    }

    public void setPersonAssets(List<FileSystemPersonAsset> personAssets) {
	this.personAssets = personAssets;
    }
}