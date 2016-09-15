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

import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.resource.IResource;

/**
 * Module that loads a list of device assets from an XML file on the filesystem.
 * 
 * @author Derek
 */
public class FileSystemDeviceAssetModule extends FileSystemAssetModule<HardwareAsset>
	implements IAssetModule<HardwareAsset> {

    /** Serial version UID */
    private static final long serialVersionUID = -4973584728643353788L;

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Module id */
    public static final String MODULE_ID = "fs-devices";

    /** Module name */
    public static final String MODULE_NAME = "Default Device Management";

    /** Filename in SiteWhere config folder that contains device assets */
    public static final String DEVICE_CONFIG_FILENAME = "device-assets.xml";

    public FileSystemDeviceAssetModule() {
	super(DEVICE_CONFIG_FILENAME, MODULE_ID, MODULE_NAME);
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
	return AssetType.Device;
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