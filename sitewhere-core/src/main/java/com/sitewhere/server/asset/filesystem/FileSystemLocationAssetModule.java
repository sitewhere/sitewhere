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

import org.apache.log4j.Logger;

import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.resource.IResource;

/**
 * Module that loads a list of location assets from an XML file on the
 * filesystem.
 * 
 * @author Derek Adams
 */
public class FileSystemLocationAssetModule extends FileSystemAssetModule<LocationAsset>
		implements IAssetModule<LocationAsset> {

	/** Serial version UID */
	private static final long serialVersionUID = 2385514171301244967L;

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(FileSystemLocationAssetModule.class);

	/** Module id */
	public static final String MODULE_ID = "fs-locations";

	/** Module name */
	public static final String MODULE_NAME = "Default Location Management";

	/** Filename in SiteWhere config folder that contains location assets */
	public static final String CONFIG_FILENAME = "location-assets.xml";

	public FileSystemLocationAssetModule() {
		super(CONFIG_FILENAME, MODULE_ID, MODULE_NAME);
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
		return AssetType.Location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.asset.filesystem.FileSystemAssetModule#unmarshal(com
	 * .sitewhere.spi.resource.IResource)
	 */
	@Override
	protected List<LocationAsset> unmarshal(IResource resource) throws SiteWhereException {
		return MarshalUtils.loadLocationAssets(new ByteArrayInputStream(resource.getContent()));
	}
}
