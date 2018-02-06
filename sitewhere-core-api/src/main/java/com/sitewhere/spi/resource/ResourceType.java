/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.resource;

/**
 * Indicates type of resource.
 * 
 * @author Derek
 */
public enum ResourceType {

    /** SiteWhere configuration file */
    ConfigurationFile,

    /** Groovy script */
    GroovyScript,

    /** Unknown type */
    Unknown;
}