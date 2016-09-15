package com.sitewhere.spi.resource;

import java.util.List;

/**
 * Indicates status of creating multiple resources.
 * 
 * @author Derek
 */
public interface IMultiResourceCreateResponse {

    /**
     * Get list of created resources.
     * 
     * @return
     */
    public List<IResource> getCreatedResources();

    /**
     * Get list of errors creating resources.
     * 
     * @return
     */
    public List<IResourceCreateError> getErrors();
}