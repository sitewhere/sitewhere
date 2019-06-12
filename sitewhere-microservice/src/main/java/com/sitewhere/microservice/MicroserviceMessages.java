/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

/**
 * Localized messages for microservice core classes.
 */
@BaseName("microservice")
@LocaleData({ @Locale("en_US") })
public enum MicroserviceMessages {

    INSTANCE_BOOTSTRAP_CONFIRMED,

    INSTANCE_BOOTSTRAP_MARKER_NOT_FOUND,

    INSTANCE_VERIFY_BOOTSTRAPPED,

    LIFECYCLE_STATUS_FAILED_NO_KAFKA,

    LIFECYCLE_STATUS_SEND_EXCEPTION,

    LIFECYCLE_STATUS_SENT,

    METRICS_REPORTING_DISABLED;
}
