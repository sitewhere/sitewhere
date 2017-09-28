package com.sitewhere.microservice.spi.security;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;

import io.jsonwebtoken.Claims;

/**
 * Allows for creating and validation of JWT tokens.
 * 
 * @author Derek
 */
public interface ITokenManagement {

    /**
     * Generate a token for the given user.
     * 
     * @param user
     * @return
     * @throws SiteWhereException
     */
    public String generateToken(IUser user) throws SiteWhereException;

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
     * Get granted authorities from given token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public List<IGrantedAuthority> getGrantedAuthoritiesFromToken(String token) throws SiteWhereException;
}