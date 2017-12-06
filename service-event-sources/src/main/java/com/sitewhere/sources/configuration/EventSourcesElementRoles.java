/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.configuration;

import com.sitewhere.configuration.model.ElementRole;
import com.sitewhere.configuration.model.IElementRoleProvider;

/**
 * Component roles associated with event sources microservice.
 * 
 * @author Derek
 */
public enum EventSourcesElementRoles implements IElementRoleProvider {

    /** Composite event decoder. Decoder choice. */
    CompositeEventDecoder_DecoderChoice(EventSourcesElementRole.COMPOSITE_EVENT_DECODER_CHOICE),

    /** Composite event decoder. Decoder choices. */
    CompositeEventDecoder_DecoderChoices(EventSourcesElementRole.COMPOSITE_EVENT_DECODER_CHOICES),

    /** Composite event decoder. Metadata extractor. */
    CompositeEventDecoder_MetadataExtractor(EventSourcesElementRole.COMPOSITE_EVENT_DECODER_METADATA_EXTRACTOR),

    /** Event source. Composite event decoder. */
    EventSource_CompositeEventDecoder(EventSourcesElementRole.EVENT_SOURCE_COMPOSITE_EVENT_DECODER),

    /** Event source. Binary event decoder. */
    EventSource_BinaryEventDecoder(EventSourcesElementRole.BINARY_EVENT_DECODER),

    /** Event source. String event decoder. */
    EventSource_StringEventDecoder(ElementRole.build("String Event Decoder", false, false, false)),

    /** Event source. Event decoder. */
    EventSource_EventDecoder(ElementRole.build("Event Decoder", false, false, false, new IElementRoleProvider[0],
	    new IElementRoleProvider[] { EventSource_BinaryEventDecoder, EventSource_StringEventDecoder })),

    /** Event source. Event deduplicator. */
    EventSource_EventDeduplicator(ElementRole.build("Event Deduplicator", true, false, false,
	    new IElementRoleProvider[0], new IElementRoleProvider[0])),

    /** Socket event source. Socket interaction handler factory. */
    EventSources_SocketInteractionHandlerFactory(
	    ElementRole.build("Socket Interaction Handler Factory", false, false, false)),

    /** Event sources container. Event source. */
    EventSources_SocketEventSource(ElementRole.build("Socket Event Source", true, true, true,
	    new IElementRoleProvider[] { EventSource_EventDecoder, EventSources_SocketInteractionHandlerFactory })),

    /** WebSocket event source. Header. */
    EventSources_WebSocketHeader(ElementRole.build("WebSocket Headers", true, true, true)),

    /** Event sources container. Event source. */
    EventSources_WebSocketEventSource(ElementRole.build("WebSocket Event Source", true, true, true,
	    new IElementRoleProvider[] { EventSource_EventDecoder, EventSources_WebSocketHeader })),

    /** Event sources container. Event source. */
    EventSources_EventSource(ElementRole.build("Event Sources", true, true, true,
	    new IElementRoleProvider[] { EventSource_EventDecoder, EventSource_EventDeduplicator },
	    new IElementRoleProvider[] { EventSources_SocketEventSource, EventSources_WebSocketEventSource })),

    /** Root event sources container. */
    EventSources(ElementRole.build(null, false, false, false, new IElementRoleProvider[] { EventSources_EventSource },
	    new IElementRoleProvider[0], true));

    // Wrapped role.
    private ElementRole role;

    private EventSourcesElementRoles(ElementRole role) {
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