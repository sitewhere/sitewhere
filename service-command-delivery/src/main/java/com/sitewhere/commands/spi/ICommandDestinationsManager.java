/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.spi;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.kafka.payload.IEnrichedEventPayload;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Manages command invocation logic.
 * 
 * @author Derek
 */
public interface ICommandDestinationsManager extends ITenantEngineLifecycleComponent {

    /**
     * Process a command invocation.
     * 
     * @param payload
     * @throws SiteWhereException
     */
    public void processCommandInvocation(IEnrichedEventPayload payload) throws SiteWhereException;
}