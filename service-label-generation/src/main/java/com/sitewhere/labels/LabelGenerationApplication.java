/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.sitewhere.labels.spi.microservice.ILabelGenerationMicroservice;
import com.sitewhere.microservice.MicroserviceApplication;

/**
 * Main application which runs the label generation microservice.
 */
@ApplicationScoped
public class LabelGenerationApplication extends MicroserviceApplication<ILabelGenerationMicroservice> {

    @Inject
    private ILabelGenerationMicroservice microservice;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public ILabelGenerationMicroservice getMicroservice() {
	return microservice;
    }
}