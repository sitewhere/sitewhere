/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.security;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.security.ITokenManagement;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * Manages creation and validation of JWT tokens.
 * 
 * @author Derek
 */
public class TokenManagement implements ITokenManagement {

    /** Token issuer */
    private static final String ISSUER = "sitewhere";

    /** Claim identifier for granted authorities */
    private static final String CLAIM_GRANTED_AUTHORITIES = "auth";

    /** Secret used for encoding */
    private String secret = "secret";

    /** Token expiration in minutes */
    private int expirationInMinutes = 60;

    /** Signature algorithm */
    private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.security.ITokenManagement#generateToken(
     * com.sitewhere.spi.user.IUser)
     */
    public String generateToken(IUser user) throws SiteWhereException {
	try {
	    JwtBuilder builder = Jwts.builder().setIssuer(ISSUER).setSubject(user.getUsername()).setIssuedAt(new Date())
		    .setExpiration(getExpirationDate()).signWith(SIGNATURE_ALGORITHM, getSecret());
	    builder.claim(CLAIM_GRANTED_AUTHORITIES, user.getAuthorities());
	    return builder.compact();
	} catch (Throwable t) {
	    throw new SiteWhereException("Unable to generate JWT.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.security.ITokenManagement#
     * getClaimsForToken(java.lang.String)
     */
    public Claims getClaimsForToken(String token) throws SiteWhereException {
	try {
	    return Jwts.parser().setSigningKey(getSecret()).parseClaimsJws(token).getBody();
	} catch (ExpiredJwtException e) {
	    throw new JwtExpiredException("JWT has expired.", e);
	} catch (UnsupportedJwtException e) {
	    throw new InvalidJwtException("JWT not in supported format.", e);
	} catch (MalformedJwtException e) {
	    throw new InvalidJwtException("JWT not correctly formatted.", e);
	} catch (Throwable t) {
	    throw new SiteWhereException("Error decoding JWT.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.security.ITokenManagement#
     * getUsernameFromToken(java.lang.String)
     */
    public String getUsernameFromToken(String token) throws SiteWhereException {
	return getUsernameFromClaims(getClaimsForToken(token));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.security.ITokenManagement#
     * getUsernameFromClaims(io.jsonwebtoken.Claims)
     */
    @Override
    public String getUsernameFromClaims(Claims claims) throws SiteWhereException {
	return claims.getSubject();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.security.ITokenManagement#
     * getGrantedAuthoritiesFromToken(java.lang.String)
     */
    public List<IGrantedAuthority> getGrantedAuthoritiesFromToken(String token) throws SiteWhereException {
	return getGrantedAuthoritiesFromClaims(getClaimsForToken(token));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.security.ITokenManagement#
     * getGrantedAuthoritiesFromClaims(io.jsonwebtoken.Claims)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<IGrantedAuthority> getGrantedAuthoritiesFromClaims(Claims claims) throws SiteWhereException {
	List<String> authIds = (List<String>) claims.get(CLAIM_GRANTED_AUTHORITIES, List.class);
	List<IGrantedAuthority> auths = new ArrayList<IGrantedAuthority>();
	for (String authId : authIds) {
	    GrantedAuthority auth = new GrantedAuthority();
	    auth.setAuthority(authId);
	    auths.add(auth);
	}
	return auths;
    }

    public Date getExpirationDate() {
	return new Date(System.currentTimeMillis() + (getExpirationInMinutes() * 60 * 1000));
    }

    public String getSecret() {
	return secret;
    }

    public void setSecret(String secret) {
	this.secret = secret;
    }

    public int getExpirationInMinutes() {
	return expirationInMinutes;
    }

    public void setExpirationInMinutes(int expirationInMinutes) {
	this.expirationInMinutes = expirationInMinutes;
    }
}