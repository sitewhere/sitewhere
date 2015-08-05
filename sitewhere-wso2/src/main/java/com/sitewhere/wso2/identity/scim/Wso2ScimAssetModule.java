/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.wso2.identity.scim;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.server.asset.AssetMatcher;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Asset module that interacts with an external WSO2 Identity Server via SCIM.
 * 
 * @author dadams
 */
public class Wso2ScimAssetModule extends LifecycleComponent implements IAssetModule<PersonAsset> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(Wso2ScimAssetModule.class);

	/** Module id */
	private static final String DEFAULT_MODULE_ID = "wso2scim";

	/** Module name */
	private static final String MODULE_NAME = "WSO2 Identity Management";

	/** Default base url for user operations */
	private static final String DEFAULT_URL = "https://localhost:9443/wso2/scim/Users";

	/** Default username for basic auth */
	private static final String DEFAULT_AUTH_USERNAME = "admin";

	/** Default password for basic auth */
	private static final String DEFAULT_AUTH_PASSWORD = "admin";

	/** Module id */
	private String moduleId = DEFAULT_MODULE_ID;

	/** URL used to access user info */
	private String scimUsersUrl = DEFAULT_URL;

	/** Basic auth username */
	private String username = DEFAULT_AUTH_USERNAME;

	/** Basic auth password */
	private String password = DEFAULT_AUTH_PASSWORD;

	/** Indicates whether to ignore a bad SSL certificate on the server */
	private boolean ignoreBadCertificate = false;

	/** Use CXF web client to send requests */
	private WebClient client;

	/** Jackson JSON factory */
	private ObjectMapper mapper = new ObjectMapper();

	/** Cached asset map */
	private Map<String, PersonAsset> assetCache = new HashMap<String, PersonAsset>();

	/** Matcher used for searches */
	protected AssetMatcher matcher = new AssetMatcher();

	public Wso2ScimAssetModule() {
		super(LifecycleComponentType.AssetModule);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	public void start() throws SiteWhereException {
		LOGGER.info("Connecting to WSO2 Identity Server instance at: " + getScimUsersUrl());
		this.client = WebClient.create(getScimUsersUrl(), getUsername(), getPassword(), null);
		cacheAssetData();
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
	public void stop() throws SiteWhereException {
		this.client = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getId()
	 */
	public String getId() {
		return getModuleId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getName()
	 */
	public String getName() {
		return MODULE_NAME;
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
	 * @see com.sitewhere.spi.asset.IAssetModule#getAssetById(java.lang.String)
	 */
	public PersonAsset getAssetById(String id) throws SiteWhereException {
		return assetCache.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#search(java.lang.String)
	 */
	public List<PersonAsset> search(String criteria) throws SiteWhereException {
		criteria = criteria.toLowerCase();
		List<PersonAsset> results = new ArrayList<PersonAsset>();
		if (criteria.length() == 0) {
			results.addAll(assetCache.values());
			return results;
		}
		for (PersonAsset asset : assetCache.values()) {
			if (matcher.isPersonMatch(asset, criteria)) {
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
	public ICommandResponse refresh() throws SiteWhereException {
		try {
			return cacheAssetData();
		} catch (SiteWhereException e) {
			return new CommandResponse(CommandResult.Failed, e.getMessage());
		}
	}

	/**
	 * Simplifies comparing possibly null non-case sensitive values.
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	protected boolean contains(String field, String value) {
		if (field == null) {
			return false;
		}
		return field.trim().toLowerCase().indexOf(value) != -1;
	}

	/**
	 * Make remote call to list all user assets, then parse and cache them.
	 * 
	 * @throws SiteWhereException
	 */
	protected ICommandResponse cacheAssetData() throws SiteWhereException {
		assetCache.clear();

		LOGGER.info("Caching search data.");
		int totalAssets = 0;
		long startTime = System.currentTimeMillis();
		WebClient caller = WebClient.fromClient(client);
		if (isIgnoreBadCertificate()) {
			doIgnoreBadCertificate(caller);
		}
		caller.accept(MediaType.APPLICATION_JSON_TYPE);
		Response response = caller.get();
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
				PersonAsset asset = parse(resource);
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
	 * Parse the JSON branch that holds a SCIM resource.
	 * 
	 * @param resource
	 * @return
	 */
	protected PersonAsset parse(JsonNode resource) throws SiteWhereException {
		PersonAsset asset = new PersonAsset();
		JsonNode id = resource.get(IScimFields.ID);
		if (id != null) {
			asset.getProperties().put(IWso2ScimFields.PROP_ASSET_ID, id.textValue());
		}

		JsonNode username = resource.get(IScimFields.USERNAME);
		if (username != null) {
			asset.getProperties().put(IWso2ScimFields.PROP_USERNAME, username.textValue());
			asset.setUserName(username.textValue());
			asset.setId(username.textValue());
		}

		JsonNode profileUrl = resource.get(IScimFields.PROFILE_URL);
		if (profileUrl != null) {
			asset.getProperties().put(IWso2ScimFields.PROP_PROFILE_URL, profileUrl.textValue());
			asset.setImageUrl(profileUrl.textValue());
		}

		parseName(resource, asset);
		parseEmail(resource, asset);

		return asset;
	}

	/**
	 * Parse name fields.
	 * 
	 * @param resource
	 * @param asset
	 */
	protected void parseName(JsonNode resource, PersonAsset asset) {
		JsonNode name = resource.get(IScimFields.NAME);
		if (name != null) {
			String full = "";
			JsonNode given = name.get(IScimFields.GIVEN_NAME);
			if (given != null) {
				String givenValue = given.textValue();
				full += givenValue + " ";
				asset.getProperties().put(IScimFields.GIVEN_NAME, givenValue);
			}
			JsonNode family = name.get(IScimFields.FAMILY_NAME);
			if (family != null) {
				String familyValue = family.textValue();
				full += familyValue;
				asset.getProperties().put(IScimFields.FAMILY_NAME, familyValue);
			}
			asset.getProperties().put(IWso2ScimFields.PROP_NAME, full.trim());
			asset.setName(full.trim());
		}
	}

	/**
	 * Parse email fields.
	 * 
	 * @param resource
	 * @param asset
	 */
	protected void parseEmail(JsonNode resource, PersonAsset asset) {
		JsonNode emails = resource.get(IScimFields.EMAILS);
		if (emails != null) {
			int index = 1;
			Iterator<JsonNode> it = emails.elements();
			while (it.hasNext()) {
				String email = it.next().textValue();
				asset.getProperties().put("emailAddress" + index, email);
				asset.setEmailAddress(email);
			}
		}
	}

	/**
	 * Update certificate management to ignore invalid SSL certificates.
	 * 
	 * @param caller
	 * @throws SiteWhereException
	 */
	protected void doIgnoreBadCertificate(WebClient caller) throws SiteWhereException {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };
		HTTPConduit httpConduit = (HTTPConduit) WebClient.getConfig(caller).getConduit();
		TLSClientParameters tlsCP = new TLSClientParameters();
		tlsCP.setTrustManagers(trustAllCerts);
		tlsCP.setDisableCNCheck(true);
		httpConduit.setTlsClientParameters(tlsCP);
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getScimUsersUrl() {
		return scimUsersUrl;
	}

	public void setScimUsersUrl(String scimUsersUrl) {
		this.scimUsersUrl = scimUsersUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isIgnoreBadCertificate() {
		return ignoreBadCertificate;
	}

	public void setIgnoreBadCertificate(boolean ignoreBadCertificate) {
		this.ignoreBadCertificate = ignoreBadCertificate;
	}
}