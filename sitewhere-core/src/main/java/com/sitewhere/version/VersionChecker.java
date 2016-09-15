/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.system.IVersionChecker;

/**
 * Attempts to connect to a website to check for information about the latest
 * version of SiteWhere.
 * 
 * @author Derek
 */
public class VersionChecker implements IVersionChecker {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** URL accessed for latest version information */
    private static final String VERSION_URL = "http://www.sitewhere.org/version.php";

    /** Type for server product */
    private static final String TYPE_SERVER = "server";

    /** Edition indicator for SiteWhere Server CE */
    private static final String EDITION_COMMUNITY = "Community";

    /** Use CXF web client to send requests */
    private RestTemplate client;

    public VersionChecker() {
	this.client = new RestTemplate();
	List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
	converters.add(new MappingJackson2HttpMessageConverter());
	converters.add(new StringHttpMessageConverter());
	client.setMessageConverters(converters);
	client.setErrorHandler(new VersionCheckErrorHandler());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
	try {
	    LOGGER.info("Checking for SiteWhere version updates...");
	    IVersion current = VersionHelper.getVersion();
	    ResponseEntity<String> response = getClient().postForEntity(VERSION_URL, current, String.class);
	    String body = response.getBody();
	    LOGGER.debug("Received:\n" + body);
	    LatestVersion latest = MarshalUtils.unmarshalJson(body.getBytes(), LatestVersion.class);
	    LatestVersion.Product product = getProductVersion(latest);
	    int[] currentParts = getVersionParts(current.getVersionIdentifier());
	    int[] latestParts = getVersionParts(product.getCurrentVersion());
	    int matchLength = (currentParts.length > latestParts.length) ? currentParts.length : latestParts.length;
	    for (int i = 0; i < matchLength; i++) {
		if (currentParts[i] < latestParts[i]) {
		    LOGGER.info("A newer version of SiteWhere is available: " + product.getName() + " "
			    + product.getCurrentVersion());
		}
	    }
	    LOGGER.info("SiteWhere version is up to date.");
	} catch (ResourceAccessException e) {
	    LOGGER.debug("Attempt to check latest version failed. " + e.getMessage());
	} catch (SiteWhereException e) {
	    LOGGER.debug("Attempt to check latest version failed. " + e.getMessage());
	}
    }

    /**
     * Show sample of JSON data.
     * 
     * @throws SiteWhereException
     */
    protected void showSample() throws SiteWhereException {
	LatestVersion.Product product = new LatestVersion.Product();
	product.setType("server");
	product.setName("SiteWhere CE");
	product.setEdition("Community");
	product.setCurrentVersion("1.3.0");
	LatestVersion latest = new LatestVersion();
	List<LatestVersion.Product> products = new ArrayList<LatestVersion.Product>();
	products.add(product);
	latest.setProducts(products);
	String output = MarshalUtils.marshalJsonAsString(latest);
	LOGGER.info("Sample:\n" + output);
    }

    /**
     * Get product version information for this product.
     * 
     * @param latest
     * @return
     * @throws SiteWhereException
     */
    protected LatestVersion.Product getProductVersion(LatestVersion latest) throws SiteWhereException {
	for (LatestVersion.Product product : latest.getProducts()) {
	    if (product.getType().equals(TYPE_SERVER) && product.getEdition().equals(EDITION_COMMUNITY)) {
		return product;
	    }
	}
	throw new SiteWhereException("Latest version information does not contain product.");
    }

    /**
     * Split version into integer parts.
     * 
     * @param version
     * @return
     */
    protected int[] getVersionParts(String version) {
	String[] strParts = version.split(".");
	int[] intParts = new int[strParts.length];
	for (int i = 0; i < strParts.length; i++) {
	    intParts[i] = Integer.parseInt(strParts[i]);
	}
	return intParts;
    }

    public RestTemplate getClient() {
	return client;
    }

    public void setClient(RestTemplate client) {
	this.client = client;
    }

    /**
     * Writes a message to the log if the remote site for checking latest
     * version is not available.
     * 
     * @author Derek
     */
    protected static class VersionCheckErrorHandler implements ResponseErrorHandler {

	/** Delegate to default error handler */
	private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.client.ResponseErrorHandler#handleError(org.
	 * springframework .http.client.ClientHttpResponse)
	 */
	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
	    try {
		errorHandler.handleError(response);
	    } catch (RestClientException e) {
		LOGGER.warn("Unable to contact site to check for latest version.");
	    }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.client.ResponseErrorHandler#hasError(org.
	 * springframework .http.client.ClientHttpResponse)
	 */
	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
	    return errorHandler.hasError(response);
	}
    }
}