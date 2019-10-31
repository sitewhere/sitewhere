/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.groovy;

/**
 * Variables injected into Groovy decoders.
 */
public interface IGroovyVariables {

    /** Groovy variable used for tenant information */
    public static final String VAR_TENANT = "tenant";

    /** Groovy variable used for device management implementaton */
    public static final String VAR_DEVICE_MANAGEMENT = "deviceManagement";

    /** Groovy variable used for device information */
    public static final String VAR_DEVICE = "device";

    /** Groovy variable used for event information */
    public static final String VAR_EVENT = "event";

    /** Groovy variable used for event context information */
    public static final String VAR_EVENT_CONTEXT = "context";

    /** Groovy variable used for passing assignment */
    public static final String VAR_ASSIGNMENT = "assignment";

    /** Groovy variable used for passing active assignments */
    public static final String VAR_ACTIVE_ASSIGNMENTS = "assignments";

    /** Groovy variable used for device management builder */
    public static final String VAR_DEVICE_MANAGEMENT_BUILDER = "deviceBuilder";

    /** Groovy variable used for event management builder */
    public static final String VAR_EVENT_MANAGEMENT_BUILDER = "eventBuilder";

    /** Groovy variable used for asset management builder */
    public static final String VAR_ASSET_MANAGEMENT_BUILDER = "assetBuilder";

    /** Groovy variable used for tenant management builder */
    public static final String VAR_TENANT_MANAGEMENT_BUILDER = "tenantBuilder";

    /** Groovy variable used for decoded events */
    public static final String VAR_DECODED_EVENTS = "events";

    /** Groovy variable used for interacting with decoded device request */
    public static final String VAR_DECODED_DEVICE_REQUEST = "request";

    /** Groovy variable used for passing payload */
    public static final String VAR_PAYLOAD = "payload";

    /** Groovy variable used for passing payload metadata */
    public static final String VAR_PAYLOAD_METADATA = "metadata";

    /** Groovy variable used for a list of event payloads */
    public static final String VAR_EVENT_PAYLOADS = "payloads";

    /** Groovy variable used for passing a REST client */
    public static final String VAR_REST_CLIENT = "rest";

    /** Groovy variable used for passing logger */
    public static final String VAR_LOGGER = "logger";

    /** Groovy variable used for passing command execution */
    public static final String VAR_COMMAND_EXECUTION = "execution";

    /** Groovy variable used for passing system command */
    public static final String VAR_SYSTEM_COMMAND = "system";

    /** Groovy variable used for passing command (or command execution) */
    public static final String VAR_NESTING_CONTEXT = "nesting";
}