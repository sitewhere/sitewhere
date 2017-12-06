/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.model;

import com.sitewhere.rest.model.configuration.ElementRole;
import com.sitewhere.spi.microservice.configuration.model.IElementRoleProvider;

public enum ElementRoles implements IElementRoleProvider {

    /** Top level element. */
    Globals_Global(ElementRole.build("Global", true, true, true)),

    /** Globals element. */
    Globals(ElementRole.build("Global Overrides", true, false, false, new ElementRoles[] { Globals_Global },
	    new ElementRoles[0], true)),

    /** Data management container. Datastore configuration. */
    DataManagement_Datastore(ElementRole.build("Datastore", false, false, false)),

    /** Data management container. Cache provider configuration. */
    DataManagement_CacheProvider(ElementRole.build("Cache Provider", true, false, false)),

    /** Inbound processing chain. Event processor. */
    InboundProcessingChain_EventProcessor(ElementRole.build("Event Processors", true, true, true)),

    /** Outbound processing chain. Processsor filters. */
    OutboundProcessingChain_OutboundFilters(ElementRole.build("Filters", true, true, true)),

    /** Outbound processing chain. Processsor filter criteria. */
    OutboundProcessingChain_Filters(ElementRole.build("Filter Criteria", true, false, false,
	    new ElementRoles[] { OutboundProcessingChain_OutboundFilters })),

    /** Outbound processing chain. Filtered event processor. */
    OutboundProcessingChain_FilteredEventProcessor(ElementRole.build("Filtered Event Processors", true, true, true,
	    new ElementRoles[] { OutboundProcessingChain_Filters })),

    /** Zone test event processsor. Zone test. */
    OutboundProcessingChain_ZoneTest(ElementRole.build("Zone Tests", true, true, true)),

    /** Outbound processing chain. Zone test event processor. */
    OutboundProcessingChain_ZoneTestEventProcessor(ElementRole.build("Zone Test Event Processor", true, true, true,
	    new ElementRoles[] { OutboundProcessingChain_ZoneTest, OutboundProcessingChain_Filters })),

    /** MQTT event processsor. Route builder. */
    OutboundProcessingChain_RouteBuilder(ElementRole.build("Route Builder", true, false, false)),

    /** Outbound processing chain. MQTT event processor. */
    OutboundProcessingChain_MqttEventProcessor(ElementRole.build("MQTT Event Processor", true, true, true,
	    new ElementRoles[] { OutboundProcessingChain_Filters, OutboundProcessingChain_RouteBuilder })),

    /** Outbound processing chain. RabbitMQ event processor. */
    OutboundProcessingChain_RabbitMqEventProcessor(ElementRole.build("RabbitMQ Event Processor", true, true, true,
	    new ElementRoles[] { OutboundProcessingChain_Filters, OutboundProcessingChain_RouteBuilder })),

    /** Siddhi query. Callback. */
    OutboundProcessingChain_SiddhiCallback(ElementRole.build("Siddhi Callbacks", true, true, true)),

    /** Siddhi event processsor. Siddhi query. */
    OutboundProcessingChain_SiddhiQuery(ElementRole.build("Siddhi Queries", true, true, true,
	    new ElementRoles[] { OutboundProcessingChain_SiddhiCallback })),

    /** Outbound processing chain. Siddhi event processor. */
    OutboundProcessingChain_SiddhiEventProcessor(ElementRole.build("Siddhi Event Processor", true, true, true,
	    new ElementRoles[] { OutboundProcessingChain_SiddhiQuery, OutboundProcessingChain_Filters })),

    /** Outbound processing chain. Event processor. */
    OutboundProcessingChain_EventProcessor(ElementRole.build("Event Processors", true, true, true, new ElementRoles[0],
	    new ElementRoles[] { OutboundProcessingChain_FilteredEventProcessor,
		    OutboundProcessingChain_ZoneTestEventProcessor, OutboundProcessingChain_MqttEventProcessor,
		    OutboundProcessingChain_RabbitMqEventProcessor, OutboundProcessingChain_SiddhiEventProcessor })),

    /** Symbol generator manager. Symbol generator. */
    SymbolGeneratorManager_SymbolGenerator(ElementRole.build("Symbol Generator", true, true, true)),

    /** Device services container. Symbol generator manager. */
    DeviceServices_SymbolGeneratorManager(ElementRole.build("Symbol Generator Manager", true, false, false,
	    new ElementRoles[] { SymbolGeneratorManager_SymbolGenerator })),

    /** Device services container. Registration manager. */
    DeviceServices_RegistrationManager(ElementRole.build("Registration Manager", false, false, false)),

    /** Device services container. Presence manager. */
    DeviceServices_PresenceManager(ElementRole.build("Presence Manager", true, false, false)),

