/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.groovy.device.communication;

/**
 * Variables injected into Groovy decoders.
 * 
 * @author Derek
 */
public interface IGroovyVariables {

    /** Groovy variable used for device management implementaton */
    public static final String VAR_DEVICE_MANAGEMENT = "deviceManagement";

    /** Groovy variable used for decoded events */
    public static final String VAR_DECODED_EVENTS = "events";

    /** Groovy variable used for passing payload */
    public static final String VAR_PAYLOAD = "payload";

    /** Groovy variable used for passing payload metadata */
    public static final String VAR_PAYLOAD_METADATA = "metadata";

    /** Groovy variable used for passing logger */
    public static final String VAR_LOGGER = "logger";

    /** Groovy variable used for passing command execution */
    public static final String VAR_COMMAND_EXCUTION = "command";

    /** Groovy variable used for passing system command */
    public static final String VAR_SYSTEM_COMMAND = "system";

    /** Groovy variable used for passing command (or command execution) */
    public static final String VAR_NESTING_CONTEXT = "nesting";

    /** Groovy variable used for passing assignment */
    public static final String VAR_ASSIGNMENT = "assignment";
}