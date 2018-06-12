/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

/**
 * Localized messages for gRPC client classes.
 * 
 * @author Derek
 */
@BaseName("grpc-client")
@LocaleData({ @Locale("en_US") })
public enum GrpcClientMessages {

    API_CHANNEL_ALREADY_ACTIVE,

    API_CHANNEL_EXCEPTION_ON_CREATE,

    API_CHANNEL_FAILED_INIT,

    API_CHANNEL_FAILED_START,

    API_CHANNEL_INIT_AFTER_MS_ADDED,

    API_CHANNEL_INTERRUPTED_WAITING_FOR_MS,

    API_CHANNEL_REMOVED_AFTER_MS_REMOVED,

    API_CHANNEL_UNABLE_TO_INIT,

    API_CHANNEL_UNABLE_TO_REMOVE,

    API_CHANNEL_UNHANDLED_EXCEPTION_ON_CREATE,

    API_CHANNEL_WAITING_FOR_AVAILABLE;
}