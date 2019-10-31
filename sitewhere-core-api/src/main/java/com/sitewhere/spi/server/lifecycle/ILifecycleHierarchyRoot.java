/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.lifecycle;

import java.util.List;

/**
 * Offers operations for a hierarchy of nested lifecycle components.
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