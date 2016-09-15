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