/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.commands.spi.microservice;

import com.sitewhere.commands.configuration.CommandDeliveryTenantConfiguration;
import com.sitewhere.commands.spi.ICommandDestinationsManager;
import com.sitewhere.commands.spi.ICommandProcessingStrategy;
import com.sitewhere.commands.spi.IOutboundCommandRouter;
import com.sitewhere.commands.spi.kafka.IEnrichedCommandInvocationsPipeline;
import com.sitewhere.commands.spi.kafka.IUndeliveredCommandInvocationsProducer;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Extends {@link IMicroserviceTenantEngine} with features specific to command
 * delivery.
 */
public interface ICommandDeliveryTenantEngine extends IMicroserviceTenantEngine<CommandDeliveryTenantConfiguration> {

    /**
     * Get command processing strategy.
     * 
     * @return
     */
    public ICommandProcessingStrategy getCommandProcessingStrategy();

    /**
     * Get outbound command router.
     * 
     * @return
     */
    public IOutboundCommandRouter getOutboundCommandRouter();

    /**
     * Get manager that executes command destination logic.
     * 
     * @return
     */
    public ICommandDestinationsManager getCommandDestinationsManager();

    /**
     * Get Kafka Streams pipeline for command invocations.
     * 
     * @return
     */
    public IEnrichedCommandInvocationsPipeline getEnrichedCommandInvocationsPipeline();

    /**
     * Get Kafka producer for undelivered command invocations.
     * 
     * @return
     */
    public IUndeliveredCommandInvocationsProducer getUndeliveredCommandInvocationsProducer();
}