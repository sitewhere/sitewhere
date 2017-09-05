package com.sitewhere.spi.resource;

/**
 * Creation mode for
 * 
 * @author Derek
 *
 */
public enum ResourceCreateMode {

    /** Fail if resource path already used */
    FAIL_IF_EXISTS,

    /** Overwrite if resource path already used */
    OVERWRITE,

    /** Push new version if path already used */
    PUSH_NEW_VERSION;
}
