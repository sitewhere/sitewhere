/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels;

import com.sitewhere.labels.spi.microservice.ILabelGenerationMicroservice;
import com.sitewhere.microservice.MicroserviceApplication;

/**
 * Spring Boot application for label generation microservice.
 */
public class LabelGenerationApplication extends MicroserviceApplication<ILabelGenerationMicroservice> {

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