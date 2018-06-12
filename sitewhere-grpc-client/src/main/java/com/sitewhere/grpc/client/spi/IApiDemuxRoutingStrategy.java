/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.spi;

import java.util.Map;

/**
 * Get strategy used to demulitplex API calls across multiple
 * {@link IApiChannel}.
 * 
 * @author Derek
 */
@SuppressWarnings("rawtypes")
public interface IApiDemuxRoutingStrategy<T extends IApiChannel> {

    /**
     * Choose an API channel from the map of available channels.
     * 
     * @param apiChannels
     * @return
     */
    public T chooseApiChannel(Map<String, T> apiChannels);
}