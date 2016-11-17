package com.sitewhere.spi.resource.request;

import java.io.Serializable;

import com.sitewhere.spi.resource.ResourceType;

/**
 * Request for creating a new resource.
 * 
 * @author Derek
 */
public interface IResourceCreateRequest extends Serializable {

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
}