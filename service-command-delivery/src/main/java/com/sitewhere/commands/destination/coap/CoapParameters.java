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
package com.sitewhere.commands.destination.coap;

/**
 * Parameters required for a CoAP client request.
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