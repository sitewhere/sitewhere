/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.asset.filesystem;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetModule;

/**
 * Module that loads a list of person assets from an XML file on the filesystem.
 * 
 * @author Derek Adams
 */
public class FileSystemPersonAssetModule extends FileSystemAssetModule<PersonAsset> implements
		IAssetModule<PersonAsset> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(FileSystemPersonAssetModule.class);

	/** Module id */
	public static final String MODULE_ID = "fs-persons";

	/** Module name */
	public static final String MODULE_NAME = "Default Identity Management";

	/** Filename in SiteWhere config folder that contains person assets */
	public static final String PERSON_CONFIG_FILENAME = "person-assets.xml";

	public FileSystemPersonAssetModule() {
		super(PERSON_CONFIG_FILENAME, MODULE_ID, MODULE_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getAssetType()
	 */
	public AssetType getAssetType() {
		return AssetType.Person;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.asset.filesystem.FileSystemAssetModule#unmarshal(java.io.File)
	 */
	@Override
	protected List<PersonAsset> unmarshal(File file) throws SiteWhereException {
		return MarshalUtils.loadPersonAssets(file);
	}
}