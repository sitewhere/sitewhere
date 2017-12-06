/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.configuration;

import com.sitewhere.rest.model.configuration.ElementRole;
import com.sitewhere.spi.microservice.configuration.model.IElementRoleProvider;

public class EventSourcesElementRole {

    public static final ElementRole BINARY_EVENT_DECODER = ElementRole.build("Binary Event Decoder", false, false,
	    false, new IElementRoleProvider[0],
	    new IElementRoleProvider[] { EventSourcesElementRoles.EventSource_CompositeEventDecoder }, false);

    public static final ElementRole COMPOSITE_EVENT_DECODER_CHOICE = ElementRole.build("Composite Event Decoder Choice",
	    true, true, true, new IElementRoleProvider[] { EventSourcesElementRoles.EventSource_BinaryEventDecoder });

    public static final ElementRole COMPOSITE_EVENT_DECODER_CHOICES = ElementRole.build(
	    "Composite Event Decoder Choices", false, false, false,
	    new IElementRoleProvider[] { EventSourcesElementRoles.CompositeEventDecoder_DecoderChoice },
	    new IElementRoleProvider[0], true);

    public static final ElementRole COMPOSITE_EVENT_DECODER_METADATA_EXTRACTOR = ElementRole.build("Metadata Extractor",
	    false, false, false);

    public static final ElementRole EVENT_SOURCE_COMPOSITE_EVENT_DECODER = ElementRole.build("Composite Event Decoder",
	    false, false, false,
	    new IElementRoleProvider[] { EventSourcesElementRoles.CompositeEventDecoder_MetadataExtractor,
		    EventSourcesElementRoles.CompositeEventDecoder_DecoderChoices });
}