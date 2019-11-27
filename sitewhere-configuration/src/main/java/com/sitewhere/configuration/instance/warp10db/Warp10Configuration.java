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

    /** Default token.secret */
    private static final String DEFAULT_TOKEN_SECRET = "sitewhere";

    /** Host */
    private String hostname;

    /** Token secret */
    private String tokenSecret = DEFAULT_TOKEN_SECRET;

    /** Application */
    private String application;

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

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }
}
