/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.destinations.coap;

/**
 * Parameters required for a CoAP client request.
 * 
 * @author Derek
 */
public class CoapParameters {

    /** Hostname */
    private String hostname;

    /** Port */
    private int port = 5683;

    /** URL where command is posted */
    private String url = "";

    /** HTTP method used */
    private String method = "POST";

    public String getHostname() {
	return hostname;
    }

    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	this.port = port;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public String getMethod() {
	return method;
    }

    public void setMethod(String method) {
	this.method = method;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
	return "CoAP request: " + getMethod().toUpperCase() + " Host: " + getHostname() + " Port: " + getPort()
		+ " URL: " + getUrl();
    }
}