package com.sitewhere.spi.resource;

/**
 * Base interface for persistent system resources such as configuration files
 * and scripts.
 * 
 * @author Derek
 */
public interface IResource {

    /**
     * Get unique resource path.
     * 
     * @return
     */
    public String getPath();

    /**
     * Get type of resource.
     * 
     * @return
     */
    public ResourceType getResourceType();

    /**
     * Get resource content.
     * 
     * @return
     */
    public byte[] getContent();

    /**
     * Get last modified date for resource.
     * 
     * @return
     */
    public long getLastModified();
}