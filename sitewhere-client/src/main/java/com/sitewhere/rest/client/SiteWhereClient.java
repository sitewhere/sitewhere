/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.sitewhere.rest.ISiteWhereWebConstants;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.datatype.JsonDateSerializer;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.device.batch.BatchOperation;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.rest.model.device.event.DeviceEventBatchResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.request.BatchCommandInvocationRequest;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupElementCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceSpecificationCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceStreamCreateRequest;
import com.sitewhere.rest.model.device.request.SiteCreateRequest;
import com.sitewhere.rest.model.device.request.ZoneCreateRequest;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.rest.model.search.AssetSearchResults;
import com.sitewhere.rest.model.search.DateRangeSearchCriteria;
import com.sitewhere.rest.model.search.DeviceAlertSearchResults;
import com.sitewhere.rest.model.search.DeviceAssignmentSearchResults;
import com.sitewhere.rest.model.search.DeviceCommandInvocationSearchResults;
import com.sitewhere.rest.model.search.DeviceCommandSearchResults;
import com.sitewhere.rest.model.search.DeviceGroupElementSearchResults;
import com.sitewhere.rest.model.search.DeviceGroupSearchResults;
import com.sitewhere.rest.model.search.DeviceLocationSearchResults;
import com.sitewhere.rest.model.search.DeviceMeasurementsSearchResults;
import com.sitewhere.rest.model.search.DeviceSearchResults;
import com.sitewhere.rest.model.search.DeviceSpecificationSearchResults;
import com.sitewhere.rest.model.search.DeviceStreamSearchResults;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.ZoneSearchResults;
import com.sitewhere.rest.model.system.Version;
import com.sitewhere.spi.ISiteWhereClient;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;

/**
 * Client for interacting with SiteWhere REST services.
 * 
 * @author dadams
 */
public class SiteWhereClient implements ISiteWhereClient {

    /** Default base url for calling REST services */
    public static final String DEFAULT_BASE_URL = "http://localhost:8080/sitewhere/api/";

    /** Default REST username */
    public static final String DEFAULT_USERNAME = "admin";

    /** Default REST password */
    public static final String DEFAULT_PASSWORD = "password";

    /** Default tenant authentication token */
    public static final String DEFAULT_TENANT_AUTH_TOKEN = "sitewhere1234567890";

    /** Default connection timeout in milliseconds */
    public static final int DEFAULT_CONNECT_TIMEOUT = 3 * 1000;

    /** Indicates whether to write debug information to the console */
    public static final boolean DEBUG_ENABLED = false;

    /** Use CXF web client to send requests */
    private RestTemplate client;

    /** Base URL used for REST calls */
    private String baseUrl = DEFAULT_BASE_URL;

    /** Username used for REST calls */
    private String username = DEFAULT_USERNAME;

    /** Password used for REST calls */
    private String password = DEFAULT_PASSWORD;

    /** Tenant auth token used for REST calls */
    private String tenantAuthToken = DEFAULT_TENANT_AUTH_TOKEN;

    public SiteWhereClient() {
	this(DEFAULT_BASE_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_TENANT_AUTH_TOKEN, DEFAULT_CONNECT_TIMEOUT);
    }

    public SiteWhereClient(String url, String username, String password) {
	this(url, username, password, DEFAULT_TENANT_AUTH_TOKEN, 0);
    }

    public SiteWhereClient(String url, String username, String password, int connectTimeoutMs) {
	this(url, username, password, DEFAULT_TENANT_AUTH_TOKEN, connectTimeoutMs);
    }

    public SiteWhereClient(String url, String username, String password, String tenantAuthToken) {
	this(url, username, password, tenantAuthToken, 0);
    }

