/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.media.microservice;

import com.sitewhere.media.spi.microservice.IStreamingMediaMicroservice;
import com.sitewhere.media.spi.microservice.IStreamingMediaTenantEngine;
import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Microservice that provides streaming media functionality.
 */
public class StreamingMediaMicroservice
	extends MultitenantMicroservice<MicroserviceIdentifier, IStreamingMediaTenantEngine>
	implements IStreamingMediaMicroservice {

    /** Microservice name */
    private static final String NAME = "Streaming Media";

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getName()
     */
    @Override
    public String getName() {
	return NAME;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getIdentifier()
     */
    @Override
    public MicroserviceIdentifier getIdentifier() {
	return MicroserviceIdentifier.StreamingMedia;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#isGlobal()
     */
    @Override
    public boolean isGlobal() {
	return false;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public IStreamingMediaTenantEngine createTenantEngine(ITenant tenant) throws SiteWhereException {
	return new StreamingMediaTenantEngine(tenant);
    }
}