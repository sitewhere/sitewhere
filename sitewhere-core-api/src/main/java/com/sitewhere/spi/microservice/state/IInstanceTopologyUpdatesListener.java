/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

/**
 * Listener interested in updates to the instance topology as microservices and
 * tenant engines are added or removed.
 */
public interface IInstanceTopologyUpdatesListener {

    /**
     * Called when a new microservice is added to the topology.
     * 
     * @param microservice
     */
    public void onMicroserviceAdded(IMicroserviceState microservice);

    /**
     * Called when a microservice is updated.
     * 
     * @param previous
     * @param updated
     */
    public void onMicroserviceUpdated(IMicroserviceState previous, IMicroserviceState updated);

    /**
     * Called when a microservice is removed from the topology.
     * 
     * @param microservice
     */
    public void onMicroserviceRemoved(IMicroserviceState microservice);

    /**
     * Called when a tenant engine is added to a microservice.
     * 
     * @param microservice
     * @param tenantEngine
     */
    public void onTenantEngineAdded(IMicroserviceState microservice, ITenantEngineState tenantEngine);

    /**
     * Called when a tenant engine is updated.
     * 
     * @param microservice
     * @param previous
     * @param updated
     */
    public void onTenantEngineUpdated(IMicroserviceState microservice, ITenantEngineState previous,
	    ITenantEngineState updated);

    /**
     * Called when a tenant engine is removed from a microservice.
     * 
     * @param microservice
     * @param tenantEngine
     */
    public void onTenantEngineRemoved(IMicroserviceState microservice, ITenantEngineState tenantEngine);
}