package com.sitewhere.spi.server.lifecycle;

import java.util.List;

/**
 * Offers operations for a hierarchy of nested lifecycle components.
 * 
 * @author Derek
 */
public interface ILifecycleHierarchyRoot {

    /**
     * Get list of components that have registered to participate in the server
     * component lifecycle.
     * 
     * @return
     */
    public List<ILifecycleComponent> getRegisteredLifecycleComponents();

    /**
     * Gets an {@link ILifecycleComponent} by unique id.
     * 
     * @param id
     * @return
     */
    public ILifecycleComponent getLifecycleComponentById(String id);
}