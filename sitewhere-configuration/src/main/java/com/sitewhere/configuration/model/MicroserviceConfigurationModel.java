/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.model;

import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;

/**
 * Implementation of {@link IConfigurationModel} that pulls information from a
 * microservice.
 * 
 * @author Derek
 */
public abstract class MicroserviceConfigurationModel extends ConfigurationModel {

    /** Microservice */
    private IMicroservice microservice;

    /**
     * Create configuration model for a microservice.
     * 
     * @param microservice
     * @param role
     * @param icon
     * @param description
     */
    public MicroserviceConfigurationModel(IMicroservice microservice, String role, String icon, String description) {
	this.microservice = microservice;
	setLocalName(getMicroservice().getIdentifier());
	setName(getMicroservice().getName());
	addElements();
    }

    /**
     * Add elements contained in model.
     */
    public abstract void addElements();

    public IMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroservice microservice) {
	this.microservice = microservice;
    }
}