/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.security;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;

import io.jsonwebtoken.Claims;

/**
 * Allows for creating and validation of JWT tokens.
 */
public interface ITokenManagement {

    /**
     * Generate a token for the given user.
     * 
     * @param user
     * @param expirationInMinutes
     * @return
     * @throws SiteWhereException
     */
    public String generateToken(IUser user, int expirationInMinutes) throws SiteWhereException;

    /**
     * Get claims for the given token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public Claims getClaimsForToken(String token) throws SiteWhereException;

    /**
     * Get username from the given token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public String getUsernameFromToken(String token) throws SiteWhereException;

    /**
     * Get username from claims.
     * 
     * @param claims
     * @return
     * @throws SiteWhereException
     */
    public String getUsernameFromClaims(Claims claims) throws SiteWhereException;

    /**
     * Get granted authorities from given token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public List<IGrantedAuthority> getGrantedAuthoritiesFromToken(String token) throws SiteWhereException;

    /**
     * Get granted authorities from claims.
     * 
     * @param claims
     * @return
     * @throws SiteWhereException
     */
    public List<IGrantedAuthority> getGrantedAuthoritiesFromClaims(Claims claims) throws SiteWhereException;
}