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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.server.asset.scim.IScimFields;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.command.CommandResult;
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
	private static final String MODULE_NAME = "GNU Health Wards";

	/** Unique module id */
	private String moduleId = MODULE_ID;

	/** Module name */
	private String moduleName = MODULE_NAME;

	/** Common GNU Health configuration */
	private GnuHealthConfiguration configuration;

	/** Use CXF web client to send requests */
	private WebClient client;

	/** Jackson JSON mapper */
	private ObjectMapper mapper = new ObjectMapper();

	/** Cached asset map */
	private Map<String, LocationAsset> assetCache = new HashMap<String, LocationAsset>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Connecting to GNU Health instance at: " + getConfiguration().getBaseUrl());
		List<Object> providers = new ArrayList<Object>();
		providers.add(new JacksonJsonProvider());
		this.client =
				WebClient.create(getConfiguration().getBaseUrl(), providers).type(MediaType.APPLICATION_JSON).accept(
						MediaType.APPLICATION_JSON);
		getConfiguration().login(client);
		// cacheAssetData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
	}

	/**
	 * Cache asset information from the GNU Health server.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	protected ICommandResponse cacheAssetData() throws SiteWhereException {
		assetCache.clear();

		LOGGER.info("Caching search data.");
		int totalAssets = 0;
		long startTime = System.currentTimeMillis();
		Response response = client.get();
		Object entity = response.getEntity();
		try {
			JsonNode json = mapper.readTree((InputStream) entity);
			JsonNode resources = json.get(IScimFields.RESOURCES);
			if (resources == null) {
				String message = "SCIM JSON response did not contain a 'resources' section.";
				LOGGER.info(message);
				return new CommandResponse(CommandResult.Failed, message);
			}
			Iterator<JsonNode> it = resources.elements();
			while (it.hasNext()) {
				JsonNode resource = it.next();
				LocationAsset asset = parse(resource);
				assetCache.put(asset.getId(), asset);
				totalAssets++;
			}
		} catch (JsonParseException e) {
			throw new SiteWhereException("Unable to parse asset response.", e);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to read asset response.", e);
		}
		long totalTime = System.currentTimeMillis() - startTime;
		String message = "Cached " + totalAssets + " assets in " + totalTime + "ms.";
		LOGGER.info(message);
		return new CommandResponse(CommandResult.Successful, message);
	}

	/**
	 * Parse JSON response into an asset record.
	 * 
	 * @param resource
	 * @return
	 * @throws SiteWhereException
	 */
	protected LocationAsset parse(JsonNode resource) throws SiteWhereException {
		LocationAsset asset = new LocationAsset();
		return asset;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getId()
	 */
	@Override
	public String getId() {
		return getModuleId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getName()
	 */
	@Override
	public String getName() {
		return getModuleName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getAssetType()
	 */
	@Override
	public AssetType getAssetType() {
		return AssetType.Location;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public GnuHealthConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(GnuHealthConfiguration configuration) {
		this.configuration = configuration;
	}
}