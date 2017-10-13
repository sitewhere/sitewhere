package com.sitewhere.grpc.model.spi.security;

import org.springframework.security.core.Authentication;

/**
 * Extends Spring {@link Authentication} with ability to carry a tenant token.
 * 
 * @author Derek
 */
public interface ITenantAwareAuthentication extends Authentication {

    /**
     * Get tenant token.
     * 
     * @return
     */
    public String getTenantToken();

    /**
     * Set tenant token.
     * 
     * @param token
     */
    public void setTenantToken(String token);
}