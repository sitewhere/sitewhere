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
package com.sitewhere.commands.kafka;

import java.util.UUID;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

import com.sitewhere.commands.spi.ICommandProcessingStrategy;
import com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine;
import com.sitewhere.grpc.event.EventModelConverter;
import com.sitewhere.grpc.model.DeviceEventModel.GProcessedEventPayload;
import com.sitewhere.microservice.kafka.ProcessorSupplierComponent;
import com.sitewhere.rest.model.device.event.kafka.ProcessedEventPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Takes command invocation events from the pipeline and applies the command
 * processing strategy.
 */
public class CommandInvocationsProcessorSupplier extends ProcessorSupplierComponent<UUID, GProcessedEventPayload> {

    /*
     * @see org.apache.kafka.streams.processor.ProcessorSupplier#get()
     */
    @Override
    public Processor<UUID, GProcessedEventPayload> get() {
	return new Processor<UUID, GProcessedEventPayload>() {

	    @SuppressWarnings("unused")
	    private ProcessorContext context;

	    /*
	     * @see
	     * org.apache.kafka.streams.processor.Processor#init(org.apache.kafka.streams.
	     * processor.ProcessorContext)
	     */
	    @Override
	    public void init(ProcessorContext context) {
		this.context = context;
	    }

	    /*
	     * @see org.apache.kafka.streams.processor.Processor#process(java.lang.Object,
	     * java.lang.Object)
	     */
	    @Override
	    public void process(UUID key, GProcessedEventPayload event) {
		try {
		    // Convert payload to API object.
		    ProcessedEventPayload payload = EventModelConverter.asApiProcessedEventPayload(event);

		    // Pass decoded payload to processing strategy implementation.
		    ICommandProcessingStrategy strategy = ((ICommandDeliveryTenantEngine) getTenantEngine())
			    .getCommandProcessingStrategy();
		    IDeviceCommandInvocation invocation = (IDeviceCommandInvocation) payload.getEvent();
		    strategy.deliverCommand(payload.getEventContext(), invocation);
		}
		// TODO: Push errors to well-known topics.
		catch (SiteWhereException e) {
		    getLogger().error("Unable to process inbound event payload.", e);
		} catch (Throwable e) {
		    getLogger().error("Unhandled exception processing inbound event payload.", e);
		}
	    }

	    /*
	     * @see org.apache.kafka.streams.processor.Processor#close()
	     */
	    @Override
	    public void close() {
	    }
	};
    }
}
