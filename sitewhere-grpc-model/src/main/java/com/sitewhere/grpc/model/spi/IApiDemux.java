/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.spi;

import java.util.List;

import com.sitewhere.grpc.model.client.ApiChannel;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Demulitiplexes API calls across multiple {@link IApiChannel} in order to
 * provide high availability and increased throughput.
 * 
 * @author Derek
 */
public interface IApiDemux<T extends IApiChannel<?>> extends ITenantEngineLifecycleComponent {

    /**
     * List of available {@link IApiChannel} that can be used for routing.
     * 
     * @return
     */
    public List<T> getApiChannels();

    /**
     * Get an API channel based on the routing strategy.
     * 
     * @return
     * @throws SiteWhereException
     */
    public T getApiChannel() throws SiteWhereException;

    /**
     * Create an {@link IApiChannel} to the given host.
     * 
     * @param host
     * @return
     * @throws SiteWhereException
     */
    public T createApiChannel(String host) throws SiteWhereException;

    /**
     * Remove API channel for the given host.
     * 
     * @param host
     * @throws SiteWhereException
     */
    public T removeApiChannel(String host) throws SiteWhereException;

    /**
     * Get strategy used for demulitplexing API calls across multiple
     * {@link ApiChannel}.
     * 
     * @return
     */
    public IApiDemuxRoutingStrategy getRoutingStrategy();
}