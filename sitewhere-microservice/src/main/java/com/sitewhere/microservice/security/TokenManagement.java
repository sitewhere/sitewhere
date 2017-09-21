package com.sitewhere.microservice.security;

import java.util.Date;
import java.util.List;

import com.sitewhere.microservice.spi.security.ITokenManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.IUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

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
    private int expirationInMinutes = 10;

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
	} catch (Throwable t) {
	    throw new SiteWhereException("Unable to parse JWT.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.security.ITokenManagement#
     * getUsernameFromToken(java.lang.String)
     */
    public String getUsernameFromToken(String token) throws SiteWhereException {
	return getClaimsForToken(token).getSubject();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.security.ITokenManagement#
     * getGrantedAuthoritiesFromToken(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<String> getGrantedAuthoritiesFromToken(String token) throws SiteWhereException {
	return (List<String>) getClaimsForToken(token).get(CLAIM_GRANTED_AUTHORITIES, List.class);
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