package com.sitewhere.instance.spi.microservice;

import com.sitewhere.instance.spi.templates.IInstanceTemplateManager;
import com.sitewhere.microservice.spi.IMicroservice;

/**
 * API for instance management microservice.
 * 
 * @author Derek
 */
public interface IInstanceManagementMicroservice extends IMicroservice {

    /**
     * Get instance template manager instance.
     * 
     * @return
     */
    public IInstanceTemplateManager getInstanceTemplateManager();
}