    /** Device communication container. Device Services. */
    DeviceCommunication_DeviceServices(ElementRole.build(
	    null, false, false, false, new ElementRoles[] { DeviceServices_RegistrationManager,
		    DeviceServices_SymbolGeneratorManager, DeviceServices_PresenceManager },
	    new ElementRoles[0], true)),

    /** Batch operations container. Batch operation manager. */
    BatchOperations_BatchOperationManager(ElementRole.build("Batch Operation Manager", false, false, false)),

    /** Device communication container. Batch operations. */
    DeviceCommunication_BatchOperations(ElementRole.build(null, false, false, false,
	    new ElementRoles[] { BatchOperations_BatchOperationManager }, new ElementRoles[0], true)),

    /** Specification mapping router. Mapping. */
    CommandRouting_SpecificationMappingRouter_Mapping(ElementRole.build("Mappings", true, true, true)),

    /** Specification mapping router. Mapping. */
    CommandRouting_SpecificationMappingRouter(ElementRole.build("Specification Mapping Router", false, false, false,
	    new ElementRoles[] { CommandRouting_SpecificationMappingRouter_Mapping })),

    /** Command routing container. Command router implementation. */
    CommandRouting_CommandRouter(ElementRole.build("Command Router", false, false, false, new ElementRoles[0],
	    new ElementRoles[] { CommandRouting_SpecificationMappingRouter })),

    /** Device communication container. Command routing configuration. */
    DeviceCommunication_CommandRouting(ElementRole.build(null, false, false, false,
	    new ElementRoles[] { CommandRouting_CommandRouter }, new ElementRoles[0], true)),

    /** Command destination. String command encoder. */
    CommandDestinations_StringCommandEncoder(ElementRole.build("String Command Encoder", false, false, false)),

    /** Command destination. Binary command encoder. */
    CommandDestinations_BinaryCommandEncoder(ElementRole.build("Binary Command Encoder", false, false, false)),

    /** Command destination. Command encoder. */
    CommandDestinations_CommandEncoder(ElementRole.build("Command Encoder", false, false, false, new ElementRoles[0],
	    new ElementRoles[] { CommandDestinations_BinaryCommandEncoder, CommandDestinations_StringCommandEncoder })),

    /** Command destination. MQTT parameter extractor. */
    CommandDestinations_MqttParameterExtractor(ElementRole.build("MQTT Parameter Extractor", false, false, false)),

    /** Command destination. MQTT parameter extractor. */
    CommandDestinations_SmsParameterExtractor(ElementRole.build("SMS Parameter Extractor", false, false, false)),

    /** Command destination. CoAP parameter extractor. */
    CommandDestinations_CoapParameterExtractor(ElementRole.build("CoAP Parameter Extractor", false, false, false)),

    /** Command destination. Parameter extractor. */
    CommandDestinations_ParameterExtractor(ElementRole.build("Parameter Extractor", false, false, false,
	    new ElementRoles[0], new ElementRoles[] { CommandDestinations_MqttParameterExtractor,
		    CommandDestinations_SmsParameterExtractor, CommandDestinations_CoapParameterExtractor })),

    /** Command destinations. Command destination. */
    CommandDestinations_CommandDestination(ElementRole.build("Command Destinations", true, true, true,
	    new ElementRoles[] { CommandDestinations_CommandEncoder, CommandDestinations_ParameterExtractor })),

    /** Device communication container. Command destinations configuration. */
    DeviceCommunication_CommandDestinations(ElementRole.build(null, false, false, false,
	    new ElementRoles[] { CommandDestinations_CommandDestination }, new ElementRoles[0], true)),

    /** Asset Management. Asset module */
    AssetManagment_AssetModule(ElementRole.build("Asset Modules", true, true, true)),

    /** Asset Management element. */
    AssetManagment(ElementRole.build(null, false, false, false, new ElementRoles[] { AssetManagment_AssetModule },
	    new ElementRoles[0], true)),

    /** Search Providers. Search provider. */
    SearchProviders_SearchProvider(ElementRole.build("Search Providers", true, true, true)),

    /** Search providers element. */
    SearchProviders(ElementRole.build("Search Providers", true, false, false,
	    new ElementRoles[] { SearchProviders_SearchProvider }, new ElementRoles[0], true)),

    /** Top level element. */
    Root(ElementRole.build(null, false, false, false, new ElementRoles[] { Globals, AssetManagment, SearchProviders }));

    // Wrapped role.
    private ElementRole role;

    private ElementRoles(ElementRole role) {
	this.role = role;
    }

    /*
     * @see com.sitewhere.configuration.model.IElementRoleProvider#getElementRole()
     */
    public ElementRole getElementRole() {
	return role;
    }

    /*
     * @see com.sitewhere.configuration.model.IElementRoleProvider#getName()
     */
    @Override
    public String getName() {
	return name();
    }
}