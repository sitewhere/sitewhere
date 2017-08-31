/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.modules.wso2.scim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.asset.modules.AssetMatcher;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Asset module that interacts with an external WSO2 Identity Server via SCIM.
 * 
 * @author dadams
 */
public class Wso2ScimAssetModule extends LifecycleComponent implements IAssetModule<PersonAsset> {

    /** Serial version UID */
    private static final long serialVersionUID = 1382873664520089825L;

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

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

    /** URL of photo returned when none is assigned */
    private static final String NO_PHOTO_URL = "https://s3.amazonaws.com/sitewhere-demo/people/no-photo.jpg";

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

    /** Use Spring RestTemplate to send requests */
    private RestTemplate client;

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
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	LOGGER.info("Connecting to WSO2 Identity Server instance at: " + getScimUsersUrl());

	// Set up the REST client.
	this.client = isIgnoreBadCertificate() ? new RestTemplate(createSecureTransport(getUsername(), getPassword()))
		: new RestTemplate();
	List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
	converters.add(new MappingJackson2HttpMessageConverter());
	converters.add(new ByteArrayHttpMessageConverter());
	client.setMessageConverters(converters);

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
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
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
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#getComponentName()
     */
    public String getComponentName() {
	return getClass().getSimpleName() + " [" + getId() + "] " + getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModule#getAsset(java.lang.String)
     */
    public PersonAsset getAsset(String id) throws SiteWhereException {
	return assetCache.get(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModule#putAsset(java.lang.String,
     * com.sitewhere.spi.asset.IAsset)
     */
    @Override
    public void putAsset(String id, PersonAsset asset) throws SiteWhereException {
	// TODO: For now, does not update repository.
	assetCache.put(id, asset);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModule#removeAsset(java.lang.String)
     */
    @Override
    public void removeAsset(String id) throws SiteWhereException {
	// TODO: For now, does not update repository.
	assetCache.remove(id);
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
     * @see
     * com.sitewhere.spi.asset.IAssetModule#refresh(com.sitewhere.spi.server.
     * lifecycle.ILifecycleProgressMonitor)
     */
    public ICommandResponse refresh(ILifecycleProgressMonitor monitor) throws SiteWhereException {
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
	JsonNode json = doGet(getScimUsersUrl(), JsonNode.class);
	try {
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
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to read asset response.", e);
	}
	long totalTime = System.currentTimeMillis() - startTime;
	String message = "Cached " + totalAssets + " assets in " + totalTime + "ms.";
	LOGGER.info(message);
	return new CommandResponse(CommandResult.Successful, message);
    }

    /**
     * Perform a REST get operation and return a marshaled result.
     * 
     * @param url
     * @param clazz
     * @return
     * @throws SiteWhereException
     */
    protected <S> S doGet(String url, Class<S> clazz) throws SiteWhereException {
	try {
	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Authorization", getAuthHeader());
	    HttpEntity<Void> entity = new HttpEntity<Void>(headers);
	    ResponseEntity<S> response = client.exchange(url, HttpMethod.GET, entity, clazz);
	    return response.getBody();
	} catch (ResourceAccessException e) {
	    throw new SiteWhereException("REST call failed.", e);
	}
    }

    /**
     * Creates a transport that allows missing certificates to be ignored.
     * 
     * @param username
     * @param password
     * @return
     */
    protected ClientHttpRequestFactory createSecureTransport(String username, String password) {
	HostnameVerifier nullHostnameVerifier = new HostnameVerifier() {
	    public boolean verify(String hostname, SSLSession session) {
		return true;
	    }
	};

	HttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(nullHostnameVerifier)
		.setSSLContext(createContext()).build();

	HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);

	return requestFactory;
    }

    protected SSLContext createContext() {
	TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
	    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return null;
	    }

	    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
	    }

	    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
	    }
	} };

	try {
	    SSLContext sc = SSLContext.getInstance("SSL");
	    sc.init(null, trustAllCerts, null);
	    SSLContext.setDefault(sc);
	    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
		    return true;
		}
	    });
	    return sc;

	} catch (Exception e) {
	}
	return null;
    }

    /**
     * Encode the username and password to make the authorization header.
     * 
     * @return
     */
    protected String getAuthHeader() {
	String token = getUsername() + ":" + getPassword();
	String encoded = new String(Base64.encodeBase64(token.getBytes()));
	return "Basic " + encoded;
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
	} else {
	    asset.getProperties().put(IWso2ScimFields.PROP_PROFILE_URL, NO_PHOTO_URL);
	    asset.setImageUrl(NO_PHOTO_URL);
	}

	parseName(resource, asset);
	parseEmail(resource, asset);
	parseRoles(resource, asset);

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
     * Parse role information.
     * 
     * @param resource
     * @param asset
     */
    protected void parseRoles(JsonNode resource, PersonAsset asset) {
	JsonNode groups = resource.get(IScimFields.GROUPS);
	if (groups != null) {
	    Iterator<JsonNode> it = groups.elements();
	    while (it.hasNext()) {
		JsonNode fields = it.next();
		JsonNode role = fields.get(IScimFields.DISPLAY);
		if (role != null) {
		    asset.getRoles().add(role.textValue());
		}
	    }
	}
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