/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.media.microservice;

import com.sitewhere.media.configuration.StreamingMediaConfiguration;
import com.sitewhere.media.spi.microservice.IStreamingMediaMicroservice;
import com.sitewhere.media.spi.microservice.IStreamingMediaTenantEngine;
import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Microservice that provides streaming media functionality.
 */
public class StreamingMediaMicroservice extends
	MultitenantMicroservice<MicroserviceIdentifier, StreamingMediaConfiguration, IStreamingMediaTenantEngine>
	implements IStreamingMediaMicroservice {

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getName()
     */
    @Override
    public String getName() {
	return "Streaming Media";
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getIdentifier()
     */
    @Override
    public MicroserviceIdentifier getIdentifier() {
	return MicroserviceIdentifier.StreamingMedia;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getConfigurationClass()
     */
    @Override
    public Class<StreamingMediaConfiguration> getConfigurationClass() {
	return StreamingMediaConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine)
     */
    @Override
    public IStreamingMediaTenantEngine createTenantEngine(SiteWhereTenantEngine engine) throws SiteWhereException {
	return new StreamingMediaTenantEngine(engine);
    }
}