/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice;

/**
 * Provides a list of known identifiers for microservices.
 * 
 * @author Derek
 */
public interface IMicroserviceIdentifiers {

    /** Asset management microservice */
    public static final String ASSET_MANAGEMENT = "asset-management";

    /** Batch operations microservice */
    public static final String BATCH_OPERATIONS = "batch-operations";

    /** Command destinations microservice */
    public static final String COMMAND_DESTINATIONS = "command-destinations";

    /** Device management microservice */
    public static final String DEVICE_MANAGEMENT = "device-management";

    /** Device registration microservice */
    public static final String DEVICE_REGISTRATION = "device-registration";

    /** Event management microservice */
    public static final String EVENT_MANAGEMENT = "event-management";

    /** Event search microservice */
    public static final String EVENT_SEARCH = "event-search";

    /** Event sources microservice */
    public static final String EVENT_SOURCES = "event-sources";

    /** Inbound processing microservice */
    public static final String INBOUND_PROCESSING = "inbound-processing";

    /** Instance management microservice */
    public static final String INSTANCE_MANAGEMENT = "instance-management";

    /** Label generation microservice */
    public static final String LABEL_GENERATION = "label-generation";

    /** Outbound processing microservice */
    public static final String OUTBOUND_PROCESSING = "outbound-processing";

    /** Presence management microservice */
    public static final String PRESENCE_MANAGEMENT = "presence-management";

    /** Rule processing microservice */
    public static final String RULE_PROCESSING = "rule-processing";

    /** Schedule management microservice */
    public static final String SCHEDULE_MANAGEMENT = "schedule-management";

    /** Tenant management microservice */
    public static final String TENANT_MANAGEMENT = "tenant-management";

    /** User management microservice */
    public static final String USER_MANAGEMENT = "user-management";

    /** Web/REST microservice */
    public static final String WEB_REST = "web-rest";
}