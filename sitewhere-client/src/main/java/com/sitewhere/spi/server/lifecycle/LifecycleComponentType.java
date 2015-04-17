/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.lifecycle;

/**
 * Enumerates types of components in the system.
 * 
 * @author Derek
 */
public enum LifecycleComponentType {

	/** Includes the entire system */
	System,

	/** Data store management */
	DataStore,

	/** Cache provider */
	CacheProvider,

	/** Asset module manager */
	AssetModuleManager,

	/** Asset module */
	AssetModule,

	/** Search provider manager */
	SearchProviderManager,

	/** Search provider */
	SearchProvider,

	/** Outbound processor chain */
	OutboundProcessorChain,

	/** Outbound event processor */
	OutboundEventProcessor,

	/** Inbound processor chain */
	InboundProcessorChain,

	/** Inbound event processor */
	InboundEventProcessor,

	/** Device communication subsystem */
	DeviceCommunication,

	/** Command processing strategy */
	CommandProcessingStrategy,

	/** Command destination */
	CommandDestination,

	/** Command execution builder */
	CommandExecutionBuilder,

	/** Command execution encoder */
	CommandExecutionEncoder,

	/** Command target resolver */
	CommandTargetResolver,

	/** Command delivery provider */
	CommandDeliveryProvider,

	/** Command router */
	CommandRouter,

	/** Outbound processing strategy */
	OutboundProcessingStrategy,

	/** Registration manager */
	RegistrationManger,

	/** Batch operation manager */
	BatchOperationManager,
	
	/** Device stream manager */
	DeviceStreamManager,

	/** Inbound processing strategy */
	InboundProcessingStrategy,

	/** Event source */
	InboundEventSource,

	/** Event receiver */
	InboundEventReceiver,

	/** Unclassified component */
	Other,
}