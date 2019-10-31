/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.configuration;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Management interface for interacting with instance configuration data.
 */
public interface IConfigurationManagement extends ITenantEngineLifecycleComponent {

    public byte[] getGlobalConfiguration(String instanceId, String moduleId) throws SiteWhereException;

    public void setGlobalConfiguration(String instanceId, String moduleId, byte[] payload) throws SiteWhereException;

    public byte[] getTenantConfiguration(String instanceId, String tenantId, String moduleId) throws SiteWhereException;

    public void setTenantConfiguration(String instanceId, String tenantId, String moduleId, byte[] payload)
	    throws SiteWhereException;
}