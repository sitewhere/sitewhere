/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.modules.filesystem;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.resource.IResource;

/**
 * Module that loads a list of hardware assets from an XML file on the
 * filesystem.
 * 
 * @author Derek Adams
 */
public class FileSystemHardwareAssetModule extends FileSystemAssetModule<HardwareAsset>
	implements IAssetModule<HardwareAsset> {

    /** Serial version UID */
    private static final long serialVersionUID = -8641817304295833195L;

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Module id */
    public static final String MODULE_ID = "fs-hardware";

    /** Module name */
    public static final String MODULE_NAME = "Default Hardware Management";

    /** Filename in SiteWhere config folder that contains hardware assets */
    public static final String HARDWARE_CONFIG_FILENAME = "hardware-assets.xml";

    public FileSystemHardwareAssetModule() {
	super(HARDWARE_CONFIG_FILENAME, MODULE_ID, MODULE_NAME);
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
     * @see
     * com.sitewhere.spi.asset.IAssetModule#isAssetTypeSupported(com.sitewhere.
     * spi.asset .AssetType)
     */
    public AssetType getAssetType() {
	return AssetType.Hardware;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.asset.filesystem.FileSystemAssetModule#unmarshal(com
     * .sitewhere.spi.resource.IResource)
     */
    @Override
    protected List<HardwareAsset> unmarshal(IResource resource) throws SiteWhereException {
	return MarshalUtils.loadHardwareAssets(new ByteArrayInputStream(resource.getContent()), getAssetType());
    }
}