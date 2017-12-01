/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.spi.IApiChannel;
import com.sitewhere.grpc.model.spi.IApiDemux;
import com.sitewhere.grpc.model.spi.IApiDemuxRoutingStrategy;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;

/**
 * Demulitiplexes API calls across multiple {@link IApiChannel} in order to
 * provide high availability and increased throughput.
 * 
 * @author Derek
 */
public abstract class ApiDemux<T extends IApiChannel<?>> extends TenantEngineLifecycleComponent implements IApiDemux {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** List of API channels */
    private List<T> apiChannels = new ArrayList<>();

    /** Routing strategy */
    private IApiDemuxRoutingStrategy<T> routingStrategy;

    /*
     * @see com.sitewhere.grpc.model.spi.IApiDemux#getApiChannel()
     */
    @Override
    public T getApiChannel() throws SiteWhereException {
	return getRoutingStrategy().chooseApiChannel(getApiChannels());
    }

    /*
     * @see
     * com.sitewhere.grpc.model.spi.IApiDemux#removeApiChannel(java.lang.String)
     */
    @Override
    public T removeApiChannel(String host) throws SiteWhereException {
	return null;
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IApiDemux#getApiChannels()
     */
    @Override
    public List<T> getApiChannels() {
	return apiChannels;
    }

    public void setApiChannels(List<T> apiChannels) {
	this.apiChannels = apiChannels;
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IApiDemux#getRoutingStrategy()
     */
    @Override
    public IApiDemuxRoutingStrategy<T> getRoutingStrategy() {
	return routingStrategy;
    }

    public void setRoutingStrategy(IApiDemuxRoutingStrategy<T> routingStrategy) {
	this.routingStrategy = routingStrategy;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}