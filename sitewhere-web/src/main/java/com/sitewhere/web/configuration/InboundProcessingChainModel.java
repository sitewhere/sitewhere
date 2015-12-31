/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration;

import com.sitewhere.spring.handler.InboundProcessingChainParser;
import com.sitewhere.spring.handler.TenantConfigurationParser;
import com.sitewhere.web.configuration.model.ConfigurationModel;
import com.sitewhere.web.configuration.model.ElementNode;
import com.sitewhere.web.configuration.model.ElementRole;

/**
 * Configuration model for inbound processing chain elements.
 * 
 * @author Derek
 */
public class InboundProcessingChainModel extends ConfigurationModel {

	public InboundProcessingChainModel() {
		addElement(createInboundProcessingChain());
		addElement(createEventStorageProcessorElement());
		addElement(createRegistrationProcessorElement());
		addElement(createDeviceStreamProcessorElement());
	}

	/**
	 * Create the container for inbound processing chain configuration.
	 * 
	 * @return
	 */
	protected ElementNode createInboundProcessingChain() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Inbound Processors",
						TenantConfigurationParser.Elements.InboundProcessingChain.getLocalName(), "sign-in",
						ElementRole.InboundProcessingChain);
		builder.description("Configure a chain of processing steps that are applied to inbound data.");

		return builder.build();
	}

	/**
	 * Create element configuration event storage processor.
	 * 
	 * @return
	 */
	protected ElementNode createEventStorageProcessorElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Event Storage Processor",
						InboundProcessingChainParser.Elements.EventStorageProcessor.getLocalName(),
						"database", ElementRole.InboundProcessingChain_EventProcessor);

		builder.description("Persists incoming events into the datastore. If this processor is removed, "
				+ "events will not be stored and outbound processing will not be triggered for the events.");
		builder.warnOnDelete("Deleting this component will prevent events from being persisted!");

		return builder.build();
	}

	/**
	 * Create element configuration for registration processor.
	 * 
	 * @return
	 */
	protected ElementNode createRegistrationProcessorElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Registration Processor",
						InboundProcessingChainParser.Elements.RegistrationProcessor.getLocalName(), "key",
						ElementRole.InboundProcessingChain_EventProcessor);

		builder.description("Passes registration events to the registration manager. "
				+ "If this processor is removed, device registration events will be ignored.");
		builder.warnOnDelete("Deleting this component will cause registration events to be ignored!");

		return builder.build();
	}

	/**
	 * Create element configuration for device stream.
	 * 
	 * @return
	 */
	protected ElementNode createDeviceStreamProcessorElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Device Stream Processor",
						InboundProcessingChainParser.Elements.DeviceStreamProcessor.getLocalName(),
						"exchange", ElementRole.InboundProcessingChain_EventProcessor);

		builder.description("Passes device stream events to the device stream manager. "
				+ "If this processor is removed, device streaming events will be ignored.");
		builder.warnOnDelete("Deleting this component will cause device stream events to be ignored!");

		return builder.build();
	}
}