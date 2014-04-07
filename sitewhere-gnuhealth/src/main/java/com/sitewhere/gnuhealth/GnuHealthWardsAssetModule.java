/*
 * GnuHealthWardsAssetModule.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.gnuhealth;

import java.util.List;

import org.apache.log4j.Logger;

import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.command.ICommandResponse;

/**
 * Asset module that enumerates wards from GNU Health for use as SiteWhere assets.
 * 
 * @author Derek
 */
public class GnuHealthWardsAssetModule implements IAssetModule<HardwareAsset> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(GnuHealthWardsAssetModule.class);

	/** Module id */
	private static final String MODULE_ID = "gnuhealth-wards";

	/** Module name */
	private static final String MODULE_NAME = "GNU Health Wards Asset Module";

	/** Default base URL Tryton JSON-RPC responder */
	private static final String DEFAULT_URL = "http://sitewhere-aws:8000/gnuhealth";

	/** Default Tryton username */
	private static final String DEFAULT_USERNAME = "admin";

	/** Default Tryton password */
	private static final String DEFAULT_PASSWORD = "admin";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#start()
	 */
	@Override
	public void start() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getId()
	 */
	@Override
	public String getId() {
		return MODULE_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getName()
	 */
	@Override
	public String getName() {
		return MODULE_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getAssetType()
	 */
	@Override
	public AssetType getAssetType() {
		return AssetType.Hardware;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getAssetById(java.lang.String)
	 */
	@Override
	public HardwareAsset getAssetById(String id) throws SiteWhereException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#search(java.lang.String)
	 */
	@Override
	public List<HardwareAsset> search(String criteria) throws SiteWhereException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#refresh()
	 */
	@Override
	public ICommandResponse refresh() throws SiteWhereException {
		return null;
	}
}