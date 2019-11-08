/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.warp10;

import com.sitewhere.configuration.instance.warp10db.Warp10Configuration;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.server.lifecycle.parameters.StringComponentParameter;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

public class Warp10DbClient extends TenantEngineLifecycleComponent implements IDiscoverableTenantLifecycleComponent {

    /** InfluxDB configuration parameters */
    private Warp10Configuration configuration;

    /** Hostname parameter */
    private ILifecycleComponentParameter<String> hostname;

    public Warp10DbClient(Warp10Configuration configuration) {
        this.configuration = configuration;
    }

    /*
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initializeParameters()
     */
    @Override
    public void initializeParameters() throws SiteWhereException {
        // Add hostname.
        this.hostname = StringComponentParameter.newBuilder(this, "Hostname").value(getConfiguration().getHostname())
         .makeRequired().build();
        getParameters().add(hostname);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
        super.start(monitor);
        //TODO: aca deberia generar la instancia del cliente de warp 10 que voy a construir!!!
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    public Warp10Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Warp10Configuration configuration) {
        this.configuration = configuration;
    }
}
