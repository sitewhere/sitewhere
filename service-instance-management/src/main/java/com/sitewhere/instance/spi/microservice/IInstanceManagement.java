package com.sitewhere.instance.spi.microservice;

import com.sitewhere.instance.spi.templates.IInstanceTemplateManager;

/**
 * API for instance management microservice.
 * 
 * @author Derek
 */
public interface IInstanceManagement {

    /**
     * Get instance template manager instance.
     * 
     * @return
     */
    public IInstanceTemplateManager getInstanceTemplateManager();
}
