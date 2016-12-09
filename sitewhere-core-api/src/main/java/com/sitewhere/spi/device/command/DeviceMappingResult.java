package com.sitewhere.spi.device.command;

/**
 * Enumeration of results for device mapping operations.
 * 
 * @author Derek
 */
public enum DeviceMappingResult {

    /** Indicates a mapping was successfully created */
    MappingCreated,

    /** Indicates mapping not created since one already exists */
    MappingFailedDueToExisting;
}