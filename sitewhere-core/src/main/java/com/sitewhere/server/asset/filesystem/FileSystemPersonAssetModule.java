/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.asset.filesystem;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.resource.IResource;

/**
 * Module that loads a list of person assets from an XML file on the filesystem.
 * 
 * @author Derek Adams
 */
public class FileSystemPersonAssetModule extends FileSystemAssetModule<PersonAsset>
		implements IAssetModule<PersonAsset> {

	/** Serial version UID */
	private static final long serialVersionUID = -8518071972348096650L;

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

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
	 * com.sitewhere.server.asset.filesystem.FileSystemAssetModule#unmarshal(com
	 * .sitewhere.spi.resource.IResource)
	 */
	@Override
	protected List<PersonAsset> unmarshal(IResource resource) throws SiteWhereException {
		return MarshalUtils.loadPersonAssets(new ByteArrayInputStream(resource.getContent()));
	}
}