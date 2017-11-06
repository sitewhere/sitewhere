/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Allows code to be run in a separate thread along with thread local security
 * credentials for the superuser account. This allows non-authenticated services
 * to interact with GRPC persistence APIs.
 * 
 * @author Derek
 */
public abstract class SystemUserRunnable implements Runnable {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Tenant engine if tenant operation */
    private IMicroserviceTenantEngine tenantEngine;

    public SystemUserRunnable(IMicroserviceTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
    }

    /**
     * Implemented in subclasses to specifiy code that should be run as the
     * system user.
     * 
     * @throws SiteWhereException
     */
    public abstract void runAsSystemUser() throws SiteWhereException;

    /*
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
	Authentication previous = SecurityContextHolder.getContext().getAuthentication();
	try {
	    Authentication system = getTenantEngine().getMicroservice().getSystemUser()
		    .getAuthenticationForTenant(getTenantEngine().getTenant());
	    SecurityContextHolder.getContext().setAuthentication(system);
	    runAsSystemUser();
	} catch (Throwable e) {
	    LOGGER.error("Unhandled exception.", e);
	} finally {
	    SecurityContextHolder.getContext().setAuthentication(previous);
	}
    }

    public IMicroserviceTenantEngine getTenantEngine() {
	return tenantEngine;
    }

    public void setTenantEngine(IMicroserviceTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
    }
}