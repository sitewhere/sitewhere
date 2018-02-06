/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.spi.microservice;

import com.sitewhere.commands.spi.ICommandDestinationsManager;
import com.sitewhere.commands.spi.kafka.IEnrichedCommandInvocationsConsumer;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Extends {@link IMicroserviceTenantEngine} with features specific to command
 * delivery.
 * 
 * @author Derek
 */
public interface ICommandDeliveryTenantEngine extends IMicroserviceTenantEngine {

    /**
     * Get manager that executes command destination logic.
     * 
     * @return
     */
    public ICommandDestinationsManager getCommandDestinationsManager();

    /**
     * Get Kafka consumer for command invocations.
     * 
     * @return
     */
    public IEnrichedCommandInvocationsConsumer getEnrichedCommandInvocationsConsumer();
}