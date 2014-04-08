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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sitewhere.gnuhealth.GnuHealthConfiguration.JsonCall;
import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.server.asset.AssetMatcher;
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
public class GnuHealthWardsAssetModule implements IAssetModule<LocationAsset> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(GnuHealthWardsAssetModule.class);

	/** Module id */
	private static final String MODULE_ID = "gnuhealth-wards";

	/** Module name */
	private static final String MODULE_NAME = "GNU Health Wards";

	/** Default image associated with wards */
	private static final String WARD_IMAGE_URL =
			"https://s3.amazonaws.com/sitewhere-demo/healthcare/ward.jpg";

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

	/** Used to find search results */
	private AssetMatcher matcher = new AssetMatcher();

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
		cacheAssetData();
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

		JsonNode ids = getWardRecordIds();
		JsonNode results = getWardRecords(ids);

		long startTime = System.currentTimeMillis();

		Iterator<JsonNode> it = results.elements();
		while (it.hasNext()) {
			JsonNode node = it.next();
			LocationAsset asset = new LocationAsset();
			asset.setId(node.get("id").asText());
			asset.setName(node.get("rec_name").asText());
			asset.setImageUrl(WARD_IMAGE_URL);
			Iterator<String> fieldNames = node.fieldNames();
			while (fieldNames.hasNext()) {
				String name = fieldNames.next();
				JsonNode value = node.get(name);
				if ((!value.isObject()) && (!value.isNull())) {
					asset.setProperty(name, value.asText());
				}
			}
			assetCache.put(asset.getId(), asset);
		}

		long totalTime = System.currentTimeMillis() - startTime;
		String message = "Cached " + assetCache.size() + " assets in " + totalTime + "ms.";
		LOGGER.info(message);
		return new CommandResponse(CommandResult.Successful, message);
	}

	/**
	 * Get a list of available record ids by doing a search.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	protected JsonNode getWardRecordIds() throws SiteWhereException {
		ObjectNode context = JsonNodeFactory.instance.objectNode();
		JsonCall login =
				new JsonCall("model.gnuhealth.hospital.ward.search", 2, new Object[] {
						getConfiguration().getUserId(),
						getConfiguration().getCookie(),
						new Object[] {},
						context });
		Response response = client.invoke(GnuHealthConfiguration.METHOD_POST, login);
		JsonNode json = null;
		try {
			json = mapper.readTree((InputStream) response.getEntity());
			LOGGER.debug("Response from ward search was: " + json.toString());
		} catch (JsonParseException e) {
			throw new SiteWhereException("Unable to parse login response.", e);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to read login response.", e);
		}
		return json.get("result");
	}

	/**
	 * Get the records matched in the previous search.
	 * 
	 * @param ids
	 * @return
	 * @throws SiteWhereException
	 */
	protected JsonNode getWardRecords(JsonNode ids) throws SiteWhereException {
		ObjectNode context = JsonNodeFactory.instance.objectNode();
		JsonCall login =
				new JsonCall("model.gnuhealth.hospital.ward.read", 3, new Object[] {
						getConfiguration().getUserId(),
						getConfiguration().getCookie(),
						ids,
						context });
		Response response = client.invoke(GnuHealthConfiguration.METHOD_POST, login);
		JsonNode json = null;
		try {
			json = mapper.readTree((InputStream) response.getEntity());
			LOGGER.info("Response from ward read was: " + json.toString());
		} catch (JsonParseException e) {
			throw new SiteWhereException("Unable to parse login response.", e);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to read login response.", e);
		}
		return json.get("result");
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
	public LocationAsset getAssetById(String id) throws SiteWhereException {
		return assetCache.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#search(java.lang.String)
	 */
	@Override
	public List<LocationAsset> search(String criteria) throws SiteWhereException {
		criteria = criteria.toLowerCase();
		List<LocationAsset> results = new ArrayList<LocationAsset>();
		if (criteria.length() == 0) {
			results.addAll(assetCache.values());
			return results;
		}
		for (LocationAsset asset : assetCache.values()) {
			if (matcher.isLocationMatch(asset, criteria)) {
				results.add(asset);
			}
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#refresh()
	 */
	@Override
	public ICommandResponse refresh() throws SiteWhereException {
		return cacheAssetData();
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