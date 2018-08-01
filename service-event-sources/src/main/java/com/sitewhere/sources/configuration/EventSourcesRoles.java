/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.configuration;

import com.sitewhere.configuration.ConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Configuration roles available for event sources microservice.
 * 
 * @author Derek
 */
public enum EventSourcesRoles implements IConfigurationRoleProvider {

    /** Root event sources role. */
    EventSources(ConfigurationRole.build(EventSourcesRoleKeys.EventSources, "Event Sources", false, false, false,
	    new IRoleKey[] { EventSourcesRoleKeys.EventSource }, new IRoleKey[0], true)),

    /** Event sources container. Event source. */
    EventSource(ConfigurationRole.build(EventSourcesRoleKeys.EventSource, "Event Source", true, true, true,
	    new IRoleKey[] { EventSourcesRoleKeys.EventDecoder, EventSourcesRoleKeys.EventDeduplicator },
	    new IRoleKey[] { EventSourcesRoleKeys.SocketEventSource, EventSourcesRoleKeys.WebSocketEventSource,
		    EventSourcesRoleKeys.CoapServerEventSource })),

    /** Event sources container. Event source. */
    SocketEventSource(ConfigurationRole.build(EventSourcesRoleKeys.SocketEventSource, "Socket Event Source", true, true,
	    true, new IRoleKey[] { EventSourcesRoleKeys.EventDecoder,
		    EventSourcesRoleKeys.SocketInteractionHandlerFactory })),

    /** Socket event source. Socket interaction handler factory. */
    SocketInteractionHandlerFactory(ConfigurationRole.build(EventSourcesRoleKeys.SocketInteractionHandlerFactory,
	    "Socket Interaction Handler Factory", false, false, false)),

    /** Event sources container. Event source. */
    WebSocketEventSource(
	    ConfigurationRole.build(EventSourcesRoleKeys.WebSocketEventSource, "WebSocket Event Source", true, true,
		    true, new IRoleKey[] { EventSourcesRoleKeys.EventDecoder, EventSourcesRoleKeys.WebSocketHeader })),

    /** WebSocket event source. Header. */
    WebSocketHeader(
	    ConfigurationRole.build(EventSourcesRoleKeys.WebSocketHeader, "WebSocket Headers", true, true, true)),

    /** CoAP event source. */
    CoapServerEventSource(ConfigurationRole.build(EventSourcesRoleKeys.CoapServerEventSource,
	    "CoAP Server Event Source", true, true, true, new IRoleKey[] { EventSourcesRoleKeys.CoapEventDecoder })),

    /** Event source. Event decoder. */
    EventDecoder(ConfigurationRole.build(EventSourcesRoleKeys.EventDecoder, "Event Decoder", true, false, false,
	    new IRoleKey[0], new IRoleKey[] { EventSourcesRoleKeys.BinaryEventDecoder,
		    EventSourcesRoleKeys.StringEventDecoder, EventSourcesRoleKeys.CoapEventDecoder })),

    /** Binary event decoder */
    BinaryEventDecoder(ConfigurationRole.build(EventSourcesRoleKeys.BinaryEventDecoder, "Binary Event Decoder", true,
	    false, false, new IRoleKey[0], new IRoleKey[] { EventSourcesRoleKeys.CompositeEventDecoder }, false)),

    /** Composite event decoder */
    CompositeEventDecoder(ConfigurationRole.build(EventSourcesRoleKeys.CompositeEventDecoder, "Composite Event Decoder",
	    true, false, false,
	    new IRoleKey[] { EventSourcesRoleKeys.CED_MetadataExtractor, EventSourcesRoleKeys.CED_DecoderChoices })),

    /** Composite event decoder metadata extractor. */
    CED_MetadataExtractor(ConfigurationRole.build(EventSourcesRoleKeys.CED_MetadataExtractor, "Metadata Extractor",
	    false, false, false)),

    /** Composite event decoder choices. */
    CED_DecoderChoices(
	    ConfigurationRole.build(EventSourcesRoleKeys.CED_DecoderChoices, "Composite Event Decoder Choices", false,
		    false, false, new IRoleKey[] { EventSourcesRoleKeys.CED_DecoderChoice }, new IRoleKey[0], true)),

    /** Composite event decoder choice. */
    CED_DecoderChoice(ConfigurationRole.build(EventSourcesRoleKeys.CED_DecoderChoice, "Composite Event Decoder Choice",
	    true, true, true, new IRoleKey[] { EventSourcesRoleKeys.BinaryEventDecoder })),

    /** Event source. String event decoder. */
    StringEventDecoder(ConfigurationRole.build(EventSourcesRoleKeys.StringEventDecoder, "String Event Decoder", false,
	    false, false)),

    /** Event source. CoAP event decoder. */
    CoapEventDecoder(
	    ConfigurationRole.build(EventSourcesRoleKeys.CoapEventDecoder, "CoAP Event Decoder", true, false, false)),

    /** Event source. Event deduplicator. */
    EventDeduplicator(ConfigurationRole.build(EventSourcesRoleKeys.EventDeduplicator, "Event Deduplicator", true, false,
	    false, new IRoleKey[0], new IRoleKey[0]));

    private ConfigurationRole role;

    private EventSourcesRoles(ConfigurationRole role) {
	this.role = role;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider
     * #getRole()
     */
    @Override
    public IConfigurationRole getRole() {
	return role;
    }
}