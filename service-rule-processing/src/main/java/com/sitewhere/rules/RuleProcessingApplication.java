/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rules;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.rules.spi.microservice.IRuleProcessingMicroservice;

/**
 * Spring Boot application for rule processing microservice.
 * 
 * @author Derek
 */
public class RuleProcessingApplication extends MicroserviceApplication<IRuleProcessingMicroservice> {

    private IRuleProcessingMicroservice microservice;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IRuleProcessingMicroservice getMicroservice() {
	return microservice;
    }
}