/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.instance.warp10db;

/**
 * Common configuration settings for an warp10 client.
 *
 * @author Luciano
 */
public class Warp10Configuration {

    /** Default hostname */
    private static final String DEFAULT_HOSTNAME = "http://warp10.default.svc.cluster.local:8080/api/v0";

    /** Default port */
    private static final int DEFAULT_PORT = 8080;

    /** Default token.secret */
    private static final String DEFAULT_TOKEN_SECRET = "token-secret";

    /** Default application */
    private static final String APPLICATION = "default-application";

    /** Host */
    private String hostname = DEFAULT_HOSTNAME;

    /** Token secret */
    private String tokenSecret = DEFAULT_TOKEN_SECRET;

    /** Application */
    private String application = APPLICATION;

    /** Port */
    private int port = DEFAULT_PORT;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }
}
