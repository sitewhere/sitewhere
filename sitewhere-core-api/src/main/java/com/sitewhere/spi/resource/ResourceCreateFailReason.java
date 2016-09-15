package com.sitewhere.spi.resource;

/**
 * Enumeration of reasons for resource creation failures.
 * 
 * @author Derek
 */
public enum ResourceCreateFailReason {

    /** Resource already exists and overwrite not enabled */
    ResourceExists,

    /** Unable to store resource on underlying medium */
    StorageFailure;
}