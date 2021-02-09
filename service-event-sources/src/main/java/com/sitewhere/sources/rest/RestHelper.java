/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.sources.rest;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;

/**
 * Helper class that simplifies use of Spring for scripting.
 */
public class RestHelper {

    /** Base URL used for REST calls */
    private String baseUrl;

    /** Username used for REST calls */
    private String username;

    /** Password used for REST calls */
    private String password;

    /** Use CXF web client to send requests */
    // private RestTemplate client;

    /** Indicates whether to ignore a bad SSL certificate on the server */
    private boolean ignoreBadCertificate = true;

    public RestHelper(String baseUrl, String username, String password) {
	this.baseUrl = baseUrl;
	this.username = username;
	this.password = password;
	//
	// this.client = isIgnoreBadCertificate() ? new
	// RestTemplate(createSecureTransport()) : new RestTemplate();
	// List<HttpMessageConverter<?>> converters = new
	// ArrayList<HttpMessageConverter<?>>();
	// converters.add(new MappingJackson2HttpMessageConverter());
	// client.setMessageConverters(converters);
    }

    /**
     * Make REST call and return JsonNode response.
     * 
     * @param relativeUrl
     * @return
     * @throws SiteWhereException
     */
    public JsonNode getJsonNode(String relativeUrl) throws SiteWhereException {
	return get(relativeUrl, JsonNode.class);
    }

    /**
     * Make REST call and return String response.
     * 
     * @param relativeUrl
     * @return
     * @throws SiteWhereException
     */
    public String getString(String relativeUrl) throws SiteWhereException {
	return get(relativeUrl, String.class);
    }

    /**
     * Perform a GET request to the given relative URL.
     * 
     * @param relativeUrl
     * @param responseType
     * @return
     * @throws SiteWhereSystemException
     */
    protected <T> T get(String relativeUrl, Class<T> responseType) throws SiteWhereException {
	// try {
	// HttpHeaders headers = new HttpHeaders();
	// if (!StringUtils.isEmpty(getUsername()) &&
	// !StringUtils.isEmpty(getPassword())) {
	// headers.add("Authorization", getAuthHeader());
	// }
	// HttpEntity<Void> entity = new HttpEntity<Void>(headers);
	// String url = baseUrl + relativeUrl;
	// ResponseEntity<T> response = client.exchange(url, HttpMethod.GET, entity,
	// responseType);
	// return response.getBody();
	// } catch (ResourceAccessException e) {
	// throw new SiteWhereException(e);
	// }
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
     * Create SSL context that allows bad certificates.
     * 
     * @return
     */
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

    public boolean isIgnoreBadCertificate() {
	return ignoreBadCertificate;
    }

    public void setIgnoreBadCertificate(boolean ignoreBadCertificate) {
	this.ignoreBadCertificate = ignoreBadCertificate;
    }
}