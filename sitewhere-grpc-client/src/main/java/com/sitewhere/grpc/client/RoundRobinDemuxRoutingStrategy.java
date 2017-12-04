/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.sitewhere.grpc.client.spi.IApiDemuxRoutingStrategy;

/**
 * Routes to API channels using a round-robin strategy.
 * 
 * @author Derek
 */
@SuppressWarnings("rawtypes")
public class RoundRobinDemuxRoutingStrategy<T extends ApiChannel> implements IApiDemuxRoutingStrategy<T> {

    /** Index of API channel to use */
    private AtomicLong index = new AtomicLong(0);

    /*
     * @see
     * com.sitewhere.grpc.model.spi.IApiDemuxRoutingStrategy#chooseApiChannel(java.
     * util.List)
     */
    @Override
    public T chooseApiChannel(List<T> apiChannels) {
	if (apiChannels.size() == 0) {
	    throw new ApiChannelNotAvailableException();
	}
	long current = getIndex().incrementAndGet();
	int mod = (int) (current % apiChannels.size());
	return apiChannels.get(mod);
    }

    protected AtomicLong getIndex() {
	return index;
    }

    protected void setIndex(AtomicLong index) {
	this.index = index;
    }
}