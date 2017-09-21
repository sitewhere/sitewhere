package com.sitewhere.web.security.jwt;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.sitewhere.spi.SiteWhereException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenUtils {

    /** Token issuer */
    private static final String ISSUER = "sitewhere";

    /** Secret used for encoding */
    private String secret = "secret";

    /** Token expiration in minutes */
    private int expirationInMinutes = 10;

    /** Signature algorithm */
    private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    /**
     * Get username from an existing token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public String getUsernameFromToken(String token) throws SiteWhereException {
	return getClaimsForToken(token).getSubject();
    }

    /**
     * Generate a new token for the given username.
     * 
     * @param username
     * @return
     * @throws SiteWhereException
     */
    public String generateToken(String username) throws SiteWhereException {
	try {
	    return Jwts.builder().setIssuer(ISSUER).setSubject(username).setIssuedAt(new Date())
		    .setExpiration(getExpirationDate()).signWith(SIGNATURE_ALGORITHM, getSecret()).compact();
	} catch (Throwable t) {
	    throw new SiteWhereException("Unable to generate JWT.", t);
	}
    }

    /**
     * Get claims for an existing token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public Claims getClaimsForToken(String token) throws SiteWhereException {
	try {
	    return Jwts.parser().setSigningKey(getSecret()).parseClaimsJws(token).getBody();
	} catch (Throwable t) {
	    throw new SiteWhereException("Unable to parse JWT.", t);
	}
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