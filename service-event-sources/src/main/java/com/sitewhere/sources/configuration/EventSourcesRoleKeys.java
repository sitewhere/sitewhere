/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.configuration;

import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

public enum EventSourcesRoleKeys implements IRoleKey {

    /** Event sources */
    EventSources("evt_srcs"),

    /** Event source */
    EventSource("evt_src"),

    /** Socket event source */
    SocketEventSource("sock_evt_src"),

    /** Socket interaction handler factory */
    SocketInteractionHandlerFactory("sock_int_fct"),

    /** WebSocket event source */
    WebSocketEventSource("ws_evt_src"),

    /** WebSocket event source header */
    WebSocketHeader("ws_header"),

    /** CoAP server event source */
    CoapServerEventSource("coap_svr_evt_src"),

    /** Binary event decoder */
    BinaryEventDecoder("bin_evt_dcd"),

    /** Composite event decoder */
    CompositeEventDecoder("cmp_evt_dcd"),

    /** Composite event decoder metadata extractor */
    CED_MetadataExtractor("ced_meta_ext"),

    /** Composite event decoder decoder choices */
    CED_DecoderChoices("ced_dcd_chcs"),

    /** Composite event decoder decoder choice */
    CED_DecoderChoice("ced_dcd_chc"),

    /** Event decoder */
    EventDecoder("evt_dcd"),

    /** String event decoder */
    StringEventDecoder("str_evt_dcd"),

    /** CoAP event decoder */
    CoapEventDecoder("coap_dcd"),

    /** Event deduplicator */
    EventDeduplicator("evt_dedup");

    private String id;

    private EventSourcesRoleKeys(String id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IRoleKey#getId()
     */
    @Override
    public String getId() {
	return id;
    }
}