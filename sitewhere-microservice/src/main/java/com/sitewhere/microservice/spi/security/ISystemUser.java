package com.sitewhere.microservice.spi.security;

import org.springframework.security.core.Authentication;

import com.sitewhere.spi.SiteWhereException;

/**
 * Support access to a global "superuser" for authorizing calls between
 * microservices.
 * 
 * @author Derek
 */
public interface ISystemUser {

    /**
     * Get authentication information for superuser.
     * 
     * @return
     * @throws SiteWhereException
     */
    public Authentication getAuthentication() throws SiteWhereException;
}