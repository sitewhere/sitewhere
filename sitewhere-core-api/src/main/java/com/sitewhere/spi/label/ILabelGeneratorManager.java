/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.label;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Manages a list of label generators.
 */
public interface ILabelGeneratorManager extends ITenantEngineLifecycleComponent {

    /**
     * Get the list of available label generators.
     * 
     * @return
     * @throws SiteWhereException
     */
    public List<ILabelGenerator> getLabelGenerators() throws SiteWhereException;

    /**
     * Get a label generator by id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public ILabelGenerator getLabelGenerator(String id) throws SiteWhereException;
}