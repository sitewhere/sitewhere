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
 */
public enum LifecycleComponentType {

    /** Includes the entire system */
    System,

    /** Engine for a single tenant */
    TenantEngine,

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

    /** Outbound connector */
    OutboundConnector,

    /** Outbound event processor filter */
    OutboundEventProcessorFilter,

    /** Inbound event processor */
    InboundEventProcessor,

    /** Device communication subsystem */
    DeviceCommunication,

    /** Event processing subsystem */
    EventProcessing,

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

    /** Command parameter extractor */
    CommandParameterExtractor,

    /** Command router */
    CommandRouter,

    /** Outbound processing strategy */
    OutboundProcessingStrategy,

    /** Registration manager */
    RegistrationManager,

    /** Label generator manager */
    LabelGeneratorManager,

    /** Label generator */
    LabelGenerator,

    /** Batch operation manager */
    BatchOperationManager,

    /** Device presence manager */
    DevicePresenceManager,

    /** Device stream manager */
    DeviceStreamManager,

    /** Schedule manager */
    ScheduleManager,

    /** Inbound processing strategy */
    InboundProcessingStrategy,

    /** Event source */
    InboundEventSource,

    /** Device event decoder */
    DeviceEventDecoder,

    /** Device event deduplicator */
    DeviceEventDeduplicator,

    /** Event receiver */
    InboundEventReceiver,

    /** Resource manager */
    ResourceManager,

    /** Tenant template manager */
    TenantTemplateManager,

    /** Dataset template manager */
    DatasetTemplateManager,

    /** Script template manager */
    ScriptTemplateManager,

    /** Unclassified component */
    Other,
}