    public SiteWhereClient(String url, String username, String password, String tenantAuthToken, int connectTimeoutMs) {
	if (DEBUG_ENABLED) {
	    enableDebugging();
	}
	this.client = new RestTemplate();
	this.username = username;
	this.password = password;
	this.tenantAuthToken = tenantAuthToken;

	// Special handling for delete requests with request body passed.
	HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory() {
	    @Override
	    protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
		if (HttpMethod.DELETE == httpMethod) {
		    return new HttpEntityEnclosingDeleteRequest(uri);
		}
		return super.createHttpUriRequest(httpMethod, uri);
	    }
	};
	if (connectTimeoutMs > 0) {
	    factory.setConnectTimeout(connectTimeoutMs);
	}
	client.setRequestFactory(factory);
	List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
	addMessageConverters(converters);
	client.setMessageConverters(converters);
	client.setErrorHandler(new SiteWhereErrorHandler());
	this.baseUrl = url;
    }

    /**
     * Allow subclasses to override converters used for the
     * {@link RestTemplate}.
     * 
     * @param converters
     */
    protected void addMessageConverters(List<HttpMessageConverter<?>> converters) {
	converters.add(new MappingJackson2HttpMessageConverter());
	converters.add(new ByteArrayHttpMessageConverter());
    }

    /**
     * Enable console debugging for messages sent by the client.
     */
    protected void enableDebugging() {
	System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
	System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
	System.setProperty("org.apache.commons.logging.simplelog.log.org.apache", "debug");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.ISiteWhereClient#getSiteWhereVersion()
     */
    @Override
    public Version getSiteWhereVersion() throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	return sendRest(getBaseUrl() + "system/version", HttpMethod.GET, null, Version.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.ISiteWhereClient#createDeviceSpecification(com.
     * sitewhere.rest .model.device.request.DeviceSpecificationCreateRequest)
     */
    @Override
    public DeviceSpecification createDeviceSpecification(DeviceSpecificationCreateRequest request)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	return sendRest(getBaseUrl() + "specifications", HttpMethod.POST, request, DeviceSpecification.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#getDeviceSpecificationByToken(java.
     * lang.String)
     */
    @Override
    public DeviceSpecification getDeviceSpecificationByToken(String token) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", token);
	return sendRest(getBaseUrl() + "specifications/{token}", HttpMethod.GET, null, DeviceSpecification.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#updateDeviceSpecification(java.lang.
     * String,
     * com.sitewhere.rest.model.device.request.DeviceSpecificationCreateRequest)
     */
    @Override
    public DeviceSpecification updateDeviceSpecification(String token, DeviceSpecificationCreateRequest request)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", token);
	return sendRest(getBaseUrl() + "specifications/{token}", HttpMethod.PUT, null, DeviceSpecification.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.ISiteWhereClient#listDeviceSpecifications(boolean,
     * boolean, com.sitewhere.rest.model.search.SearchCriteria)
     */
    @Override
    public DeviceSpecificationSearchResults listDeviceSpecifications(boolean includeDeleted, boolean includeAsset,
	    SearchCriteria criteria) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("includeDeleted", String.valueOf(includeDeleted));
	vars.put("includeAsset", String.valueOf(includeAsset));
	addSearchCriteria(vars, criteria);
	return sendRest(
		getBaseUrl() + "specifications?includeDeleted={includeDeleted}&includeAsset={includeAsset}&"
			+ getSearchCriteriaFields(criteria),
		HttpMethod.GET, null, DeviceSpecificationSearchResults.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#deleteDeviceSpecification(java.lang.
     * String, boolean)
     */
    @Override
    public DeviceSpecification deleteDeviceSpecification(String token, boolean deletePermanently)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", token);
	vars.put("force", String.valueOf(deletePermanently));
	return sendRest(getBaseUrl() + "specifications/{token}&force={force}", HttpMethod.DELETE, null,
		DeviceSpecification.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#createDeviceCommand(java.lang.String,
     * com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest)
     */
    @Override
    public DeviceCommand createDeviceCommand(String specToken, DeviceCommandCreateRequest request)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", specToken);
	return sendRest(getBaseUrl() + "specifications/{token}/commands", HttpMethod.POST, request, DeviceCommand.class,
		vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#listDeviceCommands(java.lang.String,
     * boolean)
     */
    @Override
    public DeviceCommandSearchResults listDeviceCommands(String specificationToken, boolean includeDeleted)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", specificationToken);
	vars.put("includeDeleted", String.valueOf(includeDeleted));
	return sendRest(getBaseUrl() + "specifications/{token}/commands?includeDeleted={includeDeleted}",
		HttpMethod.GET, null, DeviceCommandSearchResults.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#createSite(com.sitewhere.rest.model.
     * device. request .SiteCreateRequest)
     */
    @Override
    public Site createSite(SiteCreateRequest request) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	return sendRest(getBaseUrl() + "sites", HttpMethod.POST, request, Site.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.ISiteWhereClient#getSiteByToken(java.lang.String)
     */
    public Site getSiteByToken(String token) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	return sendRest(getBaseUrl() + "sites/" + token, HttpMethod.GET, null, Site.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.ISiteWhereClient#createZone(java.lang.String,
     * com.sitewhere.rest.model.device.request.ZoneCreateRequest)
     */
    @Override
    public Zone createZone(String siteToken, ZoneCreateRequest request) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("siteToken", siteToken);
	return sendRest(getBaseUrl() + "sites/{siteToken}/zones", HttpMethod.POST, request, Zone.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#listZonesForSite(java.lang.String)
     */
    @Override
    public ZoneSearchResults listZonesForSite(String siteToken) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("siteToken", siteToken);
	String url = getBaseUrl() + "sites/{siteToken}/zones";
	return sendRest(url, HttpMethod.GET, null, ZoneSearchResults.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#createDevice(com.sitewhere.rest.model.
     * device .request.DeviceCreateRequest )
     */
    @Override
    public Device createDevice(DeviceCreateRequest request) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	return sendRest(getBaseUrl() + "devices", HttpMethod.POST, request, Device.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.ISiteWhereClient#getDeviceByHardwareId(java.lang.
     * String)
     */
    @Override
    public Device getDeviceByHardwareId(String hardwareId) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("hardwareId", hardwareId);
	return sendRest(getBaseUrl() + "devices/{hardwareId}", HttpMethod.GET, null, Device.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.ISiteWhereClient#updateDevice(java.lang.String,
     * com.sitewhere.rest.model.device.request.DeviceCreateRequest)
     */
    @Override
    public Device updateDevice(String hardwareId, DeviceCreateRequest request) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("hardwareId", hardwareId);
	return sendRest(getBaseUrl() + "devices/{hardwareId}", HttpMethod.PUT, request, Device.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.ISiteWhereClient#listDevices(boolean, boolean,
     * boolean, boolean,
     * com.sitewhere.rest.model.search.DateRangeSearchCriteria)
     */
    @Override
    public DeviceSearchResults listDevices(boolean includeDeleted, boolean excludeAssigned,
	    boolean populateSpecification, boolean populateAssignment, DateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("includeDeleted", String.valueOf(includeDeleted));
	vars.put("excludeAssigned", String.valueOf(excludeAssigned));
	vars.put("includeSpecification", String.valueOf(populateSpecification));
	vars.put("includeAssignment", String.valueOf(populateAssignment));
	addSearchCriteria(vars, criteria);
	return sendRest(
		getBaseUrl() + "devices?includeDeleted={includeDeleted}"
			+ "&excludeAssigned={excludeAssigned}&includeSpecification={includeSpecification}"
			+ "&includeAssignment={includeAssignment}&" + getSearchCriteriaFields(criteria),
		HttpMethod.GET, null, DeviceSearchResults.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.ISiteWhereClient#deleteDevice(java.lang.String,
     * boolean)
     */
    @Override
    public Device deleteDevice(String hardwareId, boolean force) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("hardwareId", hardwareId);
	vars.put("force", String.valueOf(force));
	String url = getBaseUrl() + "devices/{hardwareId}&force={force}";
	return sendRest(url, HttpMethod.DELETE, null, Device.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#getCurrentAssignmentForDevice(java.
     * lang.String)
     */
    @Override
    public DeviceAssignment getCurrentAssignmentForDevice(String hardwareId) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("hardwareId", hardwareId);
	return sendRest(
		getBaseUrl()
			+ "devices/{hardwareId}/assignment?includeAsset=false&includeDevice=false&includeSite=false",
		HttpMethod.GET, null, DeviceAssignment.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#createDeviceAssignment(com.sitewhere.
     * spi.device .request. IDeviceAssignmentCreateRequest)
     */
    @Override
    public DeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	return sendRest(getBaseUrl() + "assignments", HttpMethod.POST, request, DeviceAssignment.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#getDeviceAssignmentByToken(java.lang.
     * String)
     */
    @Override
    public DeviceAssignment getDeviceAssignmentByToken(String assignmentToken) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("assignmentToken", assignmentToken);
	return sendRest(getBaseUrl() + "assignments/{assignmentToken}", HttpMethod.GET, null, DeviceAssignment.class,
		vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.ISiteWhereClient#listAssignmentsForSite(java.lang.
     * String)
     */
    public DeviceAssignmentSearchResults listAssignmentsForSite(String token) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	return sendRest(getBaseUrl() + "sites/" + token + "/assignments?includeDevice=true", HttpMethod.GET, null,
		DeviceAssignmentSearchResults.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.ISiteWhereClient#deleteDeviceAssignment(java.lang.
     * String, boolean)
     */
    @Override
    public DeviceAssignment deleteDeviceAssignment(String assignmentToken, boolean force) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("assignmentToken", assignmentToken);
	String url = getBaseUrl() + "assignments/{assignmentToken}";
	if (force) {
	    url += "?force=true";
	}
	return sendRest(url, HttpMethod.DELETE, null, DeviceAssignment.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#listDeviceAssignmentHistory(java.lang.
     * String)
     */
    @Override
    public DeviceAssignmentSearchResults listDeviceAssignmentHistory(String hardwareId) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("hardwareId", hardwareId);
	return sendRest(getBaseUrl() + "devices/{hardwareId}/assignments", HttpMethod.GET, null,
		DeviceAssignmentSearchResults.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.ISiteWhereClient#getAssignmentsForAsset(java.lang.
     * String, java.lang.String, java.lang.String,
     * com.sitewhere.spi.device.DeviceAssignmentStatus,
     * com.sitewhere.rest.model.search.SearchCriteria)
     */
    @Override
    public DeviceAssignmentSearchResults getAssignmentsForAsset(String siteToken, String assetModuleId, String assetId,
	    DeviceAssignmentStatus status, SearchCriteria criteria) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("assetModuleId", assetModuleId);
	vars.put("assetId", assetId);
	vars.put("siteToken", siteToken);
	String url = getBaseUrl() + "assets/modules/{assetModuleId}/assets/{assetId}/assignments?siteToken={siteToken}";
	if (status != null) {
	    url += "&status=" + status.name();
	}
	if (criteria != null) {
	    url += "&page=" + criteria.getPageNumber() + "&pageSize=" + criteria.getPageSize();
	}
	return sendRest(url, HttpMethod.GET, null, DeviceAssignmentSearchResults.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#addDeviceEventBatch(java.lang.String,
     * com.sitewhere.rest.model.device.DeviceEventBatch)
     */
    @Override
    public DeviceEventBatchResponse addDeviceEventBatch(String hardwareId, DeviceEventBatch batch)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("hardwareId", hardwareId);
	return sendRest(getBaseUrl() + "devices/{hardwareId}/batch", HttpMethod.POST, batch,
		DeviceEventBatchResponse.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#updateDeviceAssignmentMetadata(java.
     * lang.String, com.sitewhere.rest.model.device.MetadataProvider)
     */
    @Override
    public DeviceAssignment updateDeviceAssignmentMetadata(String token, MetadataProvider metadata)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", token);
	return sendRest(getBaseUrl() + "assignments/{token}/metadata", HttpMethod.POST, metadata,
		DeviceAssignment.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#createDeviceMeasurements(java.lang.
     * String,
     * com.sitewhere.rest.model.device.request.DeviceMeasurementsCreateRequest)
     */
    @Override
    public DeviceMeasurements createDeviceMeasurements(String assignmentToken, DeviceMeasurementsCreateRequest request)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", assignmentToken);
	return sendRest(getBaseUrl() + "assignments/{token}/measurements", HttpMethod.POST, request,
		DeviceMeasurements.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.ISiteWhereClient#listDeviceMeasurements(java.lang.
     * String, int)
     */
    @Override
    public SearchResults<DeviceMeasurements> listDeviceMeasurements(String assignmentToken,
	    DateRangeSearchCriteria searchCriteria) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", assignmentToken);
	addSearchCriteria(vars, searchCriteria);

	String url = getBaseUrl() + "assignments/{token}/measurements?";
	if (searchCriteria != null) {
	    url += getSearchCriteriaFields(searchCriteria);
	}
	return sendRest(url, HttpMethod.GET, null, DeviceMeasurementsSearchResults.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#createDeviceLocation(java.lang.String,
     * com.sitewhere.rest.model.device.request.DeviceLocationCreateRequest)
     */
    @Override
    public DeviceLocation createDeviceLocation(String assignmentToken, DeviceLocationCreateRequest request)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", assignmentToken);
	return sendRest(getBaseUrl() + "assignments/{token}/locations", HttpMethod.POST, request, DeviceLocation.class,
		vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#listDeviceLocations(java.lang.String,
     * com.sitewhere.rest.model.search.DateRangeSearchCriteria)
     */
    @Override
    public DeviceLocationSearchResults listDeviceLocations(String assignmentToken, DateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", assignmentToken);
	addSearchCriteria(vars, criteria);
	String url = getBaseUrl() + "assignments/{token}/locations?" + getSearchCriteriaFields(criteria);
	return sendRest(url, HttpMethod.GET, null, DeviceLocationSearchResults.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#createDeviceAlert(java.lang.String,
     * com.sitewhere.rest.model.device.request.DeviceAlertCreateRequest)
     */
    @Override
    public DeviceAlert createDeviceAlert(String assignmentToken, DeviceAlertCreateRequest request)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", assignmentToken);
	return sendRest(getBaseUrl() + "assignments/{token}/alerts", HttpMethod.POST, request, DeviceAlert.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#listDeviceAlerts(java.lang.String,
     * com.sitewhere.rest.model.search.DateRangeSearchCriteria)
     */
    @Override
    public DeviceAlertSearchResults listDeviceAlerts(String assignmentToken, DateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", assignmentToken);
	addSearchCriteria(vars, criteria);
	String url = getBaseUrl() + "assignments/{token}/alerts?" + getSearchCriteriaFields(criteria);
	return sendRest(url, HttpMethod.GET, null, DeviceAlertSearchResults.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#createDeviceCommandInvocation(java.
     * lang.String, com.sitewhere.rest.model.device.event.request.
     * DeviceCommandInvocationCreateRequest)
     */
    @Override
    public DeviceCommandInvocation createDeviceCommandInvocation(String assignmentToken,
	    DeviceCommandInvocationCreateRequest request) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", assignmentToken);
	return sendRest(getBaseUrl() + "assignments/{token}/invocations", HttpMethod.POST, request,
		DeviceCommandInvocation.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#listDeviceCommandInvocations(java.lang
     * .String, com.sitewhere.rest.model.search.DateRangeSearchCriteria)
     */
    @Override
    public DeviceCommandInvocationSearchResults listDeviceCommandInvocations(String assignmentToken,
	    DateRangeSearchCriteria criteria) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", assignmentToken);
	addSearchCriteria(vars, criteria);
	String url = getBaseUrl() + "assignments/{token}/invocations?" + getSearchCriteriaFields(criteria);
	return sendRest(url, HttpMethod.GET, null, DeviceCommandInvocationSearchResults.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#createDeviceStream(java.lang.String,
     * com.sitewhere.rest.model.device.request.DeviceStreamCreateRequest)
     */
    @Override
    public DeviceStream createDeviceStream(String assignmentToken, DeviceStreamCreateRequest request)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", assignmentToken);
	return sendRest(getBaseUrl() + "assignments/{token}/streams", HttpMethod.POST, request, DeviceStream.class,
		vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.ISiteWhereClient#getDeviceStream(java.lang.String,
     * java.lang.String)
     */
    @Override
    public DeviceStream getDeviceStream(String assignmentToken, String streamId) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", assignmentToken);
	vars.put("streamId", streamId);
	String url = getBaseUrl() + "assignments/{token}/streams/{streamId}";
	return sendRest(url, HttpMethod.GET, null, DeviceStream.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#listDeviceStreams(java.lang.String,
     * com.sitewhere.rest.model.search.DateRangeSearchCriteria)
     */
    @Override
    public DeviceStreamSearchResults listDeviceStreams(String assignmentToken, DateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", assignmentToken);
	addSearchCriteria(vars, criteria);
	String url = getBaseUrl() + "assignments/{token}/streams?" + getSearchCriteriaFields(criteria);
	return sendRest(url, HttpMethod.GET, null, DeviceStreamSearchResults.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#addDeviceStreamData(java.lang.String,
     * java.lang.String, long, byte[])
     */
    @Override
    public void addDeviceStreamData(String assignmentToken, String streamId, long sequenceNumber, byte[] data)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", assignmentToken);
	vars.put("streamId", streamId);
	vars.put("sequenceNumber", String.valueOf(sequenceNumber));
	sendBinary(getBaseUrl() + "assignments/{token}/streams/{streamId}?sequenceNumber={sequenceNumber}",
		HttpMethod.POST, data, null, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#getDeviceStreamData(java.lang.String,
     * java.lang.String, long)
     */
    @Override
    public byte[] getDeviceStreamData(String assignmentToken, String streamId, long sequenceNumber)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", assignmentToken);
	vars.put("streamId", streamId);
	vars.put("sequenceNumber", String.valueOf(sequenceNumber));
	try {
	    return sendRestWithBinaryResponse(
		    getBaseUrl() + "assignments/{token}/streams/{streamId}/data/{sequenceNumber}", HttpMethod.GET,
		    vars);
	} catch (SiteWhereSystemException e) {
	    return null;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#listDeviceStreamData(java.lang.String,
     * java.lang.String,
     * com.sitewhere.rest.model.search.DateRangeSearchCriteria)
     */
    @Override
    public byte[] listDeviceStreamData(String assignmentToken, String streamId, DateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", assignmentToken);
	vars.put("streamId", streamId);
	return sendRestWithBinaryResponse(getBaseUrl() + "assignments/{token}/streams/{streamId}/data", HttpMethod.GET,
		vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#createBatchCommandInvocation(java.lang
     * .String, java.lang.String, java.util.Map, java.util.List)
     */
    @Override
    public BatchOperation createBatchCommandInvocation(String batchToken, String commandToken,
	    Map<String, String> parameters, List<String> hardwareIds) throws SiteWhereException {
	BatchCommandInvocationRequest request = new BatchCommandInvocationRequest();
	request.setToken(batchToken);
	request.setCommandToken(commandToken);
	request.setParameterValues(parameters);
	request.setHardwareIds(hardwareIds);
	Map<String, String> vars = new HashMap<String, String>();
	return sendRest(getBaseUrl() + "batch/command", HttpMethod.POST, request, BatchOperation.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#createDeviceGroup(com.sitewhere.rest.
     * model. device .request.DeviceGroupCreateRequest)
     */
    @Override
    public DeviceGroup createDeviceGroup(DeviceGroupCreateRequest request) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	return sendRest(getBaseUrl() + "devicegroups", HttpMethod.POST, request, DeviceGroup.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.ISiteWhereClient#getDeviceGroupByToken(java.lang.
     * String)
     */
    @Override
    public DeviceGroup getDeviceGroupByToken(String token) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", token);
	return sendRest(getBaseUrl() + "devicegroups/{token}", HttpMethod.GET, null, DeviceGroup.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#deleteDeviceGroup(java.lang.String)
     */
    @Override
    public DeviceGroup deleteDeviceGroup(String token) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", token);
	return sendRest(getBaseUrl() + "devicegroups/{token}", HttpMethod.DELETE, null, DeviceGroup.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#listDeviceGroups(java.lang.String,
     * com.sitewhere.rest.model.search.SearchCriteria, boolean)
     */
    @Override
    public DeviceGroupSearchResults listDeviceGroups(String role, SearchCriteria criteria, boolean includeDeleted)
	    throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	addSearchCriteria(vars, criteria);
	String url = getBaseUrl() + "devicegroups?includeDeleted=" + includeDeleted + "&"
		+ getSearchCriteriaFields(criteria);
	if (role != null) {
	    url += "&role=" + role;
	}
	return sendRest(url, HttpMethod.GET, null, DeviceGroupSearchResults.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.ISiteWhereClient#addDeviceGroupElements(java.lang.
     * String, java.util.List)
     */
    @Override
    public DeviceGroupElementSearchResults addDeviceGroupElements(String groupToken,
	    List<DeviceGroupElementCreateRequest> elements) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", groupToken);
	return sendRest(getBaseUrl() + "devicegroups/{token}/elements", HttpMethod.PUT, elements,
		DeviceGroupElementSearchResults.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#listDeviceGroupElements(java.lang.
     * String, boolean, com.sitewhere.rest.model.search.SearchCriteria)
     */
    @Override
    public DeviceGroupElementSearchResults listDeviceGroupElements(String groupToken, boolean includeDetails,
	    SearchCriteria criteria) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", groupToken);
	addSearchCriteria(vars, criteria);
	String url = getBaseUrl() + "devicegroups/{token}/elements?" + getSearchCriteriaFields(criteria);
	return sendRest(url, HttpMethod.GET, null, DeviceGroupElementSearchResults.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#deleteDeviceGroupElements(java.lang.
     * String, java.util.List)
     */
    @Override
    public DeviceGroupElementSearchResults deleteDeviceGroupElements(String groupToken,
	    List<DeviceGroupElementCreateRequest> elements) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	vars.put("token", groupToken);
	return sendRest(getBaseUrl() + "devicegroups/{token}/elements", HttpMethod.DELETE, elements,
		DeviceGroupElementSearchResults.class, vars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.ISiteWhereClient#getAssetsByModuleId(java.lang.String,
     * java.lang.String)
     */
    public AssetSearchResults getAssetsByModuleId(String moduleId, String criteria) throws SiteWhereException {
	Map<String, String> vars = new HashMap<String, String>();
	String url = "assets/modules/" + moduleId + "/assets";
	if ((criteria != null) && (criteria.length() > 0)) {
	    url += "?criteria=" + criteria;
	}
	return sendRest(getBaseUrl() + url, HttpMethod.GET, null, AssetSearchResults.class, vars);
    }

    /**
     * Get fields associated with search criteria.
     * 
     * @return
     */
    protected String getSearchCriteriaFields(SearchCriteria criteria) {
	String result = "page={page}&pageSize={pageSize}";
	if (criteria instanceof DateRangeSearchCriteria) {
	    DateRangeSearchCriteria dates = (DateRangeSearchCriteria) criteria;
	    if (dates.getStartDate() != null) {
		result += "&startDate={startDate}";
	    }
	    if (dates.getEndDate() != null) {
		result += "&endDate={endDate}";
	    }
	}
	return result;
    }

    /**
     * Add fields from {@link SearchCriteria}.
     * 
     * @param vars
     * @param criteria
     */
    protected void addSearchCriteria(Map<String, String> vars, SearchCriteria criteria) {
	if (criteria != null) {
	    if (criteria.getPageNumber() != null) {
		vars.put("page", String.valueOf(criteria.getPageNumber()));
	    }

	    if (criteria.getPageSize() != null) {
		vars.put("pageSize", String.valueOf(criteria.getPageSize()));
	    }
	}
	if (criteria instanceof DateRangeSearchCriteria) {
	    DateRangeSearchCriteria dates = (DateRangeSearchCriteria) criteria;
	    if (dates.getStartDate() != null) {
		vars.put("startDate", JsonDateSerializer.serialize(dates.getStartDate()));
	    }

	    if (dates.getEndDate() != null) {
		vars.put("endDate", JsonDateSerializer.serialize(dates.getEndDate()));
	    }
	}
    }

    /**
     * Send a REST request and handle the response.
     * 
     * @param url
     * @param method
     * @param input
     * @param clazz
     * @param vars
     * @return
     * @throws SiteWhereSystemException
     */
    protected <S, T> S sendRest(String url, HttpMethod method, T input, Class<S> clazz, Map<String, String> vars)
	    throws SiteWhereSystemException {
	try {
	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Authorization", getAuthHeader());
	    headers.add(ISiteWhereWebConstants.HEADER_TENANT_AUTH, getTenantAuthToken());
	    HttpEntity<T> entity = new HttpEntity<T>(input, headers);
	    ResponseEntity<S> response = getClient().exchange(url, method, entity, clazz, vars);
	    return response.getBody();
	} catch (ResourceAccessException e) {
	    if (e.getCause() instanceof SiteWhereSystemException) {
		throw (SiteWhereSystemException) e.getCause();
	    }
	    throw new RuntimeException(e);
	}
    }

    protected <S, T> S sendBinary(String url, HttpMethod method, T input, Class<S> clazz, Map<String, String> vars)
	    throws SiteWhereSystemException {
	try {
	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Authorization", getAuthHeader());
	    headers.add(ISiteWhereWebConstants.HEADER_TENANT_AUTH, getTenantAuthToken());
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	    HttpEntity<T> entity = new HttpEntity<T>(input, headers);
	    ResponseEntity<S> response = getClient().exchange(url, method, entity, clazz, vars);
	    return response.getBody();
	} catch (ResourceAccessException e) {
	    if (e.getCause() instanceof SiteWhereSystemException) {
		throw (SiteWhereSystemException) e.getCause();
	    }
	    throw new RuntimeException(e);
	}
    }

    /**
     * Send a REST request and return response as byte array.
     * 
     * @param url
     * @param method
     * @param vars
     * @return
     * @throws SiteWhereSystemException
     */
    protected <T> byte[] sendRestWithBinaryResponse(String url, HttpMethod method, Map<String, String> vars)
	    throws SiteWhereSystemException {
	try {
	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Authorization", getAuthHeader());
	    headers.add(ISiteWhereWebConstants.HEADER_TENANT_AUTH, getTenantAuthToken());

	    ResponseEntity<byte[]> response = getClient().exchange(url, method, new HttpEntity<byte[]>(headers),
		    byte[].class, vars);

	    return response.getBody();
	} catch (ResourceAccessException e) {
	    if (e.getCause() instanceof SiteWhereSystemException) {
		throw (SiteWhereSystemException) e.getCause();
	    }
	    throw new RuntimeException(e);
	}
    }

    /**
     * Used to get around limitation of Spring RestTemplate not being able to
     * send a body request for a delete.
     * 
     * @author Derek
     */
    public static class HttpEntityEnclosingDeleteRequest extends HttpEntityEnclosingRequestBase {

	public HttpEntityEnclosingDeleteRequest(final URI uri) {
	    super();
	    setURI(uri);
	}

	@Override
	public String getMethod() {
	    return "DELETE";
	}
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

    public RestTemplate getClient() {
	return client;
    }

    public void setClient(RestTemplate client) {
	this.client = client;
    }

    public String getBaseUrl() {
	return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
	this.baseUrl = baseUrl;
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

    public String getTenantAuthToken() {
	return tenantAuthToken;
    }

    public void setTenantAuthToken(String tenantAuthToken) {
	this.tenantAuthToken = tenantAuthToken;
    }
}