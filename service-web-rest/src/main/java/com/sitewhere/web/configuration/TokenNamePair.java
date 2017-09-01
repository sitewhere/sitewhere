/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration;

/**
 * Maps a token to an object name.
 * 
 * @author Derek
 */
public class TokenNamePair implements Comparable<TokenNamePair> {

    /** Token */
    private String token;

    /** Name */
    private String name;

    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(TokenNamePair other) {
	return getName().compareTo(other.getName());
    }
}