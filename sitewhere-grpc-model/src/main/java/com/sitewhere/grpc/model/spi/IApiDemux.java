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

/**
 * Demulitiplexes API calls across multiple {@link IApiChannel} in order to
 * provide high availability and increased throughput.
 * 
 * @author Derek
 */
public interface IApiDemux {

    /**
     * List of available {@link ApiChannel} that can be used for routing.
     * 
     * @return
     */
    public List<ApiChannel> getApiChannels();

    /**
     * Add an {@link ApiChannel}.
     * 
     * @param channel
     * @throws SiteWhereException
     */
    public IApiChannel createApiChannel(String host) throws SiteWhereException;

    /**
     * Remove an {@link ApiChannel}.
     * 
     * @param channel
     * @throws SiteWhereException
     */
    public void removeApiChannel(IApiChannel channel) throws SiteWhereException;

    /**
     * Get strategy used for demulitplexing API calls across multiple
     * {@link ApiChannel}.
     * 
     * @return
     */
    public IApiDemuxRoutingStrategy getRoutingStrategy();
}