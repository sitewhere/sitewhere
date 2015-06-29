/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
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
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Handles operations for loading and caching GNU Health data.
 * 
 * @author Derek
 */
public class GnuHealthData extends LifecycleComponent {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(GnuHealthData.class);

	/** Default image associated with wards */
	private static final String BUILDING_IMAGE_URL =
			"https://s3.amazonaws.com/sitewhere-demo/healthcare/building.png";

	/** Default image associated with wards */
	private static final String WARD_IMAGE_URL =
			"https://s3.amazonaws.com/sitewhere-demo/healthcare/ward.jpg";

	/** Configuration settings */
	private GnuHealthConfiguration configuration;

	/** Use CXF web client to send requests */
	private WebClient client;

	/** Jackson JSON mapper */
	private ObjectMapper mapper = new ObjectMapper();

	/** Cached map of ward assets */
	private Map<String, LocationAsset> buildingCache = new HashMap<String, LocationAsset>();

	/** Cached map of ward assets */
	private Map<String, LocationAsset> wardCache = new HashMap<String, LocationAsset>();

	public GnuHealthData(GnuHealthConfiguration configuration) {
		super(LifecycleComponentType.DataStore);
		this.configuration = configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
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
		refresh();
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
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
	}

	/**
	 * Refresh all data.
	 * 
	 * @throws SiteWhereException
	 */
	public void refresh() throws SiteWhereException {
		refreshBuildings();
		refreshWards();
	}

	/**
	 * Get the cached list of buildings.
	 * 
	 * @return
	 */
	public Map<String, LocationAsset> getCachedBuildings() {
		return buildingCache;
	}

	/**
	 * Cache building asset information from the GNU Health server.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public ICommandResponse refreshBuildings() throws SiteWhereException {
		long startTime = System.currentTimeMillis();

		buildingCache.clear();
		JsonNode results = getBuildingsJson();
		Iterator<JsonNode> it = results.elements();
		while (it.hasNext()) {
			JsonNode node = it.next();
			LocationAsset asset = new LocationAsset();
			asset.setId(node.get("id").asText());
			asset.setName(node.get("rec_name").asText());
			asset.setImageUrl(BUILDING_IMAGE_URL);
			Iterator<String> fieldNames = node.fieldNames();
			while (fieldNames.hasNext()) {
				String name = fieldNames.next();
				JsonNode value = node.get(name);
				if ((!value.isObject()) && (!value.isNull())) {
					asset.getProperties().put(name, value.asText());
				}
			}
			buildingCache.put(asset.getId(), asset);
		}

		long totalTime = System.currentTimeMillis() - startTime;
		String message =
				"Cached " + buildingCache.size() + " GNU Health building assets in " + totalTime + "ms.";
		LOGGER.info(message);
		return new CommandResponse(CommandResult.Successful, message);
	}

	/**
	 * Get the JSON response for all buildings.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	protected JsonNode getBuildingsJson() throws SiteWhereException {
		ObjectNode context = JsonNodeFactory.instance.objectNode();
		JsonCall search =
				new JsonCall("model.gnuhealth.hospital.building.search_read", 2, new Object[] {
						getConfiguration().getUserId(),
						getConfiguration().getCookie(),
						new Object[] {},
						context });
		Response response = client.invoke(GnuHealthConfiguration.METHOD_POST, search);
		JsonNode json = null;
		try {
			json = mapper.readTree((InputStream) response.getEntity());
			LOGGER.info("Response from building search was: " + json.toString());
		} catch (JsonParseException e) {
			throw new SiteWhereException("Unable to parse building search response.", e);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to read building search response.", e);
		}
		return json.get("result");
	}

	/**
	 * Get the cached list of wards.
	 * 
	 * @return
	 */
	public Map<String, LocationAsset> getCachedWards() {
		return wardCache;
	}

	/**
	 * Cache ward asset information from the GNU Health server.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public ICommandResponse refreshWards() throws SiteWhereException {
		long startTime = System.currentTimeMillis();

		wardCache.clear();
		JsonNode results = getWardsJson();
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
					asset.getProperties().put(name, value.asText());
				}
			}
			wardCache.put(asset.getId(), asset);
		}

		long totalTime = System.currentTimeMillis() - startTime;
		String message = "Cached " + wardCache.size() + " GNU Health ward assets in " + totalTime + "ms.";
		LOGGER.info(message);
		return new CommandResponse(CommandResult.Successful, message);
	}

	/**
	 * Get the JSON response for all wards.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	protected JsonNode getWardsJson() throws SiteWhereException {
		ObjectNode context = JsonNodeFactory.instance.objectNode();
		JsonCall search =
				new JsonCall("model.gnuhealth.hospital.ward.search_read", 2, new Object[] {
						getConfiguration().getUserId(),
						getConfiguration().getCookie(),
						new Object[] {},
						context });
		Response response = client.invoke(GnuHealthConfiguration.METHOD_POST, search);
		JsonNode json = null;
		try {
			json = mapper.readTree((InputStream) response.getEntity());
			LOGGER.info("Response from ward search was: " + json.toString());
		} catch (JsonParseException e) {
			throw new SiteWhereException("Unable to parse ward search response.", e);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to read ward search response.", e);
		}
		return json.get("result");
	}

	public GnuHealthConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(GnuHealthConfiguration configuration) {
		this.configuration = configuration;
	}
}