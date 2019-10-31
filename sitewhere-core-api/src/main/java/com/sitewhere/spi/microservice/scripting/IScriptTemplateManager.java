/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.scripting;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Component that manages the list of script templates that are available for a
 * microservice.
 */
public interface IScriptTemplateManager extends ILifecycleComponent {

    /**
     * Get list of templates that provide examples of various types of scripts.
     * 
     * @return
     * @throws SiteWhereException
     */
    public List<IScriptTemplate> getScriptTemplates() throws SiteWhereException;

    /**
     * Get content for a script template.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public byte[] getScriptTemplateContent(String id) throws SiteWhereException;
}