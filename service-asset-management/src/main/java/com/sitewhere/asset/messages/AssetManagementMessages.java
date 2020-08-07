/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.messages;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

/**
 * Localized messages for the asset managment microservice.
 */
@BaseName("asset-management")
@LocaleData({ @Locale("en_US") })
public enum AssetManagementMessages {

    ALL_REQUIRED_MS_AVAILABLE,

    DEVICE_MANAGEMENT_MS_AVAILABLE;
